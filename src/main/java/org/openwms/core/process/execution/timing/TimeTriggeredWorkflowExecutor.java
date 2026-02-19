/*
 * Copyright 2005-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openwms.core.process.execution.timing;

import jakarta.annotation.PostConstruct;
import org.openwms.core.process.execution.spi.ProgramExecutor;
import org.openwms.core.process.execution.timing.events.ConfigurationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * A TimeTriggeredWorkflowExecutor.
 *
 * @author Heiko Scherrer
 */
@Service
class TimeTriggeredWorkflowExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeTriggeredWorkflowExecutor.class);
    private final Map<String, ScheduledFuture<?>> scheduledFutureMap = new ConcurrentHashMap<>();
    private final ProgramExecutor programExecutor;
    private final TaskScheduler taskScheduler;
    private final TimerConfigurationService timerConfigurationService;

    TimeTriggeredWorkflowExecutor(ProgramExecutor programExecutor, TaskScheduler taskScheduler, TimerConfigurationService timerConfigurationService) {
        this.programExecutor = programExecutor;
        this.taskScheduler = taskScheduler;
        this.timerConfigurationService = timerConfigurationService;
    }

    @TransactionalEventListener
    public void onEvent(ConfigurationEvent configurationEvent) {
        switch (configurationEvent.getType()) {
            case CREATED -> {
                createAndSchedule((TimerConfiguration) configurationEvent.getSource());
                LOGGER.info("New TimerConfiguration created");
            }
            case UPDATED -> LOGGER.info("New TimerConfiguration updated");
            case DELETED -> {
                var pKey = (String) configurationEvent.getSource();
                for (var entries : scheduledFutureMap.entrySet()) {
                    if (pKey.equals(entries.getKey())) {
                        entries.getValue().cancel(true);
                        scheduledFutureMap.remove(pKey);
                        LOGGER.info("New TimerConfiguration deleted");
                        break;
                    }
                }
            }
            default -> LOGGER.warn("Unknown ConfigurationEvent type: {}", configurationEvent.getType());
        }
    }

    @PostConstruct
    public void execute() {
        var timerConfigurations = timerConfigurationService.loadConfigurations();
        timerConfigurations.forEach(this::createAndSchedule);
    }

    private void createAndSchedule(TimerConfiguration tc) {
        var cronTrigger = new CronTrigger(tc.getCronExpression());
        var scheduledFuture = taskScheduler.schedule(
                () -> programExecutor.execute(tc.getName(), new HashMap<>(tc.getRuntimeVariables())),
                cronTrigger);
        scheduledFutureMap.put(tc.getPersistentKey(), scheduledFuture);
    }
}

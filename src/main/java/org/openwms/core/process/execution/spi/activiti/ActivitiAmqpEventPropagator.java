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
package org.openwms.core.process.execution.spi.activiti;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.ameba.annotation.Measured;
import org.ameba.app.SpringProfiles;
import org.openwms.core.process.execution.api.WorkflowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * A ActivitiAmqpEventPropagator is receiving Activiti workflow execution events and sends out {@link WorkflowEvent}s over AMQP.
 *
 * @author Heiko Scherrer
 */
@Profile("ACTIVITI && " + SpringProfiles.ASYNCHRONOUS_PROFILE)
@Component
class ActivitiAmqpEventPropagator implements ActivitiEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger("AMQP_EVENTS");
    private final String exchangeName;
    private final AmqpTemplate amqpTemplate;
    private final ProcessEngine processEngine;

    ActivitiAmqpEventPropagator(@Value("${owms.process.execution.amqp.exchange-name}") String exchangeName, AmqpTemplate amqpTemplate, ProcessEngine processEngine) {
        this.exchangeName = exchangeName;
        this.amqpTemplate = amqpTemplate;
        this.processEngine = processEngine;
    }

    /**
     * {@inheritDoc}
     */
    @Measured
    @Override
    public void onEvent(ActivitiEvent event) {
        switch (event.getType()) {
            case ACTIVITY_STARTED, ACTIVITY_COMPLETED,
                 PROCESS_STARTED, PROCESS_COMPLETED, PROCESS_CANCELLED, PROCESS_COMPLETED_WITH_ERROR_END_EVENT,
                 JOB_EXECUTION_SUCCESS, JOB_CANCELED, JOB_EXECUTION_FAILURE:
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("Event: [{}, {}, {}, {}]", event.getType(), event.getExecutionId(), event.getProcessDefinitionId(),
                            event.getProcessInstanceId());
                }
                processEngine.getHistoryService().createHistoricProcessInstanceQuery().list().forEach(pi -> {
                    //System.out.println(pi.toString());
                });
                processEngine.getRuntimeService().createExecutionQuery().list().forEach(pi -> {
                    System.out.println(pi.toString());
                });
                amqpTemplate.convertAndSend(exchangeName, "execution.event."+event.getType().name(), new WorkflowEvent(event.getType().name(), event.getExecutionId(),
                        event.getProcessDefinitionId(),
                        event.getProcessInstanceId()));
                break;
            default:
                LOGGER.trace("Unhandled event received: [{}]", event.getType());
                processEngine.getHistoryService().createHistoricProcessInstanceQuery().list().forEach(pi -> {
                    //System.out.println(pi.toString());
                });
                processEngine.getRuntimeService().createExecutionQuery().list().forEach(pi -> {
                    //System.out.println(pi.toString());
                });
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFailOnException() {
        return false;
    }
}

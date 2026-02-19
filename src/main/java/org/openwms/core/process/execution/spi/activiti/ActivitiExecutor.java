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

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.repository.ProcessDefinition;
import org.ameba.annotation.Measured;
import org.openwms.core.process.execution.spi.AbstractExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static org.ameba.LoggingCategories.BOOT;

/**
 * A ActivitiExecutor delegates to Activiti for program execution.
 *
 * @author Heiko Scherrer
 */
@Profile("ACTIVITI")
@Component
class ActivitiExecutor extends AbstractExecutor<ProcessDefinition> {

    private static final Logger BOOT_LOGGER = LoggerFactory.getLogger(BOOT);
    private final RuntimeService runtimeService;
    private final ProcessInstanceExecutor processInstanceExecutor;
    private final RepositoryService repositoryService;
    private final List<ActivitiEventListener> eventListeners;

    ActivitiExecutor(RuntimeService runtimeService, ProcessInstanceExecutor processInstanceExecutor, RepositoryService repositoryService,
            @Autowired(required = false) List<ActivitiEventListener> eventListeners) {
        this.runtimeService = runtimeService;
        this.processInstanceExecutor = processInstanceExecutor;
        this.repositoryService = repositoryService;
        this.eventListeners = eventListeners;
        BOOT_LOGGER.info("-- w/ Activiti executor");
    }

    /**
     * {@inheritDoc}
     */
    @Measured
    @Override
    protected ProcessDefinition loadProcessDefinition(String processName) {
        return repositoryService.createProcessDefinitionQuery().processDefinitionKey(processName).active().latestVersion().singleResult();
    }

    /**
     * {@inheritDoc}
     */
    @Measured
    @Override
    protected void executeProcessDefinition(ProcessDefinition processDefinition, Map<String, Object> runtimeVariables) {
        processInstanceExecutor.execute(processDefinition, runtimeVariables);
        if (eventListeners != null) {
            for (var eventListener : eventListeners) {
                runtimeService.addEventListener(eventListener);
            }
        }
    }
}

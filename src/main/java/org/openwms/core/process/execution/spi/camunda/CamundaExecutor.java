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
package org.openwms.core.process.execution.spi.camunda;

import org.ameba.annotation.Measured;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.openwms.core.process.execution.spi.AbstractExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.ameba.LoggingCategories.BOOT;

/**
 * A CamundaExecutor delegates to Camunda for program execution.
 *
 * @author Heiko Scherrer
 */
@Profile("!FLOWABLE && !ACTIVITI")
@Component
class CamundaExecutor extends AbstractExecutor<ProcessDefinition> {

    private static final Logger BOOT_LOGGER = LoggerFactory.getLogger(BOOT);
    private final RuntimeService runtimeService;
    private final RepositoryService repositoryService;

    CamundaExecutor(RuntimeService runtimeService, RepositoryService repositoryService) {
        this.runtimeService = runtimeService;
        this.repositoryService = repositoryService;
        BOOT_LOGGER.info("-- w/ Camunda executor");
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
        runtimeService.startProcessInstanceById(processDefinition.getId(), runtimeVariables);
    }
}

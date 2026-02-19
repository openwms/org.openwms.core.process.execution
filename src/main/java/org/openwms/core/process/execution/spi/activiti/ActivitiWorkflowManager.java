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

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.activiti.engine.RepositoryService;
import org.ameba.annotation.Measured;
import org.ameba.exception.TechnicalRuntimeException;
import org.openwms.core.process.execution.api.WorkflowDefinition;
import org.openwms.core.process.execution.spi.WorkflowFinder;
import org.openwms.core.process.execution.spi.WorkflowUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * A ActivitiWorkflowManager uses Activiti to manage workflows.
 *
 * @author Heiko Scherrer
 */
@Profile("ACTIVITI")
@Component
class ActivitiWorkflowManager implements WorkflowFinder, WorkflowUpdater {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivitiWorkflowManager.class);
    private final RepositoryService repositoryService;

    ActivitiWorkflowManager(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    /**
     * {@inheritDoc}
     */
    @Measured
    @Override
    public @NotBlank String loadProcessFile(@NotBlank String workflowDefinitionKey) {
        var pd = repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionKey(workflowDefinitionKey)
                .latestVersion()
                .singleResult();
        try (var is = repositoryService.getResourceAsStream(pd.getDeploymentId(), pd.getResourceName())) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new TechnicalRuntimeException(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Measured
    @Override
    public @NotNull List<WorkflowDefinition> findAll() {
        return repositoryService
                .createProcessDefinitionQuery()
                .latestVersion()
                .list()
                .stream()
                .map(pd -> {
                    var builder = WorkflowDefinition.Builder.aWorkflowDefinition()
                            .withPKey(pd.getId())
                            .withId(pd.getKey())
                            .withName(pd.getName());
                    try (var is = repositoryService.getResourceAsStream(pd.getDeploymentId(), pd.getResourceName())) {
                        builder.withBpmnXml(new String(is.readAllBytes(), StandardCharsets.UTF_8));
                    } catch (IOException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                    return builder.build();
                }).toList();
    }

    /**
     * {@inheritDoc}
     */
    @Measured
    @Override
    public @NotNull WorkflowDefinition save(@NotNull WorkflowDefinition workflowDefinition) {
        var activeProcessDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(workflowDefinition.getId())
                .latestVersion()
                .singleResult();

        repositoryService.createDeployment().addString(activeProcessDefinition.getResourceName(),
                workflowDefinition.getBpmnXml()).deploy();

        activeProcessDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(workflowDefinition.getId())
                .latestVersion()
                .singleResult();
        var builder = WorkflowDefinition.Builder.aWorkflowDefinition();
        builder.withPKey(activeProcessDefinition.getId());
        builder.withId(activeProcessDefinition.getKey());
        builder.withName(activeProcessDefinition.getName());
        try (var is = repositoryService.getResourceAsStream(activeProcessDefinition.getDeploymentId(),
                activeProcessDefinition.getResourceName())) {
            builder.withBpmnXml(new String(is.readAllBytes(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return builder.build();
    }
}

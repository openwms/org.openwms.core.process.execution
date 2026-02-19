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
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * A ProcessInstanceExecutor.
 *
 * @author Heiko Scherrer
 */
@Profile("ACTIVITI")
@Component
class ProcessInstanceExecutor {

    private final ProcessEngine processEngine;

    ProcessInstanceExecutor(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    @Async
    public void execute(ProcessDefinition processDefinition, Map<String, Object> runtimeVariables) {
        processEngine.getRuntimeService().startProcessInstanceById(processDefinition.getId(), runtimeVariables);
    }
}

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
package org.openwms.core.process.execution.spi;

import jakarta.validation.constraints.NotBlank;
import org.openwms.core.process.execution.ProgramResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

/**
 * A AbstractExecutor.
 *
 * @author Heiko Scherrer
 */
public abstract class AbstractExecutor<T> implements ProgramExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractExecutor.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<ProgramResult> execute(@NotBlank String processName, Map<String, Object> runtimeVariables) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Executing process : [{}]", processName);
        }
        var processDefinition = loadProcessDefinition(processName);
        if (null == processDefinition) {
            throw new IllegalStateException("No active process with name [%s] found".formatted(processName));
        }
        executeProcessDefinition(processDefinition, runtimeVariables);
        return Optional.empty();
    }

    protected abstract T loadProcessDefinition(String processName);

    protected abstract void executeProcessDefinition(T processDefinition, Map<String, Object> runtimeVariables);
}

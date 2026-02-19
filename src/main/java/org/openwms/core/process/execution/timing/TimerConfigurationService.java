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

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * A TimerConfigurationService.
 *
 * @author Heiko Scherrer
 */
public interface TimerConfigurationService {

    /**
     * Load all {@link TimerConfiguration} entities.
     *
     * @return A list of TimerConfiguration instances.
     */
    List<TimerConfiguration> loadConfigurations();

    /**
     * Creates a new {@link TimerConfiguration} instance.
     *
     * @param newInstance The TimerConfiguration instance to be created
     * @return The created TimerConfiguration instance
     */
    TimerConfiguration create(@NotNull TimerConfiguration newInstance);

    /**
     * Deletes a {@link TimerConfiguration} identified by the given persistent key.
     *
     * @param pKey The persistent key that identifies the TimerConfiguration to be deleted
     */
    void delete(@NotBlank String pKey);

    /**
     * Find and return a {@link TimerConfiguration}.
     *
     * @param pKey The persistent key that identifies the TimerConfiguration
     * @return The instance, never {@literal null}
     */
    @NotNull TimerConfiguration findByPKey(@NotBlank String pKey);
}

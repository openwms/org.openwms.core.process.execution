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

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A TimerConfigurationServiceTest.
 *
 * @author Heiko Scherrer
 */
@SpringBootTest
class TimerConfigurationServiceTest {

    @Autowired
    private TimerConfigurationService timerConfigurationService;
    @Autowired
    private EntityManager entityManager;

    @Test
    void shall_create_and_delete() {
        var eo = new TimerConfiguration();
        eo.setName("test");
        eo.setDescription("description");
        eo.setCronExpression("10 * * * * *");
        eo.setRuntimeVariables(Map.of("key", "value"));

        var saved = timerConfigurationService.create(eo);

        assertThat(saved.getPersistentKey()).isNotBlank();

        timerConfigurationService.delete(saved.getPersistentKey());

        var deleted = entityManager.find(TimerConfiguration.class, saved.getPk());
        assertThat(deleted).isNull();
    }
}
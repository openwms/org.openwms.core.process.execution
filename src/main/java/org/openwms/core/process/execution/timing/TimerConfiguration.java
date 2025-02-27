/*
 * Copyright 2005-2025 the original author or authors.
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

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import org.ameba.integration.jpa.ApplicationEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A TimerConfiguration stores timer configuration when a workflow should be executed.
 *
 * @author Heiko Scherrer
 */
@Entity
@Table(name = "TMS_RSRV_TIMER_CONFIG")
public class TimerConfiguration extends ApplicationEntity {

    /** Name of the workflow to execute. */
    @NotBlank
    @Column(name = "C_NAME")
    private String name;
    /** A descriptive text for the configuration. */
    @Column(name = "C_DESCRIPTION")
    private String description;
    /** A 6-digit Spring Cron expression, like {@literal 10 * * * * ?}. */
    @NotBlank
    @Column(name = "C_CRON_EXPRESSION")
    private String cronExpression;
    /** An arbitrary map of runtime variables that are passed to the workflow execution. */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "TMS_RSRV_TIMER_CONFIG_VARS",
            joinColumns = {
                    @JoinColumn(name = "C_TC_PK", referencedColumnName = "C_PK")
            },
            foreignKey = @ForeignKey(name = "FK_TIMER_CFG_VAR")
    )
    @MapKeyColumn(name = "C_KEY")
    @Column(name = "C_VALUE")
    private Map<String, String> runtimeVariables = new HashMap<>();

    protected TimerConfiguration() {}

    @Override
    public void setPersistentKey(String pKey) {
        super.setPersistentKey(pKey);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public Map<String, String> getRuntimeVariables() {
        return runtimeVariables;
    }

    public void setRuntimeVariables(Map<String, String> runtimeVariables) {
        this.runtimeVariables = runtimeVariables;
    }

    /**
     * {@inheritDoc}
     *
     * All fields.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        var that = (TimerConfiguration) o;
        return Objects.equals(name, that.name) && Objects.equals(description, that.description) && Objects.equals(cronExpression, that.cronExpression) && Objects.equals(runtimeVariables, that.runtimeVariables);
    }

    /**
     * {@inheritDoc}
     *
     * All fields.
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, description, cronExpression, runtimeVariables);
    }
}

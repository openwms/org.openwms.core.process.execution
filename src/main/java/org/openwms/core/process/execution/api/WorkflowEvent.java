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
package org.openwms.core.process.execution.api;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;
import java.util.Objects;

/**
 * A WorkflowEvent signals a change in the workflow execution.
 *
 * @author Heiko Scherrer
 */
public class WorkflowEvent implements Serializable {

    /** The type of event. */
    @NotBlank
    private String eventType;
    /** The ID of the execution this event is associated with (nullable). */
    @Nullable
    private String executionId;
    /** The ID of the execution this event is associated with (nullable). */
    @Nullable
    private String processDefinitionId;
    /** The ID of the process definition this event is associated with (nullable). */
    @Nullable
    private String processInstanceId;

    public WorkflowEvent() {
    }

    public WorkflowEvent(String eventType, String executionId, String processDefinitionId, String processInstanceId) {
        this.eventType = eventType;
        this.executionId = executionId;
        this.processDefinitionId = processDefinitionId;
        this.processInstanceId = processInstanceId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkflowEvent that = (WorkflowEvent) o;
        return Objects.equals(eventType, that.eventType) && Objects.equals(executionId, that.executionId) && Objects.equals(processDefinitionId, that.processDefinitionId) && Objects.equals(processInstanceId, that.processInstanceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventType, executionId, processDefinitionId, processInstanceId);
    }
}

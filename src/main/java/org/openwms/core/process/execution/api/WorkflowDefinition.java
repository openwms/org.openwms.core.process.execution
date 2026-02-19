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

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;
import java.util.Objects;

/**
 * A WorkflowDefinition.
 *
 * @author Heiko Scherrer
 */
public class WorkflowDefinition implements Serializable {

    @JsonProperty("pKey")
    private String pKey;
    @NotBlank
    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("bpmnXml")
    private String bpmnXml;

    public String getpKey() {
        return pKey;
    }

    public void setpKey(String pKey) {
        this.pKey = pKey;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBpmnXml() {
        return bpmnXml;
    }

    public void setBpmnXml(String bpmnXml) {
        this.bpmnXml = bpmnXml;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        var that = (WorkflowDefinition) o;
        return Objects.equals(pKey, that.pKey) && Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(bpmnXml, that.bpmnXml);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pKey, id, name, bpmnXml);
    }

    public static final class Builder {
        private String pKey;
        private @NotBlank String id;
        private String name;
        private String bpmnXml;

        private Builder() {
        }

        public static Builder aWorkflowDefinition() {
            return new Builder();
        }

        public Builder withPKey(String pKey) {
            this.pKey = pKey;
            return this;
        }

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withBpmnXml(String bpmnXml) {
            this.bpmnXml = bpmnXml;
            return this;
        }

        public WorkflowDefinition build() {
            WorkflowDefinition workflowDefinition = new WorkflowDefinition();
            workflowDefinition.setId(id);
            workflowDefinition.setName(name);
            workflowDefinition.setBpmnXml(bpmnXml);
            workflowDefinition.pKey = this.pKey;
            return workflowDefinition;
        }
    }
}

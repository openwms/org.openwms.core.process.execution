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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ameba.test.categories.SpringTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openwms.core.process.execution.TestServiceRunner;
import org.openwms.core.process.execution.timing.api.TimerConfigurationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * A TimerConfigurationControllerDocumentation.
 *
 * @author Heiko Scherrer
 */
@SpringTestSupport
@SpringBootTest(classes = {
        TestServiceRunner.class
}, properties = {
        "spring.cloud.config.enabled=false",
        "spring.cloud.config.discovery.enabled=false",
        "spring.cloud.discovery.enabled=false",
        "spring.jpa.show-sql=false",
        "spring.main.banner-mode=OFF",
        "spring.jackson.serialization.INDENT_OUTPUT=true"
})
@SqlGroup({
        @Sql(scripts = "classpath:test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(scripts = "classpath:delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class TimerConfigurationControllerDocumentation {

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ObjectMapper mapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation)).build();
    }

    @Test
    void shall_create_TimerConfiguration() throws Exception {
        var tc1 = new TimerConfigurationVO("tc1", "tc1desc", "10 * * * * *", Map.of("in1", "val1"));
        mockMvc.perform(
                        post("/timers")
                                .content(mapper.writeValueAsString(tc1))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andDo(document("tc-created",
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("pKey").description("The unique persistent key of the TimerConfiguration"),
                                fieldWithPath("name").description("Name of the process to execute"),
                                fieldWithPath("description").description("A descriptive text for the configuration"),
                                fieldWithPath("cronExpression").description("A 6-digit Spring Cron expression, like {@literal 10 * * * * ?}"),
                                fieldWithPath("runtimeVariables").description("An arbitrary map of runtime variables that are passed to the process execution"),
                                fieldWithPath("runtimeVariables.*").ignored()
                        )
                ));
    }

    @Test void shall_findby_pKey() throws Exception {
        mockMvc.perform(get("/timers/999999999"))
                .andExpect(status().isOk())
                .andDo(document("tc-find-pKey", preprocessResponse(prettyPrint())))
                .andExpect(jsonPath("pKey", is("999999999")))
                .andExpect(jsonPath("name", is("WF01")))
                .andExpect(jsonPath("description", is("Test workflow")))
                .andExpect(jsonPath("cronExpression", is("10 * * * * *")))
        ;
    }

    @Test void shall_delete_TimerConfiguration() throws Exception {
        mockMvc.perform(delete("/timers/999999999"))
                .andExpect(status().isNoContent())
                .andDo(document("tc-delete-pKey", preprocessResponse(prettyPrint())))
        ;
    }
}

package com.monolithinsight.api;

import com.monolithinsight.application.AnalyzeProjectUseCase;
import com.monolithinsight.domain.JavaClassInfo;
import com.monolithinsight.domain.ProjectAnalysis;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectAnalysisController.class)
class ProjectAnalysisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AnalyzeProjectUseCase useCase;

    @Test
    void shouldAnalyzeProject() throws Exception {
        ProjectAnalysis response = new ProjectAnalysis(
                "sample-project",
                1,
                List.of(
                        new JavaClassInfo(
                                "com.example",
                                "UserService",
                                "CLASS",
                                List.of("Service"),
                                List.of(""),
                                List.of(),
                                List.of("createUser")
                        )
                )
        );

        when(useCase.execute(anyString())).thenReturn(response);

        mockMvc.perform(post("/api/analysis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "projectPath": "C:/projects/sample-project"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectName")
                        .value("sample-project"))
                .andExpect(jsonPath("$.javaFileCount")
                        .value(1))
                .andExpect(jsonPath("$.classes[0].className")
                        .value("UserService"))
                .andExpect(jsonPath("$.classes[0].packageName")
                        .value("com.example"));
    }

    @Test
    void shouldRejectBlankProjectPath() throws Exception {
        mockMvc.perform(post("/api/analysis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "projectPath": ""
                            }
                            """))
                .andExpect(status().isBadRequest());
    }
}
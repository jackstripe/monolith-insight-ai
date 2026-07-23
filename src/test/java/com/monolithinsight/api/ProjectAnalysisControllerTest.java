package com.monolithinsight.api;

import com.monolithinsight.application.AnalyzeProjectUseCase;
import com.monolithinsight.application.BuildProjectGraphUseCase;
import com.monolithinsight.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.monolithinsight.support.TestFixtures.createClass;
import static com.monolithinsight.support.TestFixtures.createUserService;
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
    @MockitoBean
    private BuildProjectGraphUseCase buildProjectGraphUseCase;

    @Test
    void shouldAnalyzeProject() throws Exception {
        ProjectAnalysis response = new ProjectAnalysis(
                "sample-project",
                1,
                List.of(createUserService()),
                List.of()
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
                        .value("com.example"))
                .andExpect(jsonPath("$.classes[0].filePath")
                        .value("src/main/java/com/example/service/UserService.java"))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    void shouldAnalyzeProjectDetectBrokenJavaClass() throws Exception {
        ProjectAnalysis response = new ProjectAnalysis(
                "sample-project",
                2,
                List.of(createUserService()),
                List.of(new AnalysisError(
                        "src/main/java/Broken.java",
                        "error type",
                        "Parse error"
                ))
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
                        .value(2))
                .andExpect(jsonPath("$.classes[0].className")
                        .value("UserService"))
                .andExpect(jsonPath("$.classes[0].packageName")
                        .value("com.example"))
                .andExpect(jsonPath("$.classes[0].filePath")
                        .value("src/main/java/com/example/service/UserService.java"))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors").isNotEmpty());
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
    @Test
    void shouldBuildProjectGraph() throws Exception {
        ProjectAnalysis analysis  = new ProjectAnalysis(
                "sample-project",
                1,
                List.of(createUserService()),
                List.of()
        );
        JavaClassInfo orderService = createClass(
                "com.example.orders",
                "OrderService"
        );

        JavaClassInfo orderRepository = createClass(
                "com.example.orders",
                "OrderRepository"
        );

        ClassNode orderServiceNode = ClassNode.from(orderService);
        ClassNode orderRepositoryNode = ClassNode.from(orderRepository);

        ProjectGraph graph = new ProjectGraph(
                List.of(
                        orderServiceNode,
                        orderRepositoryNode
                ),
                List.of(
                        new ClassDependency(
                                orderServiceNode.id(),
                                orderRepositoryNode.id(),
                                DependencyType.FIELD
                        )
                )
        );

        when(useCase.execute(anyString()))
                .thenReturn(analysis);

        when(buildProjectGraphUseCase.execute(analysis))
                .thenReturn(graph);

        mockMvc.perform(post("/api/graph")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "projectPath": "C:/projects/sample"
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nodes").isArray())
                .andExpect(jsonPath("$.dependencies").isArray());
    }
}
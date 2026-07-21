package com.monolithinsight.api;

import com.monolithinsight.application.AnalyzeProjectUseCase;
import com.monolithinsight.application.BuildProjectGraphUseCase;
import com.monolithinsight.domain.ProjectAnalysis;
import com.monolithinsight.domain.ProjectGraph;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ProjectAnalysisController {

    private final AnalyzeProjectUseCase analyzeProjectUseCase;
    private final BuildProjectGraphUseCase buildProjectGraphUseCase;
    public ProjectAnalysisController(
            AnalyzeProjectUseCase analyzeProjectUseCase,
            BuildProjectGraphUseCase buildProjectGraphUseCase
    ) {
        this.analyzeProjectUseCase = analyzeProjectUseCase;
        this.buildProjectGraphUseCase = buildProjectGraphUseCase;
    }
    @PostMapping("/analysis")
    public ProjectAnalysis analyze(
            @Valid @RequestBody AnalyzeProjectRequest request
    ) {
        return analyzeProjectUseCase.execute(request.projectPath());
    }
    @PostMapping("/graph")
    public ProjectGraph buildGraph(
            @Valid @RequestBody AnalyzeProjectRequest request
    ) {
        ProjectAnalysis analysis =
                analyzeProjectUseCase.execute(
                        request.projectPath()
                );

        return buildProjectGraphUseCase.execute(analysis);

    }
}

package com.monolithinsight.api;

import com.monolithinsight.application.AnalyzeProjectUseCase;
import com.monolithinsight.domain.ProjectAnalysis;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analysis")
public class ProjectAnalysisController {

    private final AnalyzeProjectUseCase analyzeProjectUseCase;
    public ProjectAnalysisController(
            AnalyzeProjectUseCase analyzeProjectUseCase
        ) {
        this.analyzeProjectUseCase = analyzeProjectUseCase;
    }
    @PostMapping
    public ProjectAnalysis analyze(
            @Valid @RequestBody AnalyzeProjectRequest request
    ) {
        return analyzeProjectUseCase.execute(request.projectPath());
    }
}

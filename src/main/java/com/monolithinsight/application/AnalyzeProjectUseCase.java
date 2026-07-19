package com.monolithinsight.application;

import com.monolithinsight.domain.ProjectAnalysis;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
public class AnalyzeProjectUseCase {

    private final ProjectAnalyzer projectAnalyzer;

    public AnalyzeProjectUseCase(ProjectAnalyzer projectAnalyzer) {
        this.projectAnalyzer = projectAnalyzer;
    }
    public ProjectAnalysis execute(String projectPath) {
        return projectAnalyzer.analyze(Path.of(projectPath));
    }
}

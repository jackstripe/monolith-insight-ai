package com.monolithinsight.application;

import com.monolithinsight.domain.ProjectAnalysis;

import java.nio.file.Path;

public interface ProjectAnalyzer {

    ProjectAnalysis analyze(Path projectPath);
}

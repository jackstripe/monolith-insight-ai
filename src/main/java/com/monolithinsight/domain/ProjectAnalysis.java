package com.monolithinsight.domain;

import java.util.List;

public record ProjectAnalysis(
        String projectName,
        int javaFileCount,
        List<JavaClassInfo> classes,
        List<AnalysisError> errors
) {
}

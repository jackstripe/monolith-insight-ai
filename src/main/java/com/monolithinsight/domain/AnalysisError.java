package com.monolithinsight.domain;

public record AnalysisError(
        String filePath,
        String message) {
}

package com.monolithinsight.domain;

import java.time.Instant;

public record AnalysisSnapshot(
        String analysisId,
        String projectId,
        Instant createdAt,
        ArchitectureAnalysisReport report
) {
}
package com.monolithinsight.persistence.mongo;

import com.monolithinsight.domain.ArchitectureAnalysisReport;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "analysis_snapshots")
public record AnalysisSnapshotDocument(
        @Id String analysisId,
        String projectId,
        Instant createdAt,
        ArchitectureAnalysisReport report
) {
}
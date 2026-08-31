package com.monolithinsight.infrastructure.mongo;

import com.monolithinsight.domain.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AnalysisSnapshotMongoMapperTest {

    @Test
    void shouldMapSnapshotToDocumentAndBack() {
        ArchitectureAnalysisReport report =
                new ArchitectureAnalysisReport(
                        "order-service",
                        12,
                        List.of(),
                        new ProjectGraph(List.of(), List.of()),
                        new GraphMetrics(List.of()),
                        new CouplingReport(List.of()),
                        new CouplingReport(List.of()),
                        new PackageMetricsReport(List.of()),
                        new HotspotsReport(List.of())
                );

        AnalysisSnapshot snapshot =
                new AnalysisSnapshot(
                        "analysis-123",
                        "project-456",
                        Instant.parse("2026-08-29T00:00:00Z"),
                        report
                );

        AnalysisSnapshotMongoMapper mapper =
                new AnalysisSnapshotMongoMapper();

        AnalysisSnapshotDocument document =
                mapper.toDocument(snapshot);

        assertThat(document.analysisId())
                .isEqualTo("analysis-123");
        assertThat(document.projectId())
                .isEqualTo("project-456");
        assertThat(document.createdAt())
                .isEqualTo(snapshot.createdAt());
        assertThat(document.report())
                .isSameAs(report);

        assertThat(mapper.toDomain(document))
                .isEqualTo(snapshot);
    }
}

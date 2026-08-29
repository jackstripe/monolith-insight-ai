package com.monolithinsight.domain;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class AnalysisSnapshotTest {

    @Test
    void shouldRepresentPersistedArchitectureAnalysis() {
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

        Instant createdAt =
                Instant.parse("2026-08-28T00:00:00Z");

        AnalysisSnapshot snapshot =
                new AnalysisSnapshot(
                        "analysis-123",
                        "project-456",
                        createdAt,
                        report
                );

        assertThat(snapshot.analysisId()).isEqualTo("analysis-123");
        assertThat(snapshot.projectId()).isEqualTo("project-456");
        assertThat(snapshot.createdAt()).isEqualTo(createdAt);
        assertThat(snapshot.report()).isSameAs(report);
    }
}

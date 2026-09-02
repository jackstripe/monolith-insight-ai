package com.monolithinsight.application;

import com.monolithinsight.domain.*;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AnalyzeAndPersistProjectUseCaseTest {

    @Test
    void shouldOrchestrateCompleteAnalysisAndPersistSnapshot() {
        String projectId = "project-123";
        String projectPath = "/projects/order-service";
        Instant createdAt =
                Instant.parse("2026-09-01T12:00:00Z");

        ProjectAnalysis projectAnalysis =
                new ProjectAnalysis(
                        "order-service",
                        2,
                        List.of(),
                        List.of()
                );

        ProjectGraph graph =
                new ProjectGraph(List.of(), List.of());

        GraphMetrics graphMetrics =
                new GraphMetrics(List.of());

        CouplingReport mostCoupled =
                new CouplingReport(List.of());

        CouplingReport leastCoupled =
                new CouplingReport(List.of());

        PackageMetricsReport packageMetrics =
                new PackageMetricsReport(List.of());

        HotspotsReport hotspots =
                new HotspotsReport(List.of());

        AnalyzeProjectUseCase analyzeProjectUseCase =
                mock(AnalyzeProjectUseCase.class);

        BuildProjectGraphUseCase buildProjectGraphUseCase =
                mock(BuildProjectGraphUseCase.class);

        AnalyzeGraphMetricsUseCase analyzeGraphMetricsUseCase =
                mock(AnalyzeGraphMetricsUseCase.class);

        FindMostCoupledClassesUseCase mostCoupledUseCase =
                mock(FindMostCoupledClassesUseCase.class);

        FindLeastCoupledClassesUseCase leastCoupledUseCase =
                mock(FindLeastCoupledClassesUseCase.class);

        AnalyzePackageMetricsUseCase packageMetricsUseCase =
                mock(AnalyzePackageMetricsUseCase.class);

        GenerateHotspotsReportUseCase hotspotsUseCase =
                mock(GenerateHotspotsReportUseCase.class);

        AnalysisSnapshotRepository repository =
                mock(AnalysisSnapshotRepository.class);

        when(analyzeProjectUseCase.execute(projectPath))
                .thenReturn(projectAnalysis);

        when(buildProjectGraphUseCase.execute(projectAnalysis))
                .thenReturn(graph);

        when(analyzeGraphMetricsUseCase.execute(graph))
                .thenReturn(graphMetrics);

        when(mostCoupledUseCase.execute(graphMetrics, 10))
                .thenReturn(mostCoupled);

        when(leastCoupledUseCase.execute(graphMetrics, 10))
                .thenReturn(leastCoupled);

        when(packageMetricsUseCase.execute(graph))
                .thenReturn(packageMetrics);

        when(hotspotsUseCase.execute(graphMetrics))
                .thenReturn(hotspots);

        when(repository.save(any(AnalysisSnapshot.class)))
                .thenAnswer(invocation -> {
                    AnalysisSnapshot receivedSnapshot =
                            invocation.getArgument(0);

                    return new AnalysisSnapshot(
                            "persisted-analysis-id",
                            receivedSnapshot.projectId(),
                            receivedSnapshot.createdAt(),
                            receivedSnapshot.report()
                    );
                });

        when(repository.save(any(AnalysisSnapshot.class)))
                .thenReturn(persistedSnapshot);

        AnalyzeAndPersistProjectUseCase useCase =
                new AnalyzeAndPersistProjectUseCase(
                        analyzeProjectUseCase,
                        buildProjectGraphUseCase,
                        analyzeGraphMetricsUseCase,
                        mostCoupledUseCase,
                        leastCoupledUseCase,
                        packageMetricsUseCase,
                        hotspotsUseCase,
                        repository,
                        Clock.fixed(createdAt, ZoneOffset.UTC)
                );

        AnalysisSnapshot snapshot =
                useCase.execute(projectId, projectPath);

        assertThat(snapshot).isSameAs(persistedSnapshot);

        assertThat(snapshot.projectId())
                .isEqualTo(projectId);

        assertThat(snapshot.analysisId())
                .isNotBlank();

        assertThat(snapshot.createdAt())
                .isEqualTo(createdAt);

        assertThat(snapshot.report())
                .isEqualTo(new ArchitectureAnalysisReport(
                        "order-service",
                        2,
                        List.of(),
                        graph,
                        graphMetrics,
                        mostCoupled,
                        leastCoupled,
                        packageMetrics,
                        hotspots
                ));

        verify(repository).save(snapshot);

        ArgumentCaptor<AnalysisSnapshot> snapshotCaptor =
                ArgumentCaptor.forClass(AnalysisSnapshot.class);

        verify(repository).save(snapshotCaptor.capture());

        AnalysisSnapshot snapshotSentToRepository =
                snapshotCaptor.getValue();

        assertThat(snapshotSentToRepository.analysisId()).isNotBlank();
        assertThat(snapshotSentToRepository.projectId()).isEqualTo(projectId);
        assertThat(snapshotSentToRepository.createdAt()).isEqualTo(receivedSnapshot.createdAt());
        assertThat(snapshotSentToRepository.report()).isEqualTo(expectedReport);
    }

}

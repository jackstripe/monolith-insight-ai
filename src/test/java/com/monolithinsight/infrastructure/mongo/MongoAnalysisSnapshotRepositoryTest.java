package com.monolithinsight.infrastructure.mongo;

import com.monolithinsight.domain.*;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MongoAnalysisSnapshotRepositoryTest {

    SpringDataAnalysisSnapshotRepository springDataRepository =
            mock(SpringDataAnalysisSnapshotRepository.class);

    MongoAnalysisSnapshotRepository repository =
            new MongoAnalysisSnapshotRepository(
                    springDataRepository
            );
    @Test
    void shouldSaveAnalysisSnapshot() {

        AnalysisSnapshot snapshot = snapshot();

        when(springDataRepository.save(
                any(AnalysisSnapshotDocument.class)
        )).thenAnswer(invocation ->
                invocation.getArgument(0)
        );

        AnalysisSnapshot saved = repository.save(snapshot);

        ArgumentCaptor<AnalysisSnapshotDocument> captor =
                ArgumentCaptor.forClass(
                        AnalysisSnapshotDocument.class
                );

        verify(springDataRepository).save(captor.capture());

        assertThat(captor.getValue().analysisId())
                .isEqualTo(snapshot.analysisId());

        assertThat(captor.getValue().projectId())
                .isEqualTo(snapshot.projectId());

        assertThat(saved).isEqualTo(snapshot);
    }
    @Test
    void shouldFindAnalysisSnapshotById() {
        AnalysisSnapshot snapshot = snapshot();
        AnalysisSnapshotDocument document =
                new AnalysisSnapshotMongoMapper().toDocument(snapshot);

        when(springDataRepository.findById(snapshot.analysisId()))
                .thenReturn(Optional.of(document));

        Optional<AnalysisSnapshot> result =
                repository.findById(snapshot.analysisId());

        assertThat(result).contains(snapshot);
    }


    @Test
    void shouldReturnEmptyWhenAnalysisSnapshotDoesNotExist() {
        String analysisId = "missing-analysis";

        when(springDataRepository.findById(analysisId))
                .thenReturn(Optional.empty());

        Optional<AnalysisSnapshot> result =
                repository.findById(analysisId);

        assertThat(result).isEmpty();
    }
    @Test
    void shouldFindAllAnalysisSnapshotsByProjectIdFromNewestToOldest() {
        String projectId = "project-1";

        AnalysisSnapshot newestSnapshot = snapshot(
                "analysis-2",
                projectId,
                Instant.parse("2026-08-31T12:00:00Z")
        );

        AnalysisSnapshot oldestSnapshot = snapshot(
                "analysis-1",
                projectId,
                Instant.parse("2026-08-30T12:00:00Z")
        );

        AnalysisSnapshotMongoMapper mapper =
                new AnalysisSnapshotMongoMapper();

        when(springDataRepository
                .findAllByProjectIdOrderByCreatedAtDesc(projectId))
                .thenReturn(List.of(
                        mapper.toDocument(newestSnapshot),
                        mapper.toDocument(oldestSnapshot)
                ));

        List<AnalysisSnapshot> result =
                repository.findAllByProjectId(projectId);

        assertThat(result)
                .containsExactly(newestSnapshot, oldestSnapshot);
    }

    @Test
    void shouldFindLatestAnalysisSnapshotByProjectId() {
        String projectId = "project-1";

        AnalysisSnapshot latestSnapshot = snapshot(
                "analysis-latest",
                projectId,
                Instant.parse("2026-08-31T12:00:00Z")
        );

        AnalysisSnapshotDocument document =
                new AnalysisSnapshotMongoMapper()
                        .toDocument(latestSnapshot);

        when(springDataRepository
                .findFirstByProjectIdOrderByCreatedAtDesc(projectId))
                .thenReturn(Optional.of(document));

        Optional<AnalysisSnapshot> result =
                repository.findLatestByProjectId(projectId);

        assertThat(result).contains(latestSnapshot);
    }
    private AnalysisSnapshot snapshot() {


        return new AnalysisSnapshot(
                "analysis-123",
                "project-456",
                Instant.parse("2026-08-29T00:00:00Z"),
                createReport()
        );
    }
    private AnalysisSnapshot snapshot(
            String analysisId,
            String projectId,
            Instant createdAt
    ) {
        return new AnalysisSnapshot(
                analysisId,
                projectId,
                createdAt,
                createReport()
        );
    }

    private ArchitectureAnalysisReport createReport() {
        return new ArchitectureAnalysisReport(
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
    }

    @Test
    void shouldReturnEmptyListWhenProjectHasNoAnalysisSnapshots() {
        String projectId = "project-without-analyses";

        when(springDataRepository
                .findAllByProjectIdOrderByCreatedAtDesc(projectId))
                .thenReturn(List.of());

        List<AnalysisSnapshot> result =
                repository.findAllByProjectId(projectId);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyWhenProjectHasNoLatestAnalysisSnapshot() {
        String projectId = "project-without-analyses";

        when(springDataRepository
                .findFirstByProjectIdOrderByCreatedAtDesc(projectId))
                .thenReturn(Optional.empty());

        Optional<AnalysisSnapshot> result =
                repository.findLatestByProjectId(projectId);

        assertThat(result).isEmpty();
    }
}

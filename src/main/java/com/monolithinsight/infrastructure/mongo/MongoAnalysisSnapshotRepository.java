package com.monolithinsight.infrastructure.mongo;


import com.monolithinsight.application.AnalysisSnapshotRepository;
import com.monolithinsight.domain.AnalysisSnapshot;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class MongoAnalysisSnapshotRepository
        implements AnalysisSnapshotRepository {

    private final SpringDataAnalysisSnapshotRepository
            springDataRepository;

    private final AnalysisSnapshotMongoMapper mapper =
            new AnalysisSnapshotMongoMapper();

    public MongoAnalysisSnapshotRepository(
            SpringDataAnalysisSnapshotRepository springDataRepository
    ) {
        this.springDataRepository = springDataRepository;
    }
    @Override
    public AnalysisSnapshot save(AnalysisSnapshot snapshot) {

        AnalysisSnapshotDocument document = mapper.toDocument(snapshot);
        AnalysisSnapshotDocument savedDocument = springDataRepository.save(document);

        return mapper.toDomain(savedDocument);
    }

    @Override
    public Optional<AnalysisSnapshot> findById(String analysisId) {
        return springDataRepository
                .findById(analysisId)
                .map(mapper::toDomain);
    }

    @Override
    public List<AnalysisSnapshot> findAllByProjectId(String projectId) {
        return springDataRepository
                .findAllByProjectIdOrderByCreatedAtDesc(projectId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<AnalysisSnapshot> findLatestByProjectId(String projectId) {

        return springDataRepository
                .findFirstByProjectIdOrderByCreatedAtDesc(projectId)
                .map(mapper::toDomain);

    }
}

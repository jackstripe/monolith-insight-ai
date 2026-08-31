package com.monolithinsight.infrastructure.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataAnalysisSnapshotRepository
        extends MongoRepository<AnalysisSnapshotDocument, String> {

    List<AnalysisSnapshotDocument>
    findAllByProjectIdOrderByCreatedAtDesc(String projectId);

    Optional<AnalysisSnapshotDocument>
    findFirstByProjectIdOrderByCreatedAtDesc(String projectId);
}

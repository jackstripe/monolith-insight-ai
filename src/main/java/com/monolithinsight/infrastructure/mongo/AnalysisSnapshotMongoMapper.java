package com.monolithinsight.infrastructure.mongo;

import com.monolithinsight.domain.AnalysisSnapshot;

final class AnalysisSnapshotMongoMapper {

    AnalysisSnapshotDocument toDocument(AnalysisSnapshot snapshot) {
        return new AnalysisSnapshotDocument(
                snapshot.analysisId(),
                snapshot.projectId(),
                snapshot.createdAt(),
                snapshot.report()
        );
    }

    AnalysisSnapshot toDomain(AnalysisSnapshotDocument document) {
        return new AnalysisSnapshot(
                document.analysisId(),
                document.projectId(),
                document.createdAt(),
                document.report()
        );
    }
}

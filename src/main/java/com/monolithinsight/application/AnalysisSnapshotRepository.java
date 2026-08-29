package com.monolithinsight.application;

import com.monolithinsight.domain.AnalysisSnapshot;

import java.util.List;
import java.util.Optional;

public interface AnalysisSnapshotRepository {

    AnalysisSnapshot save(AnalysisSnapshot snapshot);

    Optional<AnalysisSnapshot> findById(String analysisId);

    /**
     * Returns every snapshot for the project,
     * ordered from newest to oldest.
     */
    List<AnalysisSnapshot> findAllByProjectId(String projectId);

    Optional<AnalysisSnapshot> findLatestByProjectId(String projectId);
}

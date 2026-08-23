package com.monolithinsight.application;

import com.monolithinsight.domain.DependencyPath;
import com.monolithinsight.domain.ProjectGraph;

import java.util.Optional;

public class FindShortestDependencyPathUseCase {

    public Optional<DependencyPath> execute(
            ProjectGraph graph,
            String sourceClassId,
            String targetClassId
    ) {
        // BFS + predecessor map


        return Optional.empty();
    }
}

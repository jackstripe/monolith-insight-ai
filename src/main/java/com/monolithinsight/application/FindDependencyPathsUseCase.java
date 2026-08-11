package com.monolithinsight.application;

import com.monolithinsight.domain.DependencyPathsReport;
import com.monolithinsight.domain.ProjectGraph;

import java.util.List;

public record FindDependencyPathsUseCase() {

    public DependencyPathsReport execute(
            ProjectGraph graph,
            String sourceClassId,
            String targetClassId
    ){

        return new DependencyPathsReport(List.of());
    }
}

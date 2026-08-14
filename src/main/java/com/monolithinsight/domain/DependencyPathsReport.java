package com.monolithinsight.domain;

import java.util.List;

public record DependencyPathsReport(List<DependencyPath> paths) {

    public DependencyPathsReport {
        paths = List.copyOf(paths);
    }
}

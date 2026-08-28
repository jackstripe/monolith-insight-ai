package com.monolithinsight.domain;

import java.util.List;

public record ArchitectureAnalysisReport(
        String projectName,
        int javaFileCount,
        List<AnalysisError> errors,
        ProjectGraph graph,
        GraphMetrics graphMetrics,
        CouplingReport mostCoupledClasses,
        CouplingReport leastCoupledClasses,
        PackageMetricsReport packageMetrics,
        HotspotsReport hotspots
) {

    public ArchitectureAnalysisReport {
        errors = List.copyOf(errors);
    }
}
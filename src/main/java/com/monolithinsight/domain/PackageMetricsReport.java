package com.monolithinsight.domain;

import java.util.List;

public record PackageMetricsReport(
        List<PackageMetrics> packages
) {

    public PackageMetricsReport {
        packages = List.copyOf(packages);
    }
}
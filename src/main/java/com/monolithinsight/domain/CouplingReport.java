package com.monolithinsight.domain;

import java.util.List;

public record CouplingReport(
        List<CoupledClass> classes
) {
    public CouplingReport {
        classes = List.copyOf(classes);
    }
}
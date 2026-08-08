package com.monolithinsight.domain;

import java.util.List;

public record ImpactAnalysisReport (List<ClassNode> impactedClasses) {

    public ImpactAnalysisReport {

        impactedClasses = List.copyOf(impactedClasses);
    }
}


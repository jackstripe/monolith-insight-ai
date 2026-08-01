package com.monolithinsight.domain;

public record ClassHotspot(
        String classId,
        int incomingDependencies,
        int outgoingDependencies,
        int totalDependencies,
        CouplingLevel couplingLevel
) {
}
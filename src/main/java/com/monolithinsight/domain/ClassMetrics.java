package com.monolithinsight.domain;

public record ClassMetrics(
        String classId,
        int incomingDependencies,
        int outgoingDependencies
) {
    public int totalDependencies() {
        return incomingDependencies + outgoingDependencies;
    }
}
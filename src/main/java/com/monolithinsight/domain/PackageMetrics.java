package com.monolithinsight.domain;

public record PackageMetrics(
        String packageName,
        int classCount,
        int incomingDependencies,
        int outgoingDependencies
) {

    public int totalDependencies() {
        return incomingDependencies + outgoingDependencies;
    }

    public double averageCoupling() {
        return classCount == 0
                ? 0.0
                : (double) totalDependencies() / classCount;
    }
}
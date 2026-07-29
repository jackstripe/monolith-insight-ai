package com.monolithinsight.domain;

public record CoupledClass(
        String classId,
        int incomingDependencies,
        int outgoingDependencies,
        int totalDependencies
) {}
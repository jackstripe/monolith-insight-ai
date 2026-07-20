package com.monolithinsight.domain;

public record ClassDependency(
        String sourceNodeId,
        String targetNodeId,
        DependencyType type
) {
}
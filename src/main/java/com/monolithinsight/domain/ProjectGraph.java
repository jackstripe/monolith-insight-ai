package com.monolithinsight.domain;

import java.util.List;

public record ProjectGraph(
        List<ClassNode> nodes,
        List<ClassDependency> dependencies
) {
    public ProjectGraph {
        nodes = List.copyOf(nodes);
        dependencies = List.copyOf(dependencies);
    }
}
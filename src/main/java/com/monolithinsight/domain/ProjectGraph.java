package com.monolithinsight.domain;

import java.util.List;

public record ProjectGraph(
        List<ClassNode> nodes,
        List<ClassDependency> dependencies
) {
}
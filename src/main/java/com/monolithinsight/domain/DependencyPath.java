package com.monolithinsight.domain;

import java.util.List;

public record DependencyPath(List<ClassNode> classes) {

    public DependencyPath {
        classes = List.copyOf(classes);
    }
}

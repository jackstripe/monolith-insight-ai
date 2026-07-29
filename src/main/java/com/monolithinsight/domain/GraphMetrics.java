package com.monolithinsight.domain;

import java.util.List;

public record GraphMetrics(
        List<ClassMetrics> classes
) {
    public GraphMetrics {
        classes = List.copyOf(classes);
    }
}
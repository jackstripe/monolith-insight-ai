package com.monolithinsight.application;

import com.monolithinsight.domain.ClassNode;

import java.util.List;

public record ReachabilityReport(
        List<ClassNode> classes
) {
    public ReachabilityReport {
            classes = List.copyOf(classes);
        }
}


package com.monolithinsight.domain;

import java.util.List;

public record ReachabilityReport(
        List<ClassNode> classes
) {
    public ReachabilityReport {
            classes = List.copyOf(classes);
        }
}


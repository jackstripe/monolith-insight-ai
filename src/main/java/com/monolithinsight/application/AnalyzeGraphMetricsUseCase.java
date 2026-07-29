package com.monolithinsight.application;

import com.monolithinsight.domain.*;

import java.util.*;

public class AnalyzeGraphMetricsUseCase {

    public GraphMetrics execute(ProjectGraph graph) {

        Map<String, Integer> incomingByClass = new HashMap<>();
        Map<String, Integer> outgoingByClass = new HashMap<>();

        for (ClassDependency dependency : graph.dependencies()) {
            incomingByClass.merge(
                    dependency.targetNodeId(),
                    1,
                    Integer::sum
            );
        }

        for (ClassDependency dependency : graph.dependencies()) {
            outgoingByClass.merge(
                    dependency.sourceNodeId(),
                    1,
                    Integer::sum
            );
        }

        List<ClassMetrics> metrics = graph.nodes()
                .stream()
                .map(node -> new ClassMetrics(
                        node.id(),
                        incomingByClass.getOrDefault(node.id(), 0),
                        outgoingByClass.getOrDefault(node.id(), 0)
                ))
                .toList();

        return new GraphMetrics(metrics );
    }
}

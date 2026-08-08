package com.monolithinsight.application;

import com.monolithinsight.domain.ClassDependency;
import com.monolithinsight.domain.ClassNode;
import com.monolithinsight.domain.ImpactAnalysisReport;
import com.monolithinsight.domain.ProjectGraph;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AnalyzeImpactUseCase {

    public ImpactAnalysisReport execute(
            ProjectGraph graph,
            String changedClassId
    ) {
        Map<String, ClassNode> nodesById =
                graph.nodes().stream()
                        .collect(Collectors.toMap(
                                ClassNode::id,
                                Function.identity()
                        ));

        if (!nodesById.containsKey(changedClassId)) {
            throw new IllegalArgumentException(
                    "Class not found in graph: " + changedClassId
            );
        }
        Map<String, List<String>> reverseAdjacencyList =
                graph.dependencies().stream()
                        .collect(Collectors.groupingBy(
                                ClassDependency::targetNodeId,
                                Collectors.mapping(
                                        ClassDependency::sourceNodeId,
                                        Collectors.toList()
                                )
                        ));

        return new ImpactAnalysisReport(List.of());
    }
}

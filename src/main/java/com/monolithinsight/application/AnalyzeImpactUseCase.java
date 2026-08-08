package com.monolithinsight.application;

import com.monolithinsight.domain.ClassDependency;
import com.monolithinsight.domain.ClassNode;
import com.monolithinsight.domain.ImpactAnalysisReport;
import com.monolithinsight.domain.ProjectGraph;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AnalyzeImpactUseCase {

    public ImpactAnalysisReport execute(
            ProjectGraph graph,
            String changedClassId
    ) {

        Set<String> visited =  new HashSet<>();
        Queue<String> pending = new ArrayDeque<>();

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

        visited.add(changedClassId);
        pending.add(changedClassId);


        while(!pending.isEmpty()){

            String currentId = pending.remove();

            for(String adjacentClassId : reverseAdjacencyList.getOrDefault(currentId, Collections.emptyList())){
                if (visited.add(adjacentClassId)) {
                    pending.add(adjacentClassId);
                }
            }
        }

        visited.remove(changedClassId);

        List<ClassNode> impactedClasses = visited
                .stream()
                .sorted()
                .map(nodesById::get)
                .toList();

        return new ImpactAnalysisReport(impactedClasses);

    }
}

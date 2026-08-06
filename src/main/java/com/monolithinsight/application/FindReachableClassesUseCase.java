package com.monolithinsight.application;

import com.monolithinsight.domain.ClassDependency;
import com.monolithinsight.domain.ClassNode;
import com.monolithinsight.domain.ProjectGraph;
import com.monolithinsight.domain.ReachabilityReport;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FindReachableClassesUseCase {



    public ReachabilityReport execute(ProjectGraph graph, String startClassId){

        Map<String, ClassNode> nodesById;
        Map<String, List<String>> adjacencyList;
        Set<String> visited =  new HashSet<>();
        Queue<String> pending = new ArrayDeque<>();

        nodesById = graph.nodes().stream()
                .collect(Collectors.toMap(
                        ClassNode::id,
                        Function.identity()
                ));

        if(!nodesById.containsKey(startClassId)){
            throw new IllegalArgumentException(
                    "Class not found in graph: " + startClassId
            );
        }


        adjacencyList = graph.dependencies().stream()
                .collect(Collectors.groupingBy(
                        ClassDependency::sourceNodeId,
                        Collectors.mapping(ClassDependency::targetNodeId, Collectors.toList())
                        )
                );

        if(!adjacencyList.containsKey(startClassId)){
            return new ReachabilityReport(List.of());
        }

        visited.add(startClassId);
        pending.add(startClassId);

        while(!pending.isEmpty()){
            String currentClassId = pending.remove();
            for (String adjacentClassId : adjacencyList.getOrDefault(  currentClassId, Collections.emptyList())) {
                if (visited.add(adjacentClassId)) {
                    pending.add(adjacentClassId);
                }
            }
        }

        visited.remove(startClassId);

        List<ClassNode> reachableClasses = nodesById.entrySet().stream()
                .filter(entry -> visited.contains(entry.getKey()))
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .toList();

        return new ReachabilityReport(reachableClasses);

    }
}

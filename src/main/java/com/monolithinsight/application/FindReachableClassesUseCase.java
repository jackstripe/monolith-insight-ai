package com.monolithinsight.application;

import com.monolithinsight.domain.ClassDependency;
import com.monolithinsight.domain.ClassNode;
import com.monolithinsight.domain.ProjectGraph;
import com.monolithinsight.domain.ReachabilityReport;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FindReachableClassesUseCase {



    public ReachabilityReport execute(ProjectGraph graph, String classId){

        Map<String, ClassNode> nodesById;
        Map<String, List<String>> adjacencyList;
        Set<String> visited =  new HashSet<>();
        Queue<String> pending = new ArrayDeque<>();

        nodesById = graph.nodes().stream()
                .collect(Collectors.toMap(
                        ClassNode::id,
                        Function.identity()
                ));
        adjacencyList = graph.dependencies().stream()
                .collect(Collectors.groupingBy(
                        ClassDependency::sourceNodeId,
                        Collectors.mapping(ClassDependency::targetNodeId, Collectors.toList())
                        )
                );

        visited.add(classId);
        pending.add(classId);

        while(!pending.isEmpty()){
            for(String adjacents : pending){
                List<String> list = adjacencyList.get(adjacents);
                if(list != null){
                    for(String adjacent :  adjacencyList.getOrDefault(adjacents,Collections.emptyList())){
                        pending.remove();
                        if (visited.add(adjacent)) {
                            pending.add(adjacent);
                        }
                    }
                }else {
                    pending.remove();
                }

            }
        }

        visited.remove(classId);
        ReachabilityReport reachabilityReport =  new ReachabilityReport(nodesById.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .filter(
                        entry -> visited.contains(entry.getKey())
                )
                .map(Map.Entry::getValue)
                .toList());



        return reachabilityReport;

    }
}

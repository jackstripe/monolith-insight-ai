package com.monolithinsight.application;

import com.monolithinsight.domain.ClassDependency;
import com.monolithinsight.domain.ClassNode;
import com.monolithinsight.domain.DependencyPath;
import com.monolithinsight.domain.ProjectGraph;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FindShortestDependencyPathUseCase {

    public Optional<DependencyPath> execute(
            ProjectGraph graph,
            String sourceClassId,
            String targetClassId
    ) {
        // BFS + predecessor map
        Map<String, List<String>> adjacencyList;

        Queue<String> pending = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();
        Map<String, String> previousClassById = new HashMap<>();

        Map<String, ClassNode> nodesById =
                graph.nodes().stream()
                        .collect(Collectors.toMap(
                                ClassNode::id,
                                Function.identity()
                        ));

        if (!nodesById.containsKey(sourceClassId)) {
            throw new IllegalArgumentException(
                    "Class not found in graph: " + sourceClassId
            );
        }

        if (!nodesById.containsKey(targetClassId)) {
            throw new IllegalArgumentException(
                    "Class not found in graph: " + targetClassId
            );
        }

        pending.add(sourceClassId);
        visited.add(sourceClassId);

        while(!pending.isEmpty()){
            String next = pending.element();
            List<ClassDependency> dep =  graph.dependencies().stream()
                    .filter(
                            dependecy -> dependecy.sourceNodeId()
                            .equals(next))
                    /*.forEach(dependency -> {if(visited.add(dependency.targetNodeId() );{
                                previousClassById.put(dependency.targetNodeId(), sourceClassId);
                        }
                    })*/

                    .toList();
            for(int i = 0; i < dep.size(); i++){
                if(visited.add(dep.get(i).targetNodeId())){
                    previousClassById.put(dep.get(i).targetNodeId(), next);
                    pending.add(dep.get(i).targetNodeId());
                }
            }
            pending.remove(next);
        }
        return Optional.of(
                new DependencyPath(List.of(
                        nodesById.get(sourceClassId),
                        nodesById.get(targetClassId)
                ))
        );
    }
}

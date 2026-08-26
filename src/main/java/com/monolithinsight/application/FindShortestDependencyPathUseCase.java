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
        Map<String, Set<String>> adjacencyList = new HashMap<>();

        Queue<String> pending = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();
        Map<String, String> previousClassById = new HashMap<>();

        Map<String, ClassNode> nodesById =
                graph.nodes().stream()
                        .collect(Collectors.toMap(
                                ClassNode::id,
                                Function.identity()
                        ));

        for (ClassDependency dependency : graph.dependencies()) {
            adjacencyList
                    .computeIfAbsent(
                            dependency.sourceNodeId(),
                            ignored -> new TreeSet<>()
                    )
                    .add(dependency.targetNodeId());
        }

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

        if (sourceClassId.equals(targetClassId)) {
            return Optional.of(
                    new DependencyPath(
                            List.of(nodesById.get(sourceClassId))
                    )
            );
        }

        pending.add(sourceClassId);
        visited.add(sourceClassId);

        while(!pending.isEmpty()){
            String currentClassId = pending.remove();
            for (String neighbour : adjacencyList.getOrDefault( currentClassId, Collections.emptySet() )) {
                if(visited.add(neighbour)){
                    previousClassById.put(neighbour, currentClassId);
                    if (neighbour.equals(targetClassId)) {
                        return Optional.of(reconstructPath(sourceClassId,targetClassId, previousClassById,nodesById));
                    }
                    pending.add(neighbour);
                }
            }
        }

        return Optional.empty();
    }

    private DependencyPath reconstructPath(
            String sourceClassId,
            String targetClassId,
            Map<String, String> previousClassById,
            Map<String, ClassNode> nodesById
    ) {
        LinkedList<ClassNode> path = new LinkedList<>();
        String pathClassId = targetClassId;

        while (!pathClassId.equals(sourceClassId)) {
            path.addFirst(nodesById.get(pathClassId));
            pathClassId = previousClassById.get(pathClassId);
        }

        path.addFirst(nodesById.get(sourceClassId));

        return new DependencyPath(path);
    }
}

package com.monolithinsight.application;

import com.monolithinsight.domain.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FindDependencyPathsUseCase {

    public DependencyPathsReport execute(
            ProjectGraph graph,
            String sourceClassId,
            String targetClassId
    ){
        List<DependencyPath> paths = new ArrayList<>();
        List<String> currentPath = new ArrayList<>();
        Set<String> classesInCurrentPath = new HashSet<>();

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

        Map<String, List<String>> adjacencyList =
                graph.dependencies().stream()
                        .collect(Collectors.groupingBy(
                                ClassDependency::sourceNodeId,
                                Collectors.mapping(
                                        ClassDependency::targetNodeId,
                                        Collectors.collectingAndThen(
                                                Collectors.toSet(),
                                                targets -> targets.stream()
                                                        .sorted()
                                                        .toList()
                                        )
                                )
                        ));

        findPaths(
                sourceClassId,
                targetClassId,
                adjacencyList,
                nodesById,
                currentPath,
                classesInCurrentPath,
                paths
        );
        return new DependencyPathsReport(paths);
    }

    private void findPaths(
            String currentClassId,
            String targetClassId,
            Map<String, List<String>> adjacencyList,
            Map<String, ClassNode> nodesById,
            List<String> currentPath,
            Set<String> classesInCurrentPath,
            List<DependencyPath> paths
    ) {
        currentPath.add(currentClassId);
        classesInCurrentPath.add(currentClassId);

        if (currentClassId.equals(targetClassId)) {
            List<ClassNode> pathClasses = currentPath.stream()
                    .map(nodesById::get)
                    .toList();

            paths.add(new DependencyPath(pathClasses));
        } else {
            for (String neighbour : adjacencyList.getOrDefault(currentClassId, Collections.emptyList())) {

                if(!classesInCurrentPath.contains(neighbour)){

                    findPaths(neighbour,
                            targetClassId,
                            adjacencyList,
                            nodesById,
                            currentPath,
                            classesInCurrentPath,
                            paths);
                }
            }
        }
        currentPath.remove(currentPath.size() - 1);
        classesInCurrentPath.remove(currentClassId);
    }


}

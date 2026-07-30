package com.monolithinsight.application;

import com.monolithinsight.domain.*;

import java.util.*;
import java.util.stream.Collectors;

public class AnalyzePackageMetricsUseCase {

    public PackageMetricsReport execute(ProjectGraph graph) {


        Map<String, Integer> incomingByPackage = new HashMap<>();
        Map<String, Integer> outgoingByPackage = new HashMap<>();




        Map<String, String> packageByClassId =
                graph.nodes().stream()
                        .collect(Collectors.toMap(
                                ClassNode::id,
                                node -> node.classInfo().packageName()
                        ));

        Map<String, Integer> classCountByPackage =
                graph.nodes().stream()
                        .collect(Collectors.groupingBy(
                                node -> node.classInfo().packageName(),
                                Collectors.summingInt(node -> 1)
                        ));

        for(ClassDependency dependency : graph.dependencies()){
            String sourcePackage = packageByClassId.get(dependency.sourceNodeId());

            String targetPackage = packageByClassId.get(dependency.targetNodeId());

            if (sourcePackage.equals(targetPackage)) {
                continue;
            }
            outgoingByPackage.merge(
                    sourcePackage,
                    1,
                    Integer::sum
            );

            incomingByPackage.merge(
                    targetPackage,
                    1,
                    Integer::sum
            );
        }

        List<PackageMetrics> packages = new ArrayList<>();

        classCountByPackage.forEach((key, value) ->
                packages.add(new PackageMetrics(
                    key,
                    classCountByPackage.getOrDefault(key,0),
                    incomingByPackage.getOrDefault(key,0),
                    outgoingByPackage.getOrDefault(key,0))));

        packages.sort(Comparator.comparing(PackageMetrics::packageName));
        return new PackageMetricsReport(packages);

    }
}

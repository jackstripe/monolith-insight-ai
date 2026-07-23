package com.monolithinsight.application;

import com.monolithinsight.domain.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BuildProjectGraphUseCase {

    public ProjectGraph execute(ProjectAnalysis project){
        log.info("Starting creation of the graph for project: {}", project.projectName());

        List<ClassNode> nodes = project.classes()
                .stream()
                .map(ClassNode::from)
                .toList();

        Map<String, List<ClassNode>> simpleNameIndex = buildSimpleNameIndex(nodes);
        List<ClassDependency> dependencies = resolveFieldDependencies(nodes,simpleNameIndex);

        log.info(
                "Graph created. Nodes: {}, dependencies: {}",
                nodes.size(),
                dependencies.size()
        );

        log.debug("Resolved dependencies: {}", dependencies);
        return new ProjectGraph(nodes,dependencies);
    }

    private Map<String, List<ClassNode>> buildSimpleNameIndex(
            List<ClassNode> nodes
    ) {
        return nodes.stream()
                .collect(Collectors.groupingBy(
                        node -> node.classInfo().className()
                ));
    }

    private List<ClassDependency> resolveFieldDependencies(
            List<ClassNode> nodes,
            Map<String, List<ClassNode>> simpleNameIndex
    ) {
        Set<ClassDependency> dependencies = new LinkedHashSet<>();

        for (ClassNode sourceNode : nodes) {
            for (JavaFieldInfo field : sourceNode.classInfo().fields()) {

                List<ClassNode> candidates =
                        simpleNameIndex.getOrDefault(
                                field.type(),
                                List.of()
                        );
                if (candidates.size() == 1) {
                    ClassNode targetNode = candidates.getFirst();
                    if(!Objects.equals(sourceNode.id(), targetNode.id())) {
                        ClassDependency dependency =
                                new ClassDependency(
                                        sourceNode.id(),
                                        targetNode.id(),
                                        DependencyType.FIELD
                                );

                        dependencies.add(dependency);
                    }
                }
            }
        }
        return new ArrayList<>(dependencies);
    }
}

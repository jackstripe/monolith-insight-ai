package com.monolithinsight.application;

import com.monolithinsight.domain.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BuildProjectGraphUseCase {

    public ProjectGraph execute(ProjectAnalysis project) {
        log.info("Starting creation of the graph for project: {}", project.projectName());

        List<ClassNode> nodes = project.classes()
                .stream()
                .map(ClassNode::from)
                .toList();

        Map<String, List<ClassNode>> simpleNameIndex = buildSimpleNameIndex(nodes);
        List<ClassDependency> dependencies = resolveDependencies(nodes, simpleNameIndex);

        log.info(
                "Graph created. Nodes: {}, dependencies: {}",
                nodes.size(),
                dependencies.size()
        );

        log.info("Resolved dependencies: {}", dependencies);
        return new ProjectGraph(nodes, dependencies);
    }

    private List<ClassDependency> resolveDependencies(
            List<ClassNode> nodes,
            Map<String, List<ClassNode>> simpleNameIndex
    ) {
        Set<ClassDependency> dependencies = new LinkedHashSet<>();

        for(ClassNode sourceNode : nodes){
            dependencies.addAll(resolveFieldDependencies(sourceNode, simpleNameIndex));
            dependencies.addAll(resolveConstructorDependencies(sourceNode, simpleNameIndex));
            dependencies.addAll(resolveMethodDependencies(sourceNode, simpleNameIndex));
        }

        return new ArrayList<>(dependencies);
    }

    private Set<ClassDependency> resolveFieldDependencies(ClassNode sourceNode,
                                                          Map<String, List<ClassNode>> simpleNameIndex){
        Set<ClassDependency> fieldDependencies = new LinkedHashSet<>();
        for (JavaFieldInfo field : sourceNode.classInfo().fields()) {

            List<ClassNode> candidates =
                    simpleNameIndex.getOrDefault(
                            field.type(),
                            List.of()
                    );
            if (candidates.size() == 1) {
                ClassNode targetNode = candidates.getFirst();
                if (!Objects.equals(sourceNode.id(), targetNode.id())) {
                    ClassDependency dependency =
                            new ClassDependency(
                                    sourceNode.id(),
                                    targetNode.id(),
                                    DependencyType.FIELD
                            );

                    fieldDependencies.add(dependency);
                }
            }
        }
        return fieldDependencies;
    }

    private Set<ClassDependency> resolveConstructorDependencies(ClassNode sourceNode,
                                                          Map<String, List<ClassNode>> simpleNameIndex){

        Set<ClassDependency> constructorDependencies = new LinkedHashSet<>();

        for (JavaConstructorInfo constructorInfo : sourceNode.classInfo().constructors()) {
            for (JavaParameterInfo parameterInfo : constructorInfo.parameters()){
                List<ClassNode> candidates =
                        simpleNameIndex.getOrDefault(
                                parameterInfo.type(),
                                List.of()
                        );
                if(candidates.size() == 1){
                    ClassNode targetNode = candidates.getFirst();
                    if (!Objects.equals(sourceNode.id(), targetNode.id())) {
                        ClassDependency dependency =
                                new ClassDependency(
                                        sourceNode.id(),
                                        targetNode.id(),
                                        DependencyType.CONSTRUCTOR
                                );
                        constructorDependencies.add(dependency);
                    }
                }
            }
        }
        return constructorDependencies;

    }
    private Set<ClassDependency> resolveMethodDependencies(ClassNode sourceNode,
                                                                Map<String, List<ClassNode>> simpleNameIndex) {
        Set<ClassDependency> methodDependencies = new LinkedHashSet<>();
        for(JavaMethodInfo methodInfo: sourceNode.classInfo().methods()){

            for(JavaParameterInfo parameterInfo: methodInfo.parameters()){
                List<ClassNode> candidates =
                        simpleNameIndex.getOrDefault(
                                parameterInfo.type(),
                                List.of()
                        );
                if(candidates.size() == 1){
                    ClassNode targetNode = candidates.getFirst();
                    if (!Objects.equals(sourceNode.id(), targetNode.id())) {
                        ClassDependency dependency =
                                new ClassDependency(
                                        sourceNode.id(),
                                        targetNode.id(),
                                        DependencyType.METHOD
                                );
                        methodDependencies.add(dependency);
                    }
                }
            }
        }
        return methodDependencies;
    }

    private Map<String, List<ClassNode>> buildSimpleNameIndex(
            List<ClassNode> nodes
    ) {
        return nodes.stream()
                .collect(Collectors.groupingBy(
                        node -> node.classInfo().className()
                ));
    }
}
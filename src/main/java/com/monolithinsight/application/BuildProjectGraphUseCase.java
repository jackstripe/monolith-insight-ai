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
            dependencies.addAll(resolveReturnDependencies(sourceNode,simpleNameIndex));
            if(sourceNode.classInfo().superClass().isPresent()) {
                dependencies.addAll(resolveInheritanceDependency(sourceNode, simpleNameIndex));
            }
            if(!sourceNode.classInfo().implementedInterfaces().isEmpty()){
                dependencies.addAll(resolveImplementationsDependency(sourceNode, simpleNameIndex));
            }
        }

        return new ArrayList<>(dependencies);
    }

    private Set<ClassDependency> resolveFieldDependencies(ClassNode sourceNode,
                                                          Map<String, List<ClassNode>> simpleNameIndex){
        Set<ClassDependency> fieldDependencies = new LinkedHashSet<>();
        for (JavaFieldInfo field : sourceNode.classInfo().fields()) {
            fieldDependencies.addAll(
                    findCandidatesAndResolveDependency(
                            sourceNode,
                            field.type(),
                            DependencyType.FIELD,
                            simpleNameIndex
                    ));
        }
        return fieldDependencies;
    }

    private Set<ClassDependency> resolveConstructorDependencies(ClassNode sourceNode,
                                                          Map<String, List<ClassNode>> simpleNameIndex){

        Set<ClassDependency> constructorDependencies = new LinkedHashSet<>();

        for (JavaConstructorInfo constructorInfo : sourceNode.classInfo().constructors()) {
            for (JavaParameterInfo parameterInfo : constructorInfo.parameters()){
                constructorDependencies.addAll(
                        findCandidatesAndResolveDependency(sourceNode,
                                parameterInfo.type(),
                                DependencyType.CONSTRUCTOR,
                                simpleNameIndex));
            }
        }
        return constructorDependencies;

    }
    private Set<ClassDependency> resolveMethodDependencies(ClassNode sourceNode,
                                                                Map<String, List<ClassNode>> simpleNameIndex) {
        Set<ClassDependency> methodDependencies = new LinkedHashSet<>();

        for(JavaMethodInfo methodInfo: sourceNode.classInfo().methods()){
            for(JavaParameterInfo parameterInfo: methodInfo.parameters()){
                methodDependencies.addAll(
                        findCandidatesAndResolveDependency(sourceNode,
                                parameterInfo.type(),
                                DependencyType.METHOD_PARAMETER,
                                simpleNameIndex));
            }
        }
        return methodDependencies;
    }

    private Set<ClassDependency> resolveReturnDependencies(ClassNode sourceNode,
                                                           Map<String, List<ClassNode>> simpleNameIndex) {
        Set<ClassDependency> returnDependencies = new LinkedHashSet<>();

        for(JavaMethodInfo methodInfo: sourceNode.classInfo().methods()){

            returnDependencies.addAll(
                    findCandidatesAndResolveDependency(sourceNode,
                            methodInfo.returnType(),
                            DependencyType.RETURN_TYPE,
                            simpleNameIndex));
        }
        return returnDependencies;
    }

    private Set<ClassDependency> resolveInheritanceDependency(ClassNode sourceNode,
                                                         Map<String, List<ClassNode>> simpleNameIndex){

        Set<ClassDependency> inheritanceDependencies = new LinkedHashSet<>();
        sourceNode.classInfo()
                .superClass().ifPresent(
                superClassName -> {
                    inheritanceDependencies.addAll(
                            findCandidatesAndResolveDependency(
                                    sourceNode,
                                    superClassName,
                                    DependencyType.INHERITANCE,
                                    simpleNameIndex
                            ));
                });
        return inheritanceDependencies;
    }

    private Set<ClassDependency> resolveImplementationsDependency(ClassNode sourceNode,
                                                              Map<String, List<ClassNode>> simpleNameIndex) {

        Set<ClassDependency> implementationsDependencies = new LinkedHashSet<>();
        for (String implementation : sourceNode.classInfo().implementedInterfaces()) {
            implementationsDependencies.addAll(findCandidatesAndResolveDependency(sourceNode,implementation,DependencyType.IMPLEMENTS,simpleNameIndex));
        }

        return implementationsDependencies;
    }

    private Map<String, List<ClassNode>> buildSimpleNameIndex(
            List<ClassNode> nodes
    ) {
        return nodes.stream()
                .collect(Collectors.groupingBy(
                        node -> node.classInfo().className()
                ));
    }

    private Set<ClassDependency> findCandidatesAndResolveDependency(ClassNode sourceNode, String referencedType,
                                              DependencyType dependencyType, Map<String, List<ClassNode>> simpleNameIndex ){
        Set<ClassDependency> dependencies = new LinkedHashSet<>();
        List<ClassNode> candidates =
                simpleNameIndex.getOrDefault(
                        referencedType,
                        List.of()
                );
        if(candidates.size() == 1){
            ClassNode targetNode = candidates.getFirst();
            if (!Objects.equals(sourceNode.id(), targetNode.id())) {
                ClassDependency dependency =
                        new ClassDependency(
                                sourceNode.id(),
                                targetNode.id(),
                                dependencyType
                        );
                dependencies.add(dependency);
            }
        }
        return dependencies;
    }
}
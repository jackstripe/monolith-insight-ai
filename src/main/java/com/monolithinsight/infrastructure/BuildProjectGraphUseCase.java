package com.monolithinsight.infrastructure;

import com.monolithinsight.domain.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class BuildProjectGraphUseCase {



    public BuildProjectGraphUseCase(){

    }

    public ProjectGraph execute(ProjectAnalysis project){
        log.info("Starting creation of the graph for for project: {}", project.projectName());

        List<ClassNode> nodes = new ArrayList<>();
        for(JavaClassInfo classInfo : project.classes()){
            nodes.add(ClassNode.from(classInfo));
        }
        log.info(checkDuplicatesList(nodes).toString());
        List<ClassDependency> dependencies = new ArrayList<>();
        return new ProjectGraph(nodes,dependencies);
    }
    public List<ClassNode> checkDuplicatesList(List<ClassNode> nodes){

        return nodes.stream()
                .filter(e -> Collections.frequency(nodes, e) > 1)
                .distinct()
                .collect(Collectors.toList());
    }
}

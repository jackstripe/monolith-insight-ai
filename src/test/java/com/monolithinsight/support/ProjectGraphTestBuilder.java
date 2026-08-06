package com.monolithinsight.support;

import com.monolithinsight.domain.ClassDependency;
import com.monolithinsight.domain.ClassNode;
import com.monolithinsight.domain.DependencyType;
import com.monolithinsight.domain.ProjectGraph;

import java.util.List;

public class ProjectGraphTestBuilder {

    public ProjectGraphTestBuilder(ProjectGraph projectGraph, List<ClassNode> classNodes, List<ClassDependency> classDependencies) {
        this.projectGraph = projectGraph;
        this.classNodes = classNodes;
        this.classDependencies = classDependencies;
    }

    public List<ClassNode> getClassNodes() { return List.copyOf(classNodes); }

    public List<ClassDependency> getClassDependencies() { return List.copyOf(classDependencies);  }

    public ProjectGraph getProjectGraph() { return projectGraph; }

    private final ProjectGraph projectGraph;

    private final List<ClassNode> classNodes;

    private final List<ClassDependency> classDependencies;



    public static class Builder {
        private final ProjectGraph projectGraph;

        private final List<ClassNode> classNodes;

        private final List<ClassDependency> classDependencies;

        public Builder(ProjectGraph projectGraph, List<ClassNode> classNodes, List<ClassDependency> classDependencies) {
            this.projectGraph = projectGraph;
            this.classNodes = classNodes;
            this.classDependencies = classDependencies;
        }
        public Builder addNode(String node) {

            ClassNode classNode =
                    ClassNode.from(
                            TestFixtures.createClass(
                                    "com.example.orders",
                                    node
                            )
                    );

            this.classNodes.add(classNode);
            return this;
        }
        public Builder addDependency(String source, String target){
            ClassDependency classDependency = new ClassDependency(
                    source,
                    target,
                    DependencyType.FIELD
            );
            this.classDependencies.add(classDependency);
            return this;
        }

        public ProjectGraph build(){
            return new ProjectGraph(this.classNodes,this.classDependencies);
        }
    }
}

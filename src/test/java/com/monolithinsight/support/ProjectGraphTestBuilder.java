package com.monolithinsight.support;

import com.monolithinsight.domain.ClassDependency;
import com.monolithinsight.domain.ClassNode;
import com.monolithinsight.domain.DependencyType;
import com.monolithinsight.domain.ProjectGraph;

import java.util.ArrayList;
import java.util.List;

public final class ProjectGraphTestBuilder {

    private final List<ClassNode> nodes = new ArrayList<>();
    private final List<ClassDependency> dependencies = new ArrayList<>();


    private ProjectGraphTestBuilder() {
    }

    public static ProjectGraphTestBuilder graph() {
        return new ProjectGraphTestBuilder();
    }


    public ProjectGraphTestBuilder addNode(String classId) {
        int separator = classId.lastIndexOf('.');

        String packageName = separator < 0
                ? ""
                : classId.substring(0, separator);

        String className = separator < 0
                ? classId
                : classId.substring(separator + 1);

        ClassNode node = ClassNode.from(
                TestFixtures.createClass(
                        packageName,
                        className
                )
        );

        nodes.add(node);
        return this;
    }

    public ProjectGraphTestBuilder addNode(ClassNode node) {
        nodes.add(node);
        return this;
    }

    public ProjectGraphTestBuilder addDependency(
            String sourceId,
            String targetId
    ) {

        return addDependency(
                sourceId,
                targetId,
                DependencyType.FIELD
        );
    }
    public ProjectGraphTestBuilder addDependency(String sourceId, String targetId, DependencyType type) {
        dependencies.add(
                new ClassDependency(
                        sourceId,
                        targetId,
                        type
                )
        );
        return this;
    }

    public ProjectGraph build() {
        return new ProjectGraph(nodes, dependencies);
    }
}

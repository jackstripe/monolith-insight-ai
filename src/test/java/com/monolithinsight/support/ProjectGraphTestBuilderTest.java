package com.monolithinsight.support;

import com.monolithinsight.domain.ClassDependency;
import com.monolithinsight.domain.ClassNode;
import com.monolithinsight.domain.DependencyType;
import com.monolithinsight.domain.ProjectGraph;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class ProjectGraphTestBuilderTest {
    @Test
    void shouldBuildGraphWithNodesAndDependencies() {
        String source = "com.example.orders.OrderService";
        String target = "com.example.shared.AuditService";

        ProjectGraph graph = ProjectGraphTestBuilder.graph()
                .addNode(source)
                .addNode(target)
                .addDependency(
                        source,
                        target,
                        DependencyType.CONSTRUCTOR
                )
                .build();

        assertThat(graph.nodes())
                .extracting(ClassNode::id)
                .containsExactly(source, target);

        assertThat(graph.dependencies())
                .containsExactly(
                        new ClassDependency(
                                source,
                                target,
                                DependencyType.CONSTRUCTOR
                        )
                );
    }

}

package com.monolithinsight.application;

import com.monolithinsight.domain.ClassNode;
import com.monolithinsight.domain.DependencyPath;
import com.monolithinsight.domain.ProjectGraph;
import com.monolithinsight.support.ProjectGraphTestBuilder;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class FindShortestDependencyPathUseCaseTest {

    @Test
    public void shouldFindShortestPath(){


        ProjectGraph graph = ProjectGraphTestBuilder.graph()
                .addNode("A")
                .addNode("B")
                .addNode("C")
                .addNode("D")
                .addNode("E")
                .addNode("F")
                .addDependency("A", "B")
                .addDependency("A", "C")
                .addDependency("B", "D")
                .addDependency("C", "E")
                .addDependency("D", "F")
                .addDependency("E", "F")
                .addDependency("C", "F")
                .addDependency("D", "B")
                .build();

        Optional<DependencyPath> result =
                new FindShortestDependencyPathUseCase()
                        .execute(graph, "A", "F");

        assertThat(result)
                .isPresent()
                .get()
                .extracting(path ->
                        path.classes().stream()
                                .map(ClassNode::id)
                                .toList()
                )
                .isEqualTo(List.of("A", "C", "F"));

    }
}

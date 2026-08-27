package com.monolithinsight.application;

import com.monolithinsight.domain.ClassNode;
import com.monolithinsight.domain.DependencyPath;
import com.monolithinsight.domain.ProjectGraph;
import com.monolithinsight.support.ProjectGraphTestBuilder;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class FindShortestDependencyPathUseCaseTest {

    @Test
    void shouldFindShortestPath(){

        ProjectGraph graph = ProjectGraphTestBuilder.graph()
                .addNode("A")
                .addNode("B")
                .addNode("C")
                .addNode("D")
                .addNode("E")
                .addNode("F")
                .addDependency("A", "B")
                .addDependency("A", "C")
                .addDependency("B", "C")
                .addDependency("B", "D")
                .addDependency("C", "E")
                .addDependency("D", "F")
                .addDependency("E", "F")
                .addDependency("C", "F")
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

    @Test
    void shouldReturnEmptyWhenNoPathExists(){
        ProjectGraph graph = ProjectGraphTestBuilder.graph()
                .addNode("A")
                .addNode("B")
                .addNode("C")
                .addNode("D")
                .addNode("E")
                .addNode("F")
                .addNode("G")
                .addNode("H")
                .addDependency("A", "B")
                .addDependency("A", "C")
                .addDependency("B", "D")
                .addDependency("C", "E")
                .addDependency("D", "F")
                .addDependency("E", "F")
                .addDependency("C", "F")
                .addDependency("D", "F")
                .addDependency("G", "H")
                .build();

        Optional<DependencyPath> result =
                new FindShortestDependencyPathUseCase()
                        .execute(graph, "A", "H");

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnTrivialPathWhenSourceEqualsTarget(){
        ProjectGraph graph = ProjectGraphTestBuilder.graph()
                .addNode("A")
                .addNode("B")
                .addNode("C")
                .addNode("D")
                .addNode("E")
                .addNode("F")
                .addDependency("A", "B")
                .addDependency("A", "C")
                .addDependency("B", "C")
                .addDependency("B", "D")
                .addDependency("C", "E")
                .addDependency("D", "F")
                .addDependency("E", "F")
                .addDependency("C", "F")
                .addDependency("D", "F")
                .build();

        Optional<DependencyPath> result =
                new FindShortestDependencyPathUseCase()
                        .execute(graph, "A", "A");

        assertThat(result)
                .isPresent()
                .get()
                .extracting(path ->
                        path.classes().stream()
                                .map(ClassNode::id)
                                .toList()
                )
                .isEqualTo(List.of("A"));
    }


    @Test
    void shouldRejectUnknownSourceClass(){
        ProjectGraph graph = ProjectGraphTestBuilder.graph()
                .addNode("A")
                .addNode("B")
                .addNode("C")
                .addNode("D")
                .addNode("E")
                .addNode("F")
                .addDependency("A", "B")
                .addDependency("A", "C")
                .addDependency("B", "C")
                .addDependency("B", "D")
                .addDependency("C", "E")
                .addDependency("D", "F")
                .addDependency("E", "F")
                .addDependency("C", "F")
                .addDependency("D", "F")
                .build();

        assertThatThrownBy(() ->
                new FindShortestDependencyPathUseCase()
                        .execute(graph, "FALSE", "B")
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Class not found in graph: " + "FALSE");
    }

    @Test
    void shouldRejectUnknownTargetClass(){

        ProjectGraph graph = ProjectGraphTestBuilder.graph()
                .addNode("A")
                .addNode("B")
                .addNode("C")
                .addNode("D")
                .addNode("E")
                .addNode("F")
                .addDependency("A", "B")
                .addDependency("A", "C")
                .addDependency("B", "C")
                .addDependency("B", "D")
                .addDependency("C", "E")
                .addDependency("D", "F")
                .addDependency("E", "F")
                .addDependency("C", "F")
                .addDependency("D", "F")
                .build();

        assertThatThrownBy(() ->
                new FindShortestDependencyPathUseCase()
                        .execute(graph, "A", "FALSE")
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Class not found in graph: " + "FALSE");
    }

    @Test
    void shouldHandleCycles(){

        ProjectGraph graph = ProjectGraphTestBuilder.graph()
                .addNode("A")
                .addNode("B")
                .addNode("C")
                .addNode("D")
                .addNode("E")
                .addNode("F")
                .addDependency("A", "B")
                .addDependency("A", "C")
                .addDependency("B", "C")
                .addDependency("B", "A")
                .addDependency("C", "A")
                .addDependency("D", "F")
                .addDependency("E", "F")
                .addDependency("C", "F")
                .addDependency("D", "F")
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

    @Test
    void shouldChooseDeterministicPathWhenMultipleShortestPathsExist(){

        ProjectGraph graph = ProjectGraphTestBuilder.graph()
                .addNode("A")
                .addNode("B")
                .addNode("C")
                .addNode("D")
                .addDependency("A", "C")
                .addDependency("A", "B")
                .addDependency("C", "D")
                .addDependency("B", "D")
                .build();

        Optional<DependencyPath> result =
                new FindShortestDependencyPathUseCase()
                        .execute(graph, "A", "D");

        assertThat(result)
                .isPresent()
                .get()
                .extracting(path ->
                        path.classes().stream()
                                .map(ClassNode::id)
                                .toList()
                )
                .isEqualTo(List.of("A", "B", "D"));
    }


}

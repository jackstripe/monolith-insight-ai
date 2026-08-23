package com.monolithinsight.application;

import com.monolithinsight.domain.ClassNode;
import com.monolithinsight.domain.DependencyPathsReport;
import com.monolithinsight.domain.DependencyType;
import com.monolithinsight.domain.ProjectGraph;
import com.monolithinsight.support.ProjectGraphTestBuilder;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


class FindDependencyPathsUseCaseTest {


    @Test
    void shouldFindAllDependencyPathsBetweenClasses(){
        //Arrange
        ProjectGraph graph = ProjectGraphTestBuilder.graph()
                .addNode("A")
                .addNode("B")
                .addNode("C")
                .addNode("D")
                .addNode("E")
                .addDependency("A", "B")
                .addDependency("A", "C")
                .addDependency("B", "C")
                .addDependency("B", "D")
                .addDependency("C", "D")
                .addDependency("D", "E")
                .build();

        //Action
        DependencyPathsReport dependencyPathsReport =
                new FindDependencyPathsUseCase().execute(graph, "A", "E");

        //Assert
        assertThat(dependencyPathsReport.paths())
                .extracting(
                        path -> path.classes().stream()
                                .map(ClassNode::id)
                                .toList())
                .containsExactly(
                        List.of("A", "B", "C", "D", "E"),
                        List.of("A", "B", "D", "E"),
                        List.of("A", "C", "D", "E")
                );
    }

    @Test
    void shouldHandleCyclesAndReturnOnlySimplePaths(){
        //Arrange
        ProjectGraph graph = ProjectGraphTestBuilder.graph()
                .addNode("A")
                .addNode("B")
                .addNode("C")
                .addNode("D")
                .addNode("E")
                .addDependency("A", "B")
                .addDependency("A", "C")
                .addDependency("B", "C")
                .addDependency("B", "D")
                .addDependency("D", "B")
                .addDependency("C", "D")
                .addDependency("D", "E")
                .build();

        //Action
        DependencyPathsReport dependencyPathsReport =
                new FindDependencyPathsUseCase().execute(graph, "A", "E");

        //Assert
        assertThat(dependencyPathsReport.paths())
                .extracting(
                        path -> path.classes().stream()
                                .map(ClassNode::id)
                                .toList())
                .containsExactly(
                        List.of("A", "B", "C", "D", "E"),
                        List.of("A", "B", "D", "E"),
                        List.of("A", "C", "D", "E")
                );
    }

    @Test
    void shouldReturnEmptyReportWhenNoPathExists(){
        //Arrange
        ProjectGraph graph = ProjectGraphTestBuilder.graph()
                .addNode("A")
                .addNode("B")
                .addNode("C")
                .addDependency("A", "B")
                .build();

        DependencyPathsReport report =
                new FindDependencyPathsUseCase()
                        .execute(graph, "B", "C");

        assertThat(report.paths()).isEmpty();
    }

    @Test
    void shouldReturnTrivialPathWhenSourceEqualsTarget(){
        //Arrange
        ProjectGraph graph = ProjectGraphTestBuilder.graph()
                .addNode("A")
                .addNode("B")
                .addNode("C")
                .addDependency("A", "B")
                .build();

        DependencyPathsReport dependencyPathsReport =
                new FindDependencyPathsUseCase().execute(graph, "A", "A");

        assertThat(dependencyPathsReport.paths())
                .extracting(
                        path -> path.classes().stream()
                                .map(ClassNode::id)
                                .toList())
                .containsExactly(
                        List.of("A")
                );
    }

    @Test
    void shouldRejectUnknownTargetClass(){
        ProjectGraph graph = ProjectGraphTestBuilder.graph()
                .addNode("A")
                .addNode("B")
                .addNode("C")
                .addDependency("A", "B")
                .build();

        assertThatThrownBy(() ->
                new FindDependencyPathsUseCase()
                        .execute(graph, "A", "Unknown")
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Class not found in graph: " + "Unknown");


    }

    @Test
    void shouldRejectUnknownSourceClass(){




        ProjectGraph graph = ProjectGraphTestBuilder.graph()
                .addNode("A")
                .addNode("B")
                .addNode("C")
                .addDependency("A", "B")
                .build();


        assertThatThrownBy(() ->
                new FindDependencyPathsUseCase()
                        .execute(graph, "Unknown", "A")
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Class not found in graph: " + "Unknown");
    }

    @Test
    void shouldNotDuplicatePathsForDifferentDependencyTypes(){
        ProjectGraph graph = ProjectGraphTestBuilder.graph()
                .addNode("A")
                .addNode("B")
                .addNode("C")
                .addDependency("A", "B", DependencyType.FIELD)
                .addDependency("A", "B", DependencyType.CONSTRUCTOR)
                .build();

        DependencyPathsReport dependencyPathsReport =
                new FindDependencyPathsUseCase().execute(graph, "A", "B");


        assertThat(dependencyPathsReport.paths())
                .extracting(
                        path -> path.classes().stream()
                                .map(ClassNode::id)
                                .toList())
                .containsExactly(
                        List.of("A", "B")
                );
    }
}

package com.monolithinsight.application;

import com.monolithinsight.domain.ClassNode;
import com.monolithinsight.domain.DependencyPathsReport;
import com.monolithinsight.domain.ProjectGraph;
import com.monolithinsight.support.ProjectGraphTestBuilder;
import org.junit.jupiter.api.Test;

import java.util.List;

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



        DependencyPathsReport dependencyPathsReport =  new FindDependencyPathsUseCase().execute(graph, "A", "E");
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
}

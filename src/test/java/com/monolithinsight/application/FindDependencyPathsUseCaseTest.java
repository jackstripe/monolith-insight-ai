package com.monolithinsight.application;

import com.monolithinsight.domain.ClassNode;
import com.monolithinsight.domain.DependencyPath;
import com.monolithinsight.domain.DependencyPathsReport;
import com.monolithinsight.domain.ProjectGraph;
import com.monolithinsight.support.ProjectGraphTestBuilder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


public class FindDependencyPathsUseCaseTest {


    @Test
    void shouldReturnDependencyPathReport(){
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
                .extracting(DependencyPath::classes)
                .extracting(ClassNode::id)
                .containsExactly(
                        tuple(
                        "A",
                                "B",
                                "C",
                                "D",
                                "E"),
                        tuple(
                                "A",
                                "B",
                                "D",
                                "E"),
                        tuple(
                                "A",
                                "C",
                                "D",
                                "E")
                );
    }
}

package com.monolithinsight.application;

import com.monolithinsight.domain.ClassNode;
import com.monolithinsight.domain.ImpactAnalysisReport;
import com.monolithinsight.domain.ProjectGraph;
import com.monolithinsight.support.ProjectGraphTestBuilder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class AnalyzeImpactUseCaseTest {

    @Test
    void shouldFindDirectAndTransitiveImpactedClasses() {

        ProjectGraph graph = ProjectGraphTestBuilder.graph()
                .addNode("OrderController")
                .addNode("BatchProcessor")
                .addNode("OrderService")
                .addNode("AuditService")
                .addNode("LegacyHelper")
                .addDependency("OrderController", "OrderService")
                .addDependency("BatchProcessor", "OrderService")
                .addDependency("OrderService", "AuditService")
                .addDependency("AuditService", "LegacyHelper")
                .build();

        ImpactAnalysisReport report =
                new AnalyzeImpactUseCase().execute(graph, "AuditService");

        assertThat(report.impactedClasses())
                .extracting(ClassNode::id)
                .containsExactly(
                        "BatchProcessor",
                        "OrderController",
                        "OrderService"
                );
    }

    @Test
    void shouldReturnEmptyReportWhenNoClassesDependOnChangedClass(){

        ProjectGraph graph = ProjectGraphTestBuilder.graph()
                .addNode("OrderController")
                .addNode("OrderService")
                .addDependency("OrderController", "OrderService")
                .build();

        ImpactAnalysisReport report =
                new AnalyzeImpactUseCase().execute(graph, "OrderController");

        assertThat(report.impactedClasses())
                .extracting(ClassNode::id)
                .isEmpty();
    }

    @Test
    void shouldRejectUnknownChangedClass() {

        ProjectGraph graph = ProjectGraphTestBuilder.graph()
                .addNode("OrderController")
                .addNode("BatchProcessor")
                .addNode("OrderService")
                .addNode("AuditService")
                .addNode("LegacyHelper")
                .addDependency("OrderController", "OrderService")
                .addDependency("BatchProcessor", "OrderService")
                .addDependency("OrderService", "AuditService")
                .addDependency("AuditService", "LegacyHelper")
                .build();

        String unknownClassId = "UnknownService";
        assertThatThrownBy(() ->
                new AnalyzeImpactUseCase()
                        .execute(graph, unknownClassId)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Class not found in graph: " + unknownClassId);
    }

    @Test
    void shouldHandleCycleWithoutIncludingChangedClass() {
        ProjectGraph graph = ProjectGraphTestBuilder.graph()
                .addNode("OrderController")
                .addNode("BatchProcessor")
                .addNode("OrderService")
                .addDependency("OrderController", "BatchProcessor")
                .addDependency("BatchProcessor", "OrderService")
                .addDependency("OrderService", "OrderController")
                .build();

        ImpactAnalysisReport report =
                new AnalyzeImpactUseCase().execute(graph, "BatchProcessor");

        assertThat(report.impactedClasses())
                .extracting(ClassNode::id)
                .containsExactly(
                        "OrderController",
                        "OrderService"
                );
    }
}

package com.monolithinsight.application;

import com.monolithinsight.domain.ClassNode;
import com.monolithinsight.domain.ImpactAnalysisReport;
import com.monolithinsight.domain.ProjectGraph;
import com.monolithinsight.support.ProjectGraphTestBuilder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class AnalyzeImpactUseCaseTest {

    @Test
    void shouldFindDirectAndTransitiveImpactedClasses() {
        ProjectGraph graph = new ProjectGraphTestBuilder()
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
}

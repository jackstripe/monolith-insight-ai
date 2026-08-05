package com.monolithinsight.application;

import com.monolithinsight.domain.ClassNode;
import com.monolithinsight.domain.ProjectGraph;
import com.monolithinsight.domain.ReachabilityReport;
import com.monolithinsight.support.TestFixtures;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class FindReachableClassesUseCaseTest {


    @Test
    void shouldFindDirectAndTransitiveReachableClasses() {
        // Arrange

        ProjectGraph projectGraph = TestFixtures.createReachabilityGraph();
        String classIdOrigin = "com.example.orders.OrderController";
        // Act

        ReachabilityReport reachabilityReport =  new FindReachableClassesUseCase().execute(projectGraph,classIdOrigin);
        // Assert

        assertThat(reachabilityReport.classes())
                .extracting(
                        ClassNode::id)
                .containsExactly(
                        "com.example.inventory.InventoryService",
                        "com.example.legacy.LegacyHelper",
                        "com.example.orders.OrderService",
                        "com.example.shared.AuditService"

                );
    }
}

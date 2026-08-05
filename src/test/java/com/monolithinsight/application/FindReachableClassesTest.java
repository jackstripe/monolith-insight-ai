package com.monolithinsight.application;

import com.monolithinsight.domain.ClassNode;
import com.monolithinsight.domain.PackageMetrics;
import com.monolithinsight.domain.ProjectGraph;
import com.monolithinsight.support.TestFixtures;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class FindReachableClassesTest {


    @Test
    void shouldFindDirectAndTransitiveReachableClasses() {
        // Arrange

        ProjectGraph projectGraph = TestFixtures.createGraphTest();
        String classIdOrigin = "OrderService";
        // Act

        ReachabilityReport reachabilityReport =  new AnalyzeBFSReachableClasses().execute(projectGraph,classIdOrigin);
        // Assert

        assertThat(reachabilityReport.classes())
                .extracting(
                        ClassNode::id)
                .containsExactly(
                        "OrderController",
                        "OrderService",
                        "InventoryService",
                        "AuditService",
                        "legacyHelper"
                );
    }
}

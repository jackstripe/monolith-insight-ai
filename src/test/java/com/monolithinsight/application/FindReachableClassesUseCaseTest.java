package com.monolithinsight.application;

import com.monolithinsight.domain.ClassNode;
import com.monolithinsight.domain.DependencyType;
import com.monolithinsight.domain.ProjectGraph;
import com.monolithinsight.domain.ReachabilityReport;
import com.monolithinsight.support.ProjectGraphTestBuilder;
import com.monolithinsight.support.TestFixtures;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class FindReachableClassesUseCaseTest {


    private static final String ORDER_CONTROLLER = "com.example.orders.OrderController";
    private static final String LEGACY_HELPER = "com.example.legacy.LegacyHelper";
    private static final String INVENTORY_SERVICE = "com.example.inventory.InventoryService";
    private static final String OUTSIDER_CLASS = "com.example.legacy.monitor.OutsiderClass";
    private static final String ORDER_SERVICE = "com.example.orders.OrderService";
    private static final String AUDIT_SERVICE = "com.example.shared.AuditService";

    @Test
    void shouldFindDirectAndTransitiveReachableClasses() {
        // Arrange
        ProjectGraph projectGraph = reachabilityGraph().build();

        // Act
        ReachabilityReport reachabilityReport =  new FindReachableClassesUseCase().execute(projectGraph, ORDER_CONTROLLER);

        // Assert
        assertThat(reachabilityReport.classes())
                .extracting(
                        ClassNode::id)
                .containsExactly(
                        INVENTORY_SERVICE,
                        LEGACY_HELPER,
                        ORDER_SERVICE,
                        AUDIT_SERVICE

                );
    }

    @Test
    void shouldReturnEmptyReportForClassWithoutOutgoingDependencies() {

        // Arrange
        ProjectGraph projectGraph = reachabilityGraph().build();

        // Act
        ReachabilityReport reachabilityReport =  new FindReachableClassesUseCase().execute(projectGraph,LEGACY_HELPER);
        // Assert

        assertThat(reachabilityReport.classes())
                .extracting(
                        ClassNode::id)
                .isEmpty();
    }

    @Test
    void shouldRejectUnknownStartingClass() {
        ProjectGraph graph = ProjectGraphTestBuilder.graph()
                .addNode(ORDER_SERVICE)
                .build();

        assertThatThrownBy(() ->
                new FindReachableClassesUseCase()
                        .execute(graph, OUTSIDER_CLASS)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "Class not found in graph: "
                                + OUTSIDER_CLASS
                );
    }

    @Test
    void shouldHandleCycleWithoutIncludingStartingClass() {

        ProjectGraph graph = ProjectGraphTestBuilder.graph()
                .addNode(ORDER_SERVICE)
                .addNode(AUDIT_SERVICE)
                .addNode(LEGACY_HELPER)
                .addDependency(
                        ORDER_SERVICE,
                        AUDIT_SERVICE
                )
                .addDependency(
                        AUDIT_SERVICE,
                        LEGACY_HELPER
                )
                .addDependency(
                        LEGACY_HELPER,
                        ORDER_SERVICE,
                        DependencyType.CONSTRUCTOR
                )
                .build();

        ReachabilityReport report =
                new FindReachableClassesUseCase()
                        .execute(graph, ORDER_SERVICE);

        assertThat(report.classes())
                .extracting(ClassNode::id)
                .containsExactly(
                        LEGACY_HELPER,
                        AUDIT_SERVICE
                );
    }

    @Test
    void shouldIgnoreDisconnectedClasses(){

        ProjectGraph projectGraph = reachabilityGraph()
                .addNode(OUTSIDER_CLASS)
                .build();

        // Act

        ReachabilityReport reachabilityReport =  new FindReachableClassesUseCase().execute(projectGraph, ORDER_CONTROLLER);
        // Assert

        assertThat(reachabilityReport.classes())
                .extracting(
                        ClassNode::id)
                .containsExactly(
                        INVENTORY_SERVICE,
                        LEGACY_HELPER,
                        ORDER_SERVICE,
                        AUDIT_SERVICE
                );
    }

    private ProjectGraphTestBuilder reachabilityGraph() {
        return ProjectGraphTestBuilder.graph()
                .addNode(ORDER_CONTROLLER)
                .addNode(ORDER_SERVICE)
                .addNode(INVENTORY_SERVICE)
                .addNode(AUDIT_SERVICE)
                .addNode(LEGACY_HELPER)
                .addDependency(ORDER_CONTROLLER, ORDER_SERVICE)
                .addDependency(ORDER_CONTROLLER, INVENTORY_SERVICE)
                .addDependency(ORDER_SERVICE, AUDIT_SERVICE)
                .addDependency(INVENTORY_SERVICE, AUDIT_SERVICE)
                .addDependency(AUDIT_SERVICE, LEGACY_HELPER)
                .addDependency(ORDER_SERVICE, LEGACY_HELPER);
    }
}

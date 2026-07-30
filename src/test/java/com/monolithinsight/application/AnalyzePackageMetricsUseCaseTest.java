package com.monolithinsight.application;

import com.monolithinsight.domain.*;
import com.monolithinsight.support.TestFixtures;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class AnalyzePackageMetricsUseCaseTest {

    @Test
    void shouldCalculatePackageMetrics(){
        ClassNode orderService =
                ClassNode.from(
                        TestFixtures.createClass(
                                "com.example.orders",
                                "OrderService"
                        )
                );
        ClassNode auditService =
                ClassNode.from(
                        TestFixtures.createClass(
                                "com.example.shared",
                                "AuditService"
                        )
                );
        ClassNode orderController =
                ClassNode.from(
                        TestFixtures.createClass(
                                "com.example.orders",
                                "OrderController"
                        )
                );

        ClassNode inventoryService =
                ClassNode.from(
                        TestFixtures.createClass(
                                "com.example.inventory",
                                "InventoryService"
                        )
                );
        ClassNode legacyHelper =
                ClassNode.from(
                        TestFixtures.createClass(
                                "com.example.legacy",
                                "LegacyHelper"
                        )
                );


        ProjectGraph graph = new ProjectGraph(
                List.of(
                        orderService,
                        orderController,
                        inventoryService,
                        auditService,
                        legacyHelper
                ),
                List.of(
                        new ClassDependency(
                                orderController.id(),
                                orderService.id(),
                                DependencyType.FIELD
                        ),
                        new ClassDependency(
                                orderService.id(),
                                inventoryService.id(),
                                DependencyType.FIELD
                        ),
                        new ClassDependency(
                                auditService.id(),
                                orderService.id(),
                                DependencyType.CONSTRUCTOR
                        )
                )
        );

        PackageMetricsReport aCase = new AnalyzePackageMetricsUseCase().execute(graph);

        assertThat(aCase.packages())
                .extracting(
                        PackageMetrics::packageName,
                        PackageMetrics::classCount,
                        PackageMetrics::incomingDependencies,
                        PackageMetrics::outgoingDependencies,
                        PackageMetrics::totalDependencies,
                        PackageMetrics::averageCoupling
                )
                .containsExactlyInAnyOrder(
                        tuple(
                                "com.example.inventory",
                                1,
                                1,
                                0,
                                1,
                                1.0
                        ),
                        tuple(
                                "com.example.legacy",
                                1,
                                0,
                                0,
                                0,
                                0.0
                        ),
                        tuple(
                                "com.example.orders",
                                2,
                                1,
                                1,
                                2,
                                1.0
                        ),
                        tuple(
                                "com.example.shared",
                                1,
                                0,
                                1,
                                1,
                                1.0
                        )

                );
    }

    @Test
    void shouldIgnoreDependenciesWithinSamePackage(){

    }

    @Test
    void shouldIncludePackageWithoutExternalDependencies(){

    }

    @Test
    void shouldReturnEmptyReportForEmptyGraph(){

    }
}

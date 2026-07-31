package com.monolithinsight.application;

import com.monolithinsight.domain.ClassDependency;
import com.monolithinsight.domain.ClassNode;
import com.monolithinsight.domain.DependencyType;
import com.monolithinsight.domain.ProjectGraph;
import com.monolithinsight.domain.PackageMetricsReport;
import com.monolithinsight.domain.PackageMetrics;
import com.monolithinsight.support.TestFixtures;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class AnalyzePackageMetricsUseCaseTest {

    @Test
    void shouldCalculatePackageMetrics() {
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

        PackageMetricsReport report = new AnalyzePackageMetricsUseCase().execute(graph);

        assertThat(report.packages())
                .extracting(
                        PackageMetrics::packageName,
                        PackageMetrics::classCount,
                        PackageMetrics::incomingDependencies,
                        PackageMetrics::outgoingDependencies,
                        PackageMetrics::totalDependencies,
                        PackageMetrics::averageCoupling
                )
                .containsExactly(
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
    void shouldIgnoreDependenciesWithinSamePackage() {
        ClassNode controller = ClassNode.from(
                TestFixtures.createClass(
                        "com.example.orders",
                        "OrderController"
                )
        );

        ClassNode service = ClassNode.from(
                TestFixtures.createClass(
                        "com.example.orders",
                        "OrderService"
                )
        );

        ProjectGraph graph = new ProjectGraph(
                List.of(controller, service),
                List.of(
                        new ClassDependency(
                                controller.id(),
                                service.id(),
                                DependencyType.FIELD
                        )
                )
        );

        PackageMetricsReport report =
                new AnalyzePackageMetricsUseCase().execute(graph);

        assertThat(report.packages())
                .extracting(
                        PackageMetrics::packageName,
                        PackageMetrics::classCount,
                        PackageMetrics::incomingDependencies,
                        PackageMetrics::outgoingDependencies,
                        PackageMetrics::totalDependencies,
                        PackageMetrics::averageCoupling
                )
                .containsExactly(
                        tuple(
                                "com.example.orders",
                                2,
                                0,
                                0,
                                0,
                                0.0
                        )
                );
    }

    @Test
    void shouldIncludePackageWithoutExternalDependencies() {
        ClassNode legacyHelper = ClassNode.from(
                TestFixtures.createClass(
                        "com.example.legacy",
                        "LegacyHelper"
                )
        );

        ProjectGraph graph = new ProjectGraph(
                List.of(legacyHelper),
                List.of()
        );

        PackageMetricsReport report =
                new AnalyzePackageMetricsUseCase().execute(graph);

        assertThat(report.packages())
                .extracting(
                        PackageMetrics::packageName,
                        PackageMetrics::classCount,
                        PackageMetrics::incomingDependencies,
                        PackageMetrics::outgoingDependencies
                )
                .containsExactly(
                        tuple(
                                "com.example.legacy",
                                1,
                                0,
                                0
                        )
                );
    }

    @Test
    void shouldReturnEmptyReportForEmptyGraph() {
        ProjectGraph graph = new ProjectGraph(
                List.of(),
                List.of()
        );

        PackageMetricsReport report =
                new AnalyzePackageMetricsUseCase().execute(graph);

        assertThat(report.packages()).isEmpty();
    }
}

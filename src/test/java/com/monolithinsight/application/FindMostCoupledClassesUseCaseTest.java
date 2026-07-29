package com.monolithinsight.application;

import com.monolithinsight.domain.*;
import com.monolithinsight.support.TestFixtures;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class FindMostCoupledClassesUseCaseTest {

    @Test
    void shouldCalculateMostCoupledClasses() {
        ClassNode orderService =
                ClassNode.from(
                        TestFixtures.createClass(
                                "com.example",
                                "OrderService"
                        )
                );

        ClassNode orderRepository =
                ClassNode.from(
                        TestFixtures.createClass(
                                "com.example",
                                "OrderRepository"
                        )
                );

        ClassNode orderController =
                ClassNode.from(
                        TestFixtures.createClass(
                                "com.example",
                                "OrderController"
                        )
                );

        ProjectGraph graph = new ProjectGraph(
                List.of(
                        orderService,
                        orderRepository,
                        orderController
                ),
                List.of(
                        new ClassDependency(
                                orderController.id(),
                                orderService.id(),
                                DependencyType.FIELD
                        ),
                        new ClassDependency(
                                orderService.id(),
                                orderRepository.id(),
                                DependencyType.CONSTRUCTOR
                        )
                )
        );

        GraphMetrics metrics =
                new AnalyzeGraphMetricsUseCase()
                        .execute(graph);

        CouplingReport report = new FindMostCoupledClassesUseCase().execute(metrics,2);

        assertThat(report.classes())
                .extracting(
                        CoupledClass::classId,
                        CoupledClass::incomingDependencies,
                        CoupledClass::outgoingDependencies,
                        CoupledClass::totalDependencies
                )
                .containsExactly(
                        tuple("com.example.OrderService",
                                1,
                                1,
                                2),
                        tuple("com.example.OrderController",
                                0,
                                1,
                                1)

                        );
    }

    @Test
    void shouldCalculateMostCoupledClassesLimitGreaterThanSize() {
        ClassNode orderService =
                ClassNode.from(
                        TestFixtures.createClass(
                                "com.example",
                                "OrderService"
                        )
                );

        ClassNode orderRepository =
                ClassNode.from(
                        TestFixtures.createClass(
                                "com.example",
                                "OrderRepository"
                        )
                );

        ClassNode orderController =
                ClassNode.from(
                        TestFixtures.createClass(
                                "com.example",
                                "OrderController"
                        )
                );

        ProjectGraph graph = new ProjectGraph(
                List.of(
                        orderService,
                        orderRepository,
                        orderController
                ),
                List.of(
                        new ClassDependency(
                                orderController.id(),
                                orderService.id(),
                                DependencyType.FIELD
                        ),
                        new ClassDependency(
                                orderService.id(),
                                orderRepository.id(),
                                DependencyType.CONSTRUCTOR
                        )
                )
        );

        GraphMetrics metrics =
                new AnalyzeGraphMetricsUseCase()
                        .execute(graph);

        CouplingReport report = new FindMostCoupledClassesUseCase().execute(metrics,4);

        assertThat(report.classes())
                .extracting(
                        CoupledClass::classId,
                        CoupledClass::incomingDependencies,
                        CoupledClass::outgoingDependencies,
                        CoupledClass::totalDependencies
                )
                .containsExactly(
                        tuple("com.example.OrderService",
                                1,
                                1,
                                2),
                        tuple("com.example.OrderController",
                                0,
                                1,
                                1),
                        tuple("com.example.OrderRepository",
                                1,
                                0,
                                1)

                );
    }

    @Test
    void shouldCalculateMostCoupledClassesLimitIsZero() {
        ClassNode orderService =
                ClassNode.from(
                        TestFixtures.createClass(
                                "com.example",
                                "OrderService"
                        )
                );

        ClassNode orderRepository =
                ClassNode.from(
                        TestFixtures.createClass(
                                "com.example",
                                "OrderRepository"
                        )
                );

        ClassNode orderController =
                ClassNode.from(
                        TestFixtures.createClass(
                                "com.example",
                                "OrderController"
                        )
                );

        ProjectGraph graph = new ProjectGraph(
                List.of(
                        orderService,
                        orderRepository,
                        orderController
                ),
                List.of(
                        new ClassDependency(
                                orderController.id(),
                                orderService.id(),
                                DependencyType.FIELD
                        ),
                        new ClassDependency(
                                orderService.id(),
                                orderRepository.id(),
                                DependencyType.CONSTRUCTOR
                        )
                )
        );

        GraphMetrics metrics =
                new AnalyzeGraphMetricsUseCase()
                        .execute(graph);

        CouplingReport report = new FindMostCoupledClassesUseCase().execute(metrics,0);

        assertThat(report.classes())
                .extracting(
                        CoupledClass::classId,
                        CoupledClass::incomingDependencies,
                        CoupledClass::outgoingDependencies,
                        CoupledClass::totalDependencies
                ).isEmpty();
    }
}

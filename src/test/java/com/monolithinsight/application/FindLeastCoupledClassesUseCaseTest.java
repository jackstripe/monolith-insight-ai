package com.monolithinsight.application;

import com.monolithinsight.domain.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class FindLeastCoupledClassesUseCaseTest {


    @Test
    void shouldFindLeastCoupledClassesOrderedByTotalAndClassId() {

        GraphMetrics metrics = new GraphMetrics(
                List.of(
                        new ClassMetrics(
                                "com.example.AuditService",
                                0,
                                0
                        ),
                        new ClassMetrics(
                                "com.example.OrderController",
                                0,
                                1
                        ),
                        new ClassMetrics(
                                "com.example.OrderRepository",
                                1,
                                0
                        ),
                        new ClassMetrics(
                                "com.example.OrderService",
                                1,
                                1
                        )
                )
        );

        CouplingReport report = new FindLeastCoupledClassesUseCase().execute(metrics,3);

        assertThat(report.classes())
                .extracting(
                        CoupledClass::classId,
                        CoupledClass::incomingDependencies,
                        CoupledClass::outgoingDependencies,
                        CoupledClass::totalDependencies
                )
                .containsExactly(
                        tuple("com.example.AuditService", 0, 0, 0),
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
    void shouldReturnAllClassesWhenLimitExceedsSize() {

        GraphMetrics metrics = new GraphMetrics(
                List.of(
                        new ClassMetrics(
                                "com.example.AuditService",
                                0,
                                0
                        ),
                        new ClassMetrics(
                                "com.example.OrderController",
                                0,
                                1
                        ),
                        new ClassMetrics(
                                "com.example.OrderRepository",
                                1,
                                0
                        ),
                        new ClassMetrics(
                                "com.example.OrderService",
                                1,
                                1
                        )
                )
        );

        CouplingReport report = new FindLeastCoupledClassesUseCase().execute(metrics,5);

        assertThat(report.classes())
                .extracting(
                        CoupledClass::classId,
                        CoupledClass::incomingDependencies,
                        CoupledClass::outgoingDependencies,
                        CoupledClass::totalDependencies
                )
                .containsExactly(
                        tuple("com.example.AuditService",
                                0,
                                0,
                                0),
                        tuple("com.example.OrderController",
                                0,
                                1,
                                1),
                        tuple("com.example.OrderRepository",
                                1,
                                0,
                                1),
                        tuple("com.example.OrderService",
                                1,
                                1,
                                2)

                );
    }

    @Test
    void shouldReturnEmptyReportWhenLimitIsZero() {

        GraphMetrics metrics = new GraphMetrics(
                List.of(

                        new ClassMetrics(
                                "com.example.OrderController",
                                0,
                                1
                        ),
                        new ClassMetrics(
                                "com.example.OrderRepository",
                                1,
                                0
                        ),
                        new ClassMetrics(
                                "com.example.OrderService",
                                1,
                                1
                        )
                )
        );

        CouplingReport report = new FindLeastCoupledClassesUseCase().execute(metrics,0);

        assertThat(report.classes())
                .extracting(
                        CoupledClass::classId,
                        CoupledClass::incomingDependencies,
                        CoupledClass::outgoingDependencies,
                        CoupledClass::totalDependencies
                ).isEmpty();
    }

    @Test
    void shouldReturnIsolatedClassFirst() {

        GraphMetrics metrics = new GraphMetrics(
                List.of(
                        new ClassMetrics(
                                "com.example.AuditService",
                                0,
                                0
                        ),
                        new ClassMetrics(
                                "com.example.OrderController",
                                0,
                                1
                        ),
                        new ClassMetrics(
                                "com.example.OrderRepository",
                                1,
                                0
                        ),
                        new ClassMetrics(
                                "com.example.OrderService",
                                1,
                                1
                        )
                )
        );

        CouplingReport report = new FindLeastCoupledClassesUseCase().execute(metrics,1);

        assertThat(report.classes())
                .extracting(
                        CoupledClass::classId,
                        CoupledClass::incomingDependencies,
                        CoupledClass::outgoingDependencies,
                        CoupledClass::totalDependencies
                ).containsExactly(
                        tuple("com.example.AuditService",
                                0,
                                0,
                                0)
                );
    }
}

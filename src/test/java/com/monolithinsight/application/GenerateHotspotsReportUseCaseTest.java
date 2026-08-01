package com.monolithinsight.application;

import com.monolithinsight.domain.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class GenerateHotspotsReportUseCaseTest {
    private final GenerateHotspotsReportUseCase useCase =
            new GenerateHotspotsReportUseCase();

    @Test
    void shouldClassifyHotspotsUsingBoundaryValues() {
        GraphMetrics metrics = new GraphMetrics(List.of(
                new ClassMetrics("ZeroDependencies", 0, 0),
                new ClassMetrics("LowBoundary", 2, 3),
                new ClassMetrics("MediumLowerBoundary", 1, 5),
                new ClassMetrics("MediumUpperBoundary", 4, 6),
                new ClassMetrics("HighBoundary", 7, 4)
        ));

        HotspotsReport report = useCase.execute(metrics);

        assertThat(report.hotspots())
                .extracting(
                        ClassHotspot::classId,
                        ClassHotspot::totalDependencies,
                        ClassHotspot::couplingLevel
                )
                .containsExactly(
                        tuple("HighBoundary", 11, CouplingLevel.HIGH),
                        tuple("MediumUpperBoundary", 10, CouplingLevel.MEDIUM),
                        tuple("MediumLowerBoundary", 6, CouplingLevel.MEDIUM),
                        tuple("LowBoundary", 5, CouplingLevel.LOW),
                        tuple("ZeroDependencies", 0, CouplingLevel.LOW)
                );
    }

    @Test
    void shouldSortByTotalDependenciesDescendingAndClassIdAscendingOnTie() {
        GraphMetrics metrics = new GraphMetrics(List.of(
                new ClassMetrics("com.example.ZetaService", 4, 4),
                new ClassMetrics("com.example.LowService", 1, 2),
                new ClassMetrics("com.example.HighService", 6, 6),
                new ClassMetrics("com.example.AlphaService", 3, 5)
        ));

        HotspotsReport report = useCase.execute(metrics);

        assertThat(report.hotspots())
                .extracting(ClassHotspot::classId)
                .containsExactly(
                        "com.example.HighService",
                        "com.example.AlphaService",
                        "com.example.ZetaService",
                        "com.example.LowService"
                );
    }

    @Test
    void shouldPreserveIncomingAndOutgoingDependencies() {
        GraphMetrics metrics = new GraphMetrics(List.of(
                new ClassMetrics("OrderService", 7, 4)
        ));

        HotspotsReport report = useCase.execute(metrics);

        assertThat(report.hotspots())
                .extracting(
                        ClassHotspot::classId,
                        ClassHotspot::incomingDependencies,
                        ClassHotspot::outgoingDependencies,
                        ClassHotspot::totalDependencies,
                        ClassHotspot::couplingLevel
                )
                .containsExactly(
                        tuple(
                                "OrderService",
                                7,
                                4,
                                11,
                                CouplingLevel.HIGH
                        )
                );
    }

    @Test
    void shouldReturnEmptyReportWhenGraphMetricsIsEmpty() {
        GraphMetrics metrics = new GraphMetrics(List.of());

        HotspotsReport report = useCase.execute(metrics);

        assertThat(report.hotspots()).isEmpty();
    }
}

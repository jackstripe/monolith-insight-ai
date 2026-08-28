package com.monolithinsight.domain;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class ArchitectureAnalysisReportTest {

    @Test
    void shouldComposeCompleteArchitectureAnalysis() {
        ProjectGraph graph = new ProjectGraph(List.of(), List.of());
        GraphMetrics graphMetrics = new GraphMetrics(List.of());
        CouplingReport mostCoupled = new CouplingReport(List.of());
        CouplingReport leastCoupled = new CouplingReport(List.of());
        PackageMetricsReport packageMetrics =
                new PackageMetricsReport(List.of());
        HotspotsReport hotspots = new HotspotsReport(List.of());

        ArchitectureAnalysisReport report =
                new ArchitectureAnalysisReport(
                        "order-service",
                        12,
                        List.of(),
                        graph,
                        graphMetrics,
                        mostCoupled,
                        leastCoupled,
                        packageMetrics,
                        hotspots
                );

        assertThat(report.projectName()).isEqualTo("order-service");
        assertThat(report.javaFileCount()).isEqualTo(12);
        assertThat(report.errors()).isEmpty();
        assertThat(report.graph()).isSameAs(graph);
        assertThat(report.graphMetrics()).isSameAs(graphMetrics);
        assertThat(report.mostCoupledClasses()).isSameAs(mostCoupled);
        assertThat(report.leastCoupledClasses()).isSameAs(leastCoupled);
        assertThat(report.packageMetrics()).isSameAs(packageMetrics);
        assertThat(report.hotspots()).isSameAs(hotspots);
    }

    @Test
    void shouldDefensivelyCopyAnalysisErrors() {
        List<AnalysisError> errors = new ArrayList<>();

        ArchitectureAnalysisReport report =
                new ArchitectureAnalysisReport(
                        "order-service",
                        12,
                        errors,
                        new ProjectGraph(List.of(), List.of()),
                        new GraphMetrics(List.of()),
                        new CouplingReport(List.of()),
                        new CouplingReport(List.of()),
                        new PackageMetricsReport(List.of()),
                        new HotspotsReport(List.of())
                );

        errors.add(
                new AnalysisError(
                        "OrderService.java",
                        "PARSE_ERROR",
                        "Unexpected token"
                )
        );

        assertThat(report.errors()).isEmpty();
    }

    @Test
    void shouldExposeAnalysisErrorsAsUnmodifiable() {
        AnalysisError error = new AnalysisError(
                "OrderService.java",
                "PARSE_ERROR",
                "Unexpected token"
        );

        ArchitectureAnalysisReport report =
                new ArchitectureAnalysisReport(
                        "order-service",
                        12,
                        List.of(error),
                        new ProjectGraph(List.of(), List.of()),
                        new GraphMetrics(List.of()),
                        new CouplingReport(List.of()),
                        new CouplingReport(List.of()),
                        new PackageMetricsReport(List.of()),
                        new HotspotsReport(List.of())
                );

        assertThatThrownBy(() ->
                report.errors().add(error)
        ).isInstanceOf(UnsupportedOperationException.class);
    }
}

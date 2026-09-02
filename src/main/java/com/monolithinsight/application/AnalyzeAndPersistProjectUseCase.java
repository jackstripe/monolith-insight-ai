package com.monolithinsight.application;

import com.monolithinsight.domain.*;

import java.time.Clock;
import java.util.UUID;

public class AnalyzeAndPersistProjectUseCase {

    private final AnalyzeProjectUseCase analyzeProjectUseCase;
    private final BuildProjectGraphUseCase buildProjectGraphUseCase;
    private final AnalyzeGraphMetricsUseCase analyzeGraphMetricsUseCase;
    private final FindMostCoupledClassesUseCase mostCoupledUseCase;
    private final FindLeastCoupledClassesUseCase leastCoupledUseCase;
    private final AnalyzePackageMetricsUseCase packageMetricsUseCase;
    private final GenerateHotspotsReportUseCase hotspotsUseCase;
    private final AnalysisSnapshotRepository repository;
    private final Clock clock;

    public AnalyzeAndPersistProjectUseCase(
            AnalyzeProjectUseCase analyzeProjectUseCase,
            BuildProjectGraphUseCase buildProjectGraphUseCase,
            AnalyzeGraphMetricsUseCase analyzeGraphMetricsUseCase,
            FindMostCoupledClassesUseCase mostCoupledUseCase,
            FindLeastCoupledClassesUseCase leastCoupledUseCase,
            AnalyzePackageMetricsUseCase packageMetricsUseCase,
            GenerateHotspotsReportUseCase hotspotsUseCase,
            AnalysisSnapshotRepository repository,
            Clock clock
    ) {
        this.analyzeProjectUseCase = analyzeProjectUseCase;
        this.buildProjectGraphUseCase = buildProjectGraphUseCase;
        this.analyzeGraphMetricsUseCase = analyzeGraphMetricsUseCase;
        this.mostCoupledUseCase = mostCoupledUseCase;
        this.leastCoupledUseCase = leastCoupledUseCase;
        this.packageMetricsUseCase = packageMetricsUseCase;
        this.hotspotsUseCase = hotspotsUseCase;
        this.repository = repository;
        this.clock = clock;
    }

    private static final int DEFAULT_COUPLING_LIMIT = 10;

    public AnalysisSnapshot execute(
            String projectId,
            String projectPath
    ) {
        ProjectAnalysis projectAnalysis =
                analyzeProjectUseCase.execute(projectPath);

        ProjectGraph graph =
                buildProjectGraphUseCase.execute(projectAnalysis);

        GraphMetrics graphMetrics =
                analyzeGraphMetricsUseCase.execute(graph);

        CouplingReport mostCoupledClasses =
                mostCoupledUseCase.execute(
                        graphMetrics,
                        DEFAULT_COUPLING_LIMIT
                );

        CouplingReport leastCoupledClasses =
                leastCoupledUseCase.execute(
                        graphMetrics,
                        DEFAULT_COUPLING_LIMIT
                );

        PackageMetricsReport packageMetrics =
                packageMetricsUseCase.execute(graph);

        HotspotsReport hotspots =
                hotspotsUseCase.execute(graphMetrics);

        ArchitectureAnalysisReport report =
                new ArchitectureAnalysisReport(
                        projectAnalysis.projectName(),
                        projectAnalysis.javaFileCount(),
                        projectAnalysis.errors(),
                        graph,
                        graphMetrics,
                        mostCoupledClasses,
                        leastCoupledClasses,
                        packageMetrics,
                        hotspots
                );

        AnalysisSnapshot snapshot = new AnalysisSnapshot(
                UUID.randomUUID().toString(),
                projectId,
                clock.instant(),
                report
        );

        return repository.save(snapshot);
    }
}

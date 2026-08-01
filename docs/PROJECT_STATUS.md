# Monolith Insight AI — Project Status

## Current Iteration

Epic 4 — Architecture Insights

## Current Story

Technical Sprint Review — Epic 4

## Last Completed Story

MON-004.5 — Hotspots Report

## Current Architecture

```text
Source Code
    ↓
ProjectAnalysis
    ↓
ProjectGraph
    ↓
GraphMetrics
    ↓
Architecture Insights
```

## Implemented Components

- ClassMetrics
- GraphMetrics
- AnalyzeGraphMetricsUseCase
- CoupledClass
- CouplingReport
- FindMostCoupledClassesUseCase
- GitHub Actions CI workflow
- FindLeastCoupledClassesUseCase
- PackageMetrics
- PackageMetricsReport
- AnalyzePackageMetricsUseCase
- ClassHotspot 
- CouplingLevel 
- HotspotsReport 
- GenerateHotspotsReportUseCase

## Important Decisions

- Metrics do not depend on JavaParser or source-code parsing.
- Most-coupled classes are ordered by total dependencies descending.
- classId ascending is used as the tie-breaker.
- CoupledClass includes incoming, outgoing and total dependencies.
- Least-coupled classes are ordered by total dependencies ascending.
- `classId` ascending is used as the tie-breaker.
- Classes with zero dependencies are included as isolated classes.
- Package coupling counts only dependencies that cross package boundaries.
- Dependencies between classes in the same package are excluded.
- Average package coupling is calculated as external dependencies divided by class count.
- Package metrics are ordered alphabetically by package name.
- Hotspots are ordered by total dependencies descending.
- `classId` ascending is used as the tie-breaker.
- Coupling levels are classified as LOW for 0–5 dependencies, MEDIUM for 6–10, and HIGH for 11 or more.
- Graph metrics are calculated from ProjectGraph.
- Higher-level class insights operate on GraphMetrics.

## Next Story

MON-005.1 — Find Reachable Classes
# Monolith Insight AI — Project Status

## Current Iteration

Epic 5 — Graph Traversal

## Current Story

MON-005.3 — Dependency Paths

## Last Completed Story

MON-005.2 — Impact Analysis

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
- ReachabilityReport
- FindReachableClassesUseCase
- ImpactAnalysisReport 
- AnalyzeImpactUseCase

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
- Reachability follows outgoing class dependencies using breadth-first search.
- The starting class is excluded from the reachability result.
- Reachable classes are returned alphabetically by `classId`.
- Visited-node tracking prevents duplicates and infinite traversal through cycles.
- An unknown starting class produces an `IllegalArgumentException`.
- Impact Analysis follows incoming class dependencies using breadth-first search.
- The starting class is excluded from the impact analysis result.
- Impact analysis classes are returned alphabetically by `classId`.

## Next Story

MON-005.3 — Dependency Paths
# Monolith Insight AI — Project Status

## Current Iteration

Technical Epic 1 — Analysis Lifecycle, Persistence & Containers

## Current Story

TECH-002 — Define Aggregate Architecture Analysis Report

## Last Completed Story

MON-005.4 — Shortest Dependency Path

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
- ProjectGraphTestBuilder
- DependencyPath
- DependencyPathsReport
- FindDependencyPathsUseCase
- FindShortestDependencyPathUseCase

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
- Dependency paths are enumerated with depth-first search and backtracking.
- Nodes in the current DFS path are tracked separately so cycles are skipped without blocking valid alternative paths.
- Dependency paths contain both their source and target classes.
- Duplicate dependency types between the same two classes do not duplicate paths.
- Shortest dependency paths are found using breadth-first search.
- A predecessor map is used to reconstruct the shortest path from target to source.
- Shortest-path neighbors are processed alphabetically for deterministic tie-breaking.
- A missing shortest path is represented by `Optional.empty()`.
- Reverse dependency analysis is already provided by `AnalyzeImpactUseCase` from MON-005.2.
- MON-005.5 was closed as a duplicate of MON-005.2 instead of introducing a second implementation.

## Next Story

TECH-002 — Define Aggregate Architecture Analysis Report

# Monolith Insight AI — Project Status

## Current Iteration

Epic 4 — Architecture Insights

## Current Story

MON-004.4 — Package Metrics

## Last Completed Story

MON-004.3 — Least Coupled Classes

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

## Important Decisions

- Architectural metrics operate exclusively on ProjectGraph.
- Metrics do not depend on JavaParser or source-code parsing.
- Most-coupled classes are ordered by total dependencies descending.
- classId ascending is used as the tie-breaker.
- CoupledClass includes incoming, outgoing and total dependencies.
- Least-coupled classes are ordered by total dependencies ascending.
- `classId` ascending is used as the tie-breaker.
- Classes with zero dependencies are included as isolated classes.


## Next Story

MON-004.4 — Package Metrics

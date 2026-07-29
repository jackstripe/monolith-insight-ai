# Monolith Insight AI — Project Status

## Current Iteration

Epic 4 — Architecture Insights

## Current Story

MON-004.3 — Least Coupled Classes

## Last Completed Story

MON-004.2.1 — Establish Project Documentation as Source of Truth

## Current Architecture

Source Code
↓
ProjectAnalysis
↓
ProjectGraph
↓
GraphMetrics
↓
Architecture Insights

## Implemented Components

- ClassMetrics
- GraphMetrics
- AnalyzeGraphMetricsUseCase
- CoupledClass
- CouplingReport
- FindMostCoupledClassesUseCase

## Important Decisions

- Architectural metrics operate exclusively on ProjectGraph.
- Metrics do not depend on JavaParser or source-code parsing.
- Most-coupled classes are ordered by total dependencies descending.
- classId ascending is used as the tie-breaker.
- CoupledClass includes incoming, outgoing and total dependencies.

## Next Story

MON-004.3 — Least Coupled Classes
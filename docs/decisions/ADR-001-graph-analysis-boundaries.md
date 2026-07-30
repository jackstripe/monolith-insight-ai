# ADR-001: Graph Analysis Operates on ProjectGraph

## Status

Accepted

## Date

2026-07-29

## Context

Monolith Insight AI parses Java source code and transforms it into
domain representations.

Architectural metrics need information about the relationships between
classes, but they should not depend directly on JavaParser, source files,
or parsing details.

We need to define which domain object serves as the input boundary for
graph metrics and architectural analysis.

## Decision

Graph metrics and architectural insight use cases will operate exclusively
on ProjectGraph.

ProjectGraph will be constructed from ProjectAnalysis before metrics are
calculated.

The processing flow will be:

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

Classes such as AnalyzeGraphMetricsUseCase and
FindMostCoupledClassesUseCase will not access JavaParser or source files.

## Rationale

ProjectGraph already represents the structural relationships needed for
architectural analysis.

Using it as the boundary:

- separates parsing from graph analysis;
- prevents JavaParser details from leaking into application use cases;
- allows graph algorithms to evolve independently;
- makes tests easier by allowing ProjectGraph fixtures;
- permits other source-code parsers to be added in the future.

## Consequences

### Positive

- Metrics remain independent from JavaParser.
- Use cases can be tested using small in-memory graphs.
- Graph algorithms can be reused by different reports.
- The architecture has a clear processing pipeline.
- Parsing changes should not affect metric calculations.

### Negative

- ProjectGraph must be built before calculating metrics.
- Some information available in ProjectAnalysis may not exist in ProjectGraph.
- Adding a new metric may require extending ProjectGraph.
- The transformation pipeline introduces an additional processing step.

## Alternatives Considered

### Calculate metrics directly from ProjectAnalysis

Rejected because metric use cases would depend on the result of source-code
analysis instead of the graph abstraction.

### Calculate metrics directly with JavaParser

Rejected because it would couple architectural analysis to a specific
parsing library and make unit testing more difficult.

### Let every report calculate its own graph information

Rejected because it would duplicate dependency-counting logic and could
produce inconsistent results between reports.

- Least-coupled classes are ordered by total dependencies ascending.
- `classId` ascending is used as the tie-breaker.
- Classes with zero dependencies are included as isolated classes.

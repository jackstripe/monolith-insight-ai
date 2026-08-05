# Epic 4 Technical Review — Architecture Insights

## Status

Completed.

## Purpose

Epic 4 transformed the project dependency graph into measurable architectural information. The project can now calculate coupling metrics, identify highly and loosely coupled classes, aggregate dependencies by package, and classify architectural hotspots.

## Completed Stories

* MON-004.1 — Graph Metrics
* MON-004.2 — Most Coupled Classes
* MON-004.2.1 — Establish Project Documentation as Source of Truth
* MON-004.2.2 — Continuous Integration Pipeline
* MON-004.3 — Least Coupled Classes
* MON-004.4 — Package Metrics
* MON-004.5 — Hotspots Report

## Delivered Components

### Class-level metrics

* `ClassMetrics`
* `GraphMetrics`
* `AnalyzeGraphMetricsUseCase`

Each class now exposes:

* Incoming dependencies
* Outgoing dependencies
* Total dependencies

### Coupling reports

* `CoupledClass`
* `CouplingReport`
* `FindMostCoupledClassesUseCase`
* `FindLeastCoupledClassesUseCase`

Coupling reports support deterministic ordering and configurable result limits.

### Package-level metrics

* `PackageMetrics`
* `PackageMetricsReport`
* `AnalyzePackageMetricsUseCase`

Package metrics include:

* Number of classes
* Incoming external dependencies
* Outgoing external dependencies
* Total external dependencies
* Average coupling per class

### Hotspots

* `CouplingLevel`
* `ClassHotspot`
* `HotspotsReport`
* `GenerateHotspotsReportUseCase`

Classes are classified using the following thresholds:

* LOW: 0–5 total dependencies
* MEDIUM: 6–10 total dependencies
* HIGH: 11 or more total dependencies

## Architecture Review

The architecture maintains a clear separation between source-code parsing, graph construction, metric calculation, and architectural insights.

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

`ProjectGraph` represents classes and their dependency relationships.

`GraphMetrics` contains numerical information derived from the graph and is used by higher-level insights such as coupling reports and hotspots.

Package metrics operate directly on `ProjectGraph` because they need the source and target package of every dependency. This information is not available in `GraphMetrics`.

The application use cases remain independent from JavaParser and other infrastructure details.

## Important Technical Decisions

* Graph metrics are calculated from `ProjectGraph`.
* Higher-level class insights operate on `GraphMetrics`.
* Metrics do not depend on JavaParser or source-code parsing.
* Total coupling is calculated as incoming plus outgoing dependencies.
* Derived totals are calculated instead of stored independently.
* Classes with zero dependencies are included.
* Most-coupled classes are ordered by total dependencies descending.
* Least-coupled classes are ordered by total dependencies ascending.
* Hotspots are ordered by total dependencies descending.
* `classId` ascending is used as the tie-breaker.
* Package coupling counts only dependencies that cross package boundaries.
* Dependencies inside the same package are excluded from package coupling.
* Package metrics are ordered alphabetically.
* Report collections are exposed as immutable copies.

## Java Learnings

During this epic, I improved my understanding and practical use of Java streams.

Streams were especially useful for:

* Mapping domain objects
* Sorting results
* Limiting collections
* Grouping information
* Producing immutable result lists

I also gained more experience using records as immutable domain value objects. Records reduced boilerplate and made the domain model easier to read.

The Package Metrics story provided practical experience with `HashMap`, `merge`, `getOrDefault`, `groupingBy`, and `toMap`.

An important lesson was that streams are not always the clearest solution for every part of an algorithm. Mutable maps and loops can be appropriate when accumulating several related values, while streams remain useful for the final transformation into domain objects.

## Main Challenge

MON-004.4 — Package Metrics was the most challenging story.

Unlike the previous stories, it could not operate only on previously calculated metrics. It required several intermediate indexes:

```text
Class ID → Package
Package → Class count
Package → Incoming dependencies
Package → Outgoing dependencies
```

The solution required:

1. Resolving the package of every class.
2. Counting classes by package.
3. Traversing every dependency.
4. Ignoring dependencies inside the same package.
5. Accumulating incoming and outgoing dependencies.
6. Transforming the accumulated data into package metrics.

I understood the final logic and implementation, but independently discovering this decomposition would have taken more time. This story improved my ability to break an algorithm into indexes, accumulators, and transformations.

## Testing Review

The reviewed Epic 4 test classes contain 16 focused test cases.

The tests cover:

* Incoming and outgoing dependency calculation
* Total dependency calculation
* Descending and ascending ordering
* Alphabetical tie-breaking
* Result limits
* Limits greater than the collection size
* Zero limits
* Isolated classes
* Dependencies inside the same package
* Packages without external dependencies
* Empty graphs and metrics
* Hotspot boundary values
* Preservation of incoming and outgoing metrics

A particularly useful lesson occurred during Package Metrics testing: an unexpected result was caused by an incorrectly constructed dependency in the test fixture, not by the production algorithm. This reinforced the importance of validating test data before changing correct production logic.

## What Went Well

* The domain model remained small and focused.
* Records reduced unnecessary boilerplate.
* Application use cases remained independent from infrastructure.
* Sorting rules became deterministic.
* Tests were based on observable behavior rather than stream implementation details.
* CI and project documentation were introduced during the epic.
* Every story extended the previous architectural model instead of creating isolated functionality.
* No major architectural restructuring is required before Epic 5.

## Potential Improvements

If the epic were started again:

* CI and source-of-truth documentation would be established earlier.
* Acceptance examples would be defined before implementing each algorithm.
* `ProjectGraph` invariants would be documented from the beginning.
* Reusable test fixtures would be introduced earlier.
* Application use cases would consistently be created inside the application package from the start.

## Technical Debt

The following items do not block the next epic:

* Define explicit behavior for negative report limits.
* Traverse graph dependencies only once when calculating class metrics.
* Reduce duplication in test fixture construction.
* Replace hotspot threshold literals with named constants.
* Make hotspot thresholds configurable in a future iteration.
* Define how `ProjectGraph` handles dependencies whose nodes do not exist.
* Decide whether multiple dependency types between the same classes represent separate edges or one unique relationship.

The last decision will determine whether `ProjectGraph` behaves as a simple directed graph or as a directed multigraph.

## Final Assessment

Epic 4 is technically complete.

The project has evolved from representing a dependency graph to producing deterministic architectural insights from it. The current architecture is suitable for the next stage and does not require an immediate structural refactoring.

## Next Steps

The next iteration is Epic 5 — Graph Traversal.

Before implementing MON-005.1, a theoretical session will cover:

* Directed graphs
* Nodes and edges
* Simple graphs and multigraphs
* Adjacency lists
* Reachability
* Breadth-first search
* Depth-first search
* Visited-node tracking
* Time and space complexity

The first implementation story will be:

**MON-005.1 — Find Reachable Classes**

After the first traversal story, an integration checkpoint will connect the existing analysis use cases through an aggregate architecture report and a REST endpoint. Persistence and versioned analysis snapshots will be introduced after the complete analysis result has been defined.

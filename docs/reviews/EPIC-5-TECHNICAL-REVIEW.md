# Epic 5 Technical Review — Graph Traversal

## Status

Completed.

## Purpose

Epic 5 extended `ProjectGraph` from a static representation of classes and dependencies into a navigable directed graph. The project can now discover reachable classes, analyze change impact through incoming dependencies, enumerate every simple dependency path, and find a deterministic shortest dependency path.

## Completed Stories

* MON-005.1 — Find Reachable Classes
* TECH-001 — Reusable ProjectGraph Test Builder
* MON-005.2 — Impact Analysis
* MON-005.3 — Dependency Paths
* MON-005.4 — Shortest Dependency Path
* MON-005.5 — Reverse Dependency Analysis, covered by MON-005.2

MON-005.5 was closed during backlog refinement because Impact Analysis already performs reverse dependency traversal. Creating a second use case would have duplicated the same domain behavior.

## Delivered Components

### Reachability

* `ReachabilityReport`
* `FindReachableClassesUseCase`

Reachability follows outgoing dependencies with breadth-first search. It returns every directly or transitively reachable class, excludes the starting class, and orders results alphabetically.

### Impact analysis

* `ImpactAnalysisReport`
* `AnalyzeImpactUseCase`

Impact Analysis follows incoming dependencies using a reverse adjacency list. It identifies the classes that may be affected when a selected class changes.

### Dependency paths

* `DependencyPath`
* `DependencyPathsReport`
* `FindDependencyPathsUseCase`

All simple dependency paths between a source and target are enumerated with recursive depth-first search and backtracking.

### Shortest dependency path

* `FindShortestDependencyPathUseCase`

The shortest path is found with breadth-first search. A predecessor map records how each class was first discovered, and the path is reconstructed from the target back to the source.

### Test support

* `ProjectGraphTestBuilder`

The builder provides a compact domain-specific fixture API for graph tests and supports dependencies with default or explicit dependency types.

## Architecture Review

Graph traversal remains inside the application layer and depends only on domain objects:

```text
ProjectGraph
    ↓
Adjacency index
    ↓
BFS or DFS traversal
    ↓
Traversal report or path
```

The traversal use cases remain independent from JavaParser, controllers, databases, and infrastructure concerns.

Each execution creates its own queue, visited set, path state, and indexes. No mutable traversal state is stored in use-case instances, so repeated executions cannot contaminate one another.

## Algorithm Review

### Breadth-first search

BFS is used by reachability, impact analysis, and shortest-path search.

Its queue explores nodes by distance from the starting class. Marking a class as visited when it is enqueued prevents duplicate work and infinite traversal through cycles.

For reachability and impact analysis, the traversal cost is approximately:

```text
O(V + E)
```

where `V` is the number of classes and `E` is the number of dependencies.

For shortest-path search, the first discovery of the target is guaranteed to use the minimum number of dependency edges because the graph is unweighted and BFS explores one level at a time.

### Depth-first search and backtracking

DFS is used to enumerate every simple path between two classes.

Each recursive call:

1. Adds its current class to the path and current-path set.
2. Explores each neighbor not already in that path.
3. Records a copy when the target is reached.
4. Removes its own class before returning.

The visited state is scoped to the current path instead of the whole traversal. This prevents cycles while still allowing the same class to appear in different valid paths.

Enumerating every simple path can require exponential time and output in dense graphs. This is an inherent property of the problem rather than an implementation defect.

### Path reconstruction

Shortest-path BFS stores one predecessor per discovered class:

```text
previous[target] = prior class
```

Reconstruction begins at the target and follows predecessors toward the source. `LinkedList.addFirst()` inserts each predecessor before the partial path, producing source-to-target order without a separate reversal step.

## Important Technical Decisions

* Directed dependencies are traversed according to source and target class IDs.
* Reachability follows outgoing dependencies.
* Impact Analysis follows incoming dependencies through a reverse adjacency list.
* BFS is used for reachability and shortest unweighted paths.
* DFS with backtracking is used to enumerate all simple paths.
* Nodes are marked visited when they enter a BFS queue.
* DFS cycle prevention is scoped to the current path.
* Source and target are included in dependency paths.
* A source equal to the target produces the trivial path containing that class.
* Unknown source, target, or starting classes produce `IllegalArgumentException`.
* No shortest path is represented by `Optional.empty()`.
* Duplicate dependency types between the same classes do not duplicate paths.
* Alphabetical neighbor ordering makes path selection deterministic.
* Report collections are exposed as immutable copies.
* Reverse Dependency Analysis is represented by Impact Analysis rather than a duplicate use case.

## Java Learnings

This epic provided practical experience with:

* `Queue` and `ArrayDeque` for BFS.
* `Set.add()` as an atomic visited check.
* Adjacency and reverse-adjacency maps.
* Recursive methods and call-stack behavior.
* Backtracking with shared mutable path state.
* Separating a path `List` from a current-path membership `Set`.
* `Optional` for a result that may not exist.
* `LinkedList.addFirst()` for reverse reconstruction.
* `TreeSet` for deduplication and deterministic ordering.
* Streams for building indexes and immutable result transformations.

An important lesson was that streams are not mandatory. Imperative loops were clearer for queue processing, recursive traversal, backtracking, and predecessor reconstruction.

## Main Challenge

MON-005.3 — Dependency Paths was the most challenging story.

The difficult part was not descending recursively; it was restoring the shared path state correctly when each recursive call returned. The key invariant became:

> Every recursive call adds its own node when it enters and removes that same node before it returns.

Placing cleanup at the end of the recursive method allowed the parent call to continue with its next neighbor. Removing state inside individual branches left stale nodes in the path or removed nodes still needed by the current call.

This story strengthened the understanding of recursion as a stack of independent method invocations operating on deliberately shared traversal state.

## Testing Review

Epic 5 contains 24 focused tests across its four traversal use cases and reusable graph builder.

The tests cover:

* Direct and transitive reachability.
* Empty reachability and impact results.
* Disconnected classes.
* Reverse traversal for change impact.
* Unknown starting, source, and target classes.
* Cycles in BFS and DFS traversals.
* Exclusion of the starting class from traversal reports.
* Enumeration of every simple dependency path.
* Trivial source-to-source paths.
* Missing dependency paths.
* Multiple dependency types between the same classes.
* Shortest-path selection.
* Deterministic tie-breaking between equal-length paths.
* Construction of graph fixtures with explicit dependency types.

The deterministic shortest-path test deliberately inserts `A → C` before `A → B` and still expects `A → B → D`. This proves that the result comes from the defined alphabetical ordering rather than fixture insertion order.

## What Went Well

* The epic progressed from basic BFS to reverse traversal, DFS, backtracking, and shortest-path reconstruction.
* Each algorithm was introduced with manual graph exercises before implementation.
* Observable contracts were protected with focused tests.
* `ProjectGraphTestBuilder` reduced test setup noise.
* Traversal state remained local to each execution.
* Cycles were handled without special-case graph mutation.
* Deterministic ordering made tests and API behavior predictable.
* A duplicate backlog story was detected before duplicate production code was introduced.

## Potential Improvements

If the epic were started again:

* All stories would define acceptance examples before implementation.
* The reusable graph builder would be introduced before the first traversal story.
* Shared node and adjacency indexing would be identified earlier.
* Backlog refinement would detect the overlap between Impact Analysis and Reverse Dependency Analysis before implementation began.
* Complexity and large-graph limits would be included in each story's definition of done.

## Technical Debt

The following items do not block the next technical epic:

* Extract repeated class indexing and adjacency construction when a stable abstraction becomes clear.
* Reduce repeated graph construction across tests with focused fixture methods.
* Standardize formatting and imports in traversal use cases and tests.
* Define a maximum depth, path count, or cancellation strategy for all-path enumeration on large graphs.
* Consider an iterative DFS if recursion depth becomes a production constraint.
* Document how dependencies referencing unknown nodes should be handled.
* Standardize whether duplicate dependency types represent one traversal edge across every graph operation.
* Add performance tests once representative monolith sizes are available.

These items should be addressed based on real integration pressure instead of introducing a graph framework prematurely.

## Final Assessment

Epic 5 is technically complete.

The project now supports deterministic forward traversal, reverse impact traversal, complete simple-path enumeration, and shortest-path discovery. The application has enough independent analysis capabilities to define a single aggregate architecture result.

## Next Steps

The next iteration is Technical Epic 1 — Analysis Lifecycle, Persistence & Containers.

The first story is:

**TECH-002 — Define Aggregate Architecture Analysis Report**

This story will define one domain result that composes the existing graph metrics, package metrics, coupling reports, hotspots, and traversal-oriented analysis outputs needed by downstream persistence and API layers.

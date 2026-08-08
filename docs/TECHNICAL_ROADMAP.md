# Technical Roadmap

## Status

Proposed.

## Purpose

This roadmap defines the technical initiatives required to transform
Monolith Insight AI from a collection of deterministic analysis use cases
into a persistent, remotely accessible and deployable application.

The technical roadmap complements the product backlog. Product epics define
new architectural analysis capabilities, while technical epics improve the
application lifecycle, persistence, operability and deployment model.

## Guiding Principles

- Domain and application layers remain independent from MongoDB, Docker,
  GitHub and Kubernetes.
- Architectural facts continue to be calculated deterministically in Java.
- Persistence stores analysis results but does not replace graph algorithms.
- Infrastructure integrations are implemented behind application ports.
- Remote project acquisition must be restricted and validated.
- Idempotency will be based on project content, analyzer version and
  analysis configuration.
- Kubernetes will be introduced only after the application is containerized
  and externally configurable.

## Proposed Sequence

1. Complete Epic 5 — Graph Traversal.
2. Complete Technical Epic 1 — Analysis Lifecycle, Persistence and Containers.
3. Complete Epic 6 — Graph Analysis.
4. Complete Technical Epic 2 — Remote Analysis, Idempotency and Kubernetes.
5. Continue with Epic 7 — Architectural Intelligence.
6. Continue with Epic 8 — AI Integration.

# Technical Epic 1 — Analysis Lifecycle, Persistence and Containers

## Objective

Create a complete analysis lifecycle that coordinates the existing use
cases, persists their results and exposes them through a REST API.

## Stories

- TECH-002 — Define Aggregate Architecture Analysis Report
- TECH-003 — Create Analysis Snapshot Repository Port
- TECH-004 — Persist Analysis Snapshots with MongoDB
- TECH-005 — Orchestrate End-to-End Analysis and Persistence
- TECH-006 — Expose Analysis Lifecycle REST Endpoints
- TECH-007 — Add MongoDB Integration Tests with Testcontainers
- TECH-008 — Containerize the Application
- TECH-009 — Run Application and MongoDB with Docker Compose

## Target Flow

```text
Analysis request
    ↓
Project analysis
    ↓
Project graph
    ↓
Architecture analysis use cases
    ↓
Aggregate architecture report
    ↓
Persisted analysis snapshot
    ↓
REST response

Persistence Strategy

MongoDB is the initial persistence technology because an architecture
analysis naturally forms a document containing metadata and nested reports.

MongoDB remains an infrastructure detail. Domain objects must not depend on
Spring Data annotations.

The first implementation will persist every requested analysis as a new
snapshot. Project fingerprints and snapshot reuse are deferred to Technical
Epic 2.

Initial API
POST /api/analyses
GET  /api/analyses/{analysisId}
GET  /api/projects/{projectId}/analyses
GET  /api/projects/{projectId}/analyses/latest
Exit Criteria
A complete architecture analysis can be generated through one use case.
The analysis is persisted in MongoDB.
Persisted analyses can be retrieved through REST endpoints.
Application and MongoDB integration is tested with Testcontainers.
The application can run as a Docker image.
Application and MongoDB can run together through Docker Compose.
Out of Scope
Project content fingerprints.
Automatic snapshot reuse.
Remote GitHub repositories.
Asynchronous analysis jobs.
Kubernetes deployment.
Technical Epic 2 — Remote Analysis, Idempotency and Kubernetes
Objective

Allow the application to safely analyze remote projects, avoid duplicate
analyses and run in a Kubernetes environment.

Stories
TECH-010 — Define Project Source Abstraction
TECH-011 — Analyze Public GitHub Repositories
TECH-012 — Support Branch, Commit and Subdirectory Selection
TECH-013 — Secure and Limit Remote Source Acquisition
TECH-014 — Calculate Deterministic Project Fingerprints
TECH-015 — Reuse Existing Analysis Snapshots Idempotently
TECH-016 — Introduce Asynchronous Analysis Jobs
TECH-017 — Add Application Health and Readiness Probes
TECH-018 — Deploy the Application Locally with Kubernetes
TECH-019 — Externalize Configuration and Secrets
Remote Analysis Request
POST /api/analyses/github
{
  "repositoryUrl": "https://github.com/owner/project",
  "ref": "main",
  "subdirectory": "backend/order-service"
}

The first implementation will accept only public GitHub repositories.

Security Constraints
Accept only approved GitHub URL formats.
Reject arbitrary hosts.
Limit repository size and file count.
Apply download and analysis timeouts.
Validate requested subdirectories.
Prevent path traversal.
Handle symbolic links safely.
Remove temporary files after analysis.
Do not execute code from the analyzed repository.
Fingerprint Strategy
SHA-256(
    ordered relative paths
    + file contents
    + analyzer version
    + analysis configuration
)

The fingerprint will allow the application to find and reuse an existing
snapshot when the project and analyzer behavior have not changed.

Kubernetes Scope

The initial Kubernetes deployment will include:

Application Deployment
Application Service
ConfigMap
Secret references
Readiness probe
Liveness probe
CPU and memory limits
Temporary workspace configuration

MongoDB may run locally inside the learning environment, but a managed
MongoDB service is preferred for a production deployment.

Exit Criteria
A public GitHub repository can be analyzed.
A branch, commit or subdirectory can be selected.
Remote acquisition applies explicit security limits.
Identical inputs reuse an existing snapshot.
Long-running analyses expose an asynchronous job status.
The application runs in a local Kubernetes cluster.
Configuration and credentials are externalized.
Architecture Decisions to Formalize

The following decisions may require Architecture Decision Records:

MongoDB as the initial snapshot persistence technology.
Aggregate document versus separated graph storage.
Maximum supported analysis size.
Fingerprint composition.
GitHub repository acquisition mechanism.
Synchronous versus asynchronous analysis.
Managed MongoDB versus MongoDB inside Kubernetes.
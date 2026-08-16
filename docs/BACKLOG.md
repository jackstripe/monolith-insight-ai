# Product Backlog

## Epic 4 — Architecture Insights

- [x] MON-004.1 — Graph Metrics
- [x] MON-004.2 — Most Coupled Classes
- [x] MON-004.2.1 — Establish Project Documentation as Source of Truth
- [x] MON-004.2.2 — Continuous Integration Pipeline
- [x] MON-004.3 — Least Coupled Classes
- [x] MON-004.4 — Package Metrics
- [x] MON-004.5 — Hotspots Report

## Epic 5 — Graph Traversal

- [x] MON-005.1 — Find Reachable Classes
- [x] TECH-001 — Reusable ProjectGraph Test Builder
- [x] MON-005.2 — Impact Analysis
- [x] MON-005.3 — Dependency Paths
- [ ] MON-005.4 — Shortest Dependency Path
- [ ] MON-005.5 — Reverse Dependency Analysis

## Technical Epic 1 — Analysis Lifecycle, Persistence & Containers

- [ ] TECH-002 — Define Aggregate Architecture Analysis Report
- [ ] TECH-003 — Create Analysis Snapshot Repository Port
- [ ] TECH-004 — Persist Analysis Snapshots with MongoDB
- [ ] TECH-005 — Orchestrate End-to-End Analysis and Persistence
- [ ] TECH-006 — Expose Analysis Lifecycle REST Endpoints
- [ ] TECH-007 — Add MongoDB Integration Tests with Testcontainers
- [ ] TECH-008 — Containerize the Application
- [ ] TECH-009 — Run Application and MongoDB with Docker Compose

## Epic 6 — Graph Analysis

- [ ] Cycle Detection
- [ ] Circular Dependency Report
- [ ] Strongly Connected Components
- [ ] Dependency Layers

## Technical Epic 2 — Remote Analysis, Idempotency & Kubernetes

- [ ] TECH-010 — Define Project Source Abstraction
- [ ] TECH-011 — Analyze Public GitHub Repositories
- [ ] TECH-012 — Support Branch, Commit and Subdirectory Selection
- [ ] TECH-013 — Secure and Limit Remote Source Acquisition
- [ ] TECH-014 — Calculate Deterministic Project Fingerprints
- [ ] TECH-015 — Reuse Existing Analysis Snapshots Idempotently
- [ ] TECH-016 — Introduce Asynchronous Analysis Jobs
- [ ] TECH-017 — Add Application Health and Readiness Probes
- [ ] TECH-018 — Deploy the Application Locally with Kubernetes
- [ ] TECH-019 — Externalize Configuration and Secrets

## Epic 7 — Architectural Intelligence

- [ ] Detect God Classes
- [ ] Detect Hubs
- [ ] Detect Feature Envy
- [ ] Detect Highly Coupled Packages
- [ ] Detect Microservice Candidates

## Technical Epic 3 — GCP Cloud Deployment

> Stories will be defined after Technical Epic 2 and Epic 7 are complete.

## Epic 8 — AI Integration

- [ ] Define architectural analysis input
- [ ] Integrate Spring AI
- [ ] Generate architectural recommendations
- [ ] Generate modernization report

---
name: repo-baseline
description: Baseline state of agent-testing repo as of first review (commit 93b5d53) — minimal Spring Boot skeleton
metadata:
  type: project
---

Repo is a freshly-initialized Spring Boot skeleton (commit 93b5d53, 2026-08-31): `AgentTestingApplication`, one `HelloController` (`GET /hello` returning a static string), one context-load test. No JDBC/JPA, no manual thread/resource management, no exception handling logic yet.

**Why:** Most of the standard review checklist (SQL injection, resource leaks, thread-safety, swallowed exceptions) is not yet applicable — there's no code touching those concerns. Reviews at this stage should say so explicitly rather than force-fitting findings.

**How to apply:** For future reviews of this repo, check whether new code has actually introduced DB access, concurrency, or I/O before applying those checklist items. Once real endpoints/services/repositories appear, prioritize SQL/JPA parameterization and resource-closing checks there first, since the codebase started with zero examples to set a pattern.

Confirmed working at review time: `mvn -o test` passes; `mvn -o spring-boot:run` boots and `/hello`, `/v3/api-docs`, `/actuator/health` all return 200 (see [[verify-before-flagging]]).

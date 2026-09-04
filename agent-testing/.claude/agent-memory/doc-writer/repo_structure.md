---
name: repo-structure
description: Structure/layout notes for agent-testing repo so future doc updates skip re-exploration
metadata:
  type: project
---

agent-testing is a minimal Spring Boot 4.1.1 (Java 17) skeleton REST service, Maven build via `mvnw`. Not a git repo (no `.git` dir as of 2026-08-31).

**Layout:**
- `pom.xml` — parent `spring-boot-starter-parent:4.1.1`; deps: `spring-boot-starter-webmvc`, `spring-boot-starter-actuator`, `springdoc-openapi-starter-webmvc-ui:2.8.6`; test deps: `spring-boot-starter-webmvc-test`, `spring-boot-starter-actuator-test`.
- `src/main/java/com/example/agenttesting/AgentTestingApplication.java` — entry point (`@SpringBootApplication`).
- `src/main/java/com/example/agenttesting/HelloController.java` — only controller, single `GET /hello` endpoint, uses springdoc `@Tag`/`@Operation`.
- `src/main/resources/application.properties` — just `spring.application.name=agent-testing`, no other config.
- `src/test/java/.../AgentTestingApplicationTests.java` — single Spring context-load smoke test, no real test coverage yet.
- `.claude/agents/` — agent defs used to test Claude agent workflows in this repo: `code-reviewer.md`, `doc-writer.md` (this one), `test-runner.md`.
- `HELP.md` — Spring Initializr boilerplate, not maintained/authoritative docs.
- README.md at repo root was created 2026-08-31 (didn't previously exist) — covers stack, build/run/test commands, structure, endpoints table.

**How to apply:** This repo is intentionally tiny/skeletal — when re-documenting, just diff against this snapshot (`find src -type f`, check `pom.xml` deps) instead of re-reading every file from scratch. If new controllers/services/modules appear, update both README.md and this memory file's layout section.

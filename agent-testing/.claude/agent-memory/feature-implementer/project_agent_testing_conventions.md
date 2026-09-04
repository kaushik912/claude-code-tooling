---
name: project-agent-testing-conventions
description: agent-testing repo conventions for adding new REST endpoints (flat package, springdoc already wired)
metadata:
  type: project
---

agent-testing (com.example.agenttesting, Maven, Spring Boot 4.1.1) has springdoc-openapi-starter-webmvc-ui 2.8.6 already in pom.xml — no need to add it again when implementing new Swagger-documented endpoints.

**Why:** Checked pom.xml while implementing SCRUM-6 (quote generator); dependency was already present alongside spring-boot-starter-webmvc.

**How to apply:** Before adding springdoc per [[spring]] rule, grep pom.xml first — it may already be there. All controllers live flat in `src/main/java/com/example/agenttesting/` (no subpackages like `controller`/`service`), use constructor injection, `@RestController` + `@Tag(name = "...")` on the class, `@GetMapping` + `@Operation(summary = "...")` on methods. Follow `HelloController.java` / `QuoteController.java` as the template for new endpoints in this repo.

---

**Tickets implemented here:** SCRUM-6 (quote generator, GET /quote via QuoteController/QuoteService) — done 2026-08-31. See [[reference_jira_scrum]] for where these tickets live. Per [[feedback_record_ticket_id]], append a new entry here at the end of every ticket implementation.

SCRUM-7 (health check) — done 2026-08-31. Added `management.endpoints.web.exposure.include=health,info` to application.properties (actuator dependency was already in pom.xml) plus HealthCheckTests.java asserting `/actuator/health` returns UP. Mid-task the issue description (Gherkin AC for `GET /api/v1/ping` returning "pong") became visible via the Jira API response even though earlier triage had reported the ticket as description-less — added PingController.java + PingControllerTests.java to also cover that literal AC. See [[feedback_jira_description_visibility]].

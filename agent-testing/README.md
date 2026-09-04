# agent-testing

Minimal Spring Boot REST service used as a sandbox/skeleton for testing Claude agent workflows (code review, doc writing, test running — see `.claude/agents/`).

## Tech stack

- Java 17
- Spring Boot 4.1.1 (parent POM: `spring-boot-starter-parent`)
  - `spring-boot-starter-webmvc` — REST controllers
  - `spring-boot-starter-actuator` — health/metrics endpoints
  - `springdoc-openapi-starter-webmvc-ui` 2.8.6 — OpenAPI/Swagger UI
- Test: `spring-boot-starter-webmvc-test`, `spring-boot-starter-actuator-test` (JUnit 5)
- Build: Maven (via `mvnw` wrapper, Maven 3.9.16)

## Build / run / test

```bash
./mvnw spring-boot:run     # run the app (default port 8080)
./mvnw clean package       # build jar -> target/agent-testing-0.0.1-SNAPSHOT.jar
./mvnw test                # run tests
java -jar target/agent-testing-0.0.1-SNAPSHOT.jar   # run built jar
```

Use `mvnw.cmd` instead of `./mvnw` on Windows.

## Project structure

```
src/main/java/com/example/agenttesting/
  AgentTestingApplication.java   # @SpringBootApplication entry point
  HelloController.java           # sample REST controller
src/main/resources/
  application.properties         # spring.application.name=agent-testing
src/test/java/com/example/agenttesting/
  AgentTestingApplicationTests.java  # Spring context load smoke test
.claude/agents/                  # Claude agent definitions (code-reviewer, doc-writer, test-runner)
```

## Key components

- **`AgentTestingApplication`** — standard Spring Boot bootstrap class (`SpringApplication.run`).
- **`HelloController`** — `@RestController` with a single sample endpoint:
  - `GET /hello` → returns `"Hello, World!"` (annotated with springdoc `@Tag`/`@Operation` for OpenAPI docs).

## Endpoints

| Path | Description |
|---|---|
| `GET /hello` | Sample greeting endpoint |
| `/actuator/**` | Spring Boot Actuator (health, info, metrics) |
| `/swagger-ui.html` | Swagger UI (springdoc-openapi) |
| `/v3/api-docs` | OpenAPI JSON spec |

## Notes

- No database, security, or config layers currently — this is a bare-bones starting point.
- Currently not a git repository (`.gitignore`/`.gitattributes` present but no `.git`).

# agent-testing

Minimal Spring Boot REST sandbox for testing Claude agent workflows (agents in `.claude/agents/`: code-reviewer, debugger, doc-writer, feature-implementer, jira-triage, test-runner).

## Stack
- Java 17, Spring Boot 4.1.1, Maven wrapper (`./mvnw`)
- webmvc, actuator, springdoc-openapi-starter-webmvc-ui 2.8.6
- Tests: JUnit 5 via webmvc-test / actuator-test starters

## Commands
- Run: `./mvnw spring-boot:run` (default port 8080, none set in properties)
- Test: `./mvnw test`
- Package: `./mvnw clean package` -> `target/agent-testing-0.0.1-SNAPSHOT.jar`

## Layout
- `com.example.agenttesting` (flat, no sub-packages): `AgentTestingApplication`, `HelloController` (`/hello`), `PingController` (`/api/v1/ping`), `QuoteController` + `QuoteService` (`/quote`), `ControllerFlowController` (`/controller-flow`)
- Tests in same package: `HealthCheckTests`, `PingControllerTests`, `AgentTestingApplicationTests`
- Swagger UI `/swagger-ui.html`, spec `/v3/api-docs`

## Gotchas
- README stale: lists only `/hello`; more controllers exist
- Actuator exposes only `health,info`
- No DB, no env vars needed. No Docker.
- `.mcp.json` configures Atlassian MCP (via npx mcp-remote); Jira-related agents use it
- Java style rules: `.claude/rules/java-best-practices.md`

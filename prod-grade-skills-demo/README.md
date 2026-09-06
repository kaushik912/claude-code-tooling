# prod-grade-skills-demo

A small, real, compilable Spring Boot service (Order & Inventory API) used purely as an anchor
for three worked guides on applying [`agent-skills`](../../reference_repos/agent-skills)
(Claude Code plugin, marketplace id `addy-agent-skills`) to production-grade Spring Boot work.

The app itself is intentionally thin: `Product` + `Order`, JPA + in-memory H2 (no Docker/
Testcontainers), Swagger via springdoc. What matters is the three scenarios in
[`docs/scenarios/`](docs/scenarios/), each walking a real Spring Boot task through the
`agent-skills` DEFINE → PLAN → BUILD → VERIFY → REVIEW → SHIP pipeline:

| Doc | Scenario | Skills / personas / commands exercised |
|---|---|---|
| [01-new-feature-promo-code.md](docs/scenarios/01-new-feature-promo-code.md) | Add promo-code discounts to checkout | `/spec`, `/plan`, `/constraints`, `incremental-implementation`, `test-driven-development`, `/test`, `/review` |
| [02-bug-fix-404-mapping.md](docs/scenarios/02-bug-fix-404-mapping.md) | Fix `GET /api/products/{id}` returning 500 instead of 404 for an unknown id | `debugging-and-error-recovery`, `test-driven-development`, `/review` |
| [03-migration-spring-boot-upgrade.md](docs/scenarios/03-migration-spring-boot-upgrade.md) | Upgrade Spring Boot 3.2.0 → 3.4.x + springdoc-openapi | `deprecation-and-migration`, `/build`, `/test`, `/ship` (parallel fan-out) |

The seeded bug (scenario 2) and the pinned Spring Boot 3.2.0 baseline (scenario 3) are real,
checked-in state — `ProductService.getById` really does throw a bare `RuntimeException` today,
and `pom.xml` really is pinned to 3.2.0. Scenario 1's promo-code feature does **not** exist in
the checked-in code; that doc shows the diff you'd produce, not a diff already applied.

## Installing `agent-skills`

The reference repo already ships its own marketplace manifest, so install it from the local
clone rather than re-authoring anything:

```bash
/plugin marketplace add /home/kaush/github_projs/reference_repos/agent-skills
/plugin install agent-skills@addy-agent-skills
```

(One-off session without installing: `claude --plugin-dir /home/kaush/github_projs/reference_repos/agent-skills`.)

Verify with `/help` — you should see `/spec /plan /build /test /constraints /review /webperf
/code-simplify /ship` listed.

## How this composes with the standing rules already in effect on this machine

`agent-skills` supplies the *workflow* (spec → plan → build → review → ship, and the personas
that review each axis). It does not know this machine's house style — that comes from rules
that are **already global** regardless of which project you're in:

| Standing rule | What it adds on top of agent-skills |
|---|---|
| `spring.md` | Use `spring` CLI for scaffolding; always attempt springdoc-openapi on new APIs |
| `testing-style.md` | JUnit tests in Given/When/Then, `given<Condition>_when<Action>_then<Outcome>` naming |
| `security.md` | No hardcoded secrets, parameterized SQL only, sanitize inputs |
| `agent-testing`'s `java-best-practices.md` | Constructor injection, `record` DTOs, no `Optional` fields, testing pyramid, etc. |

The `/build`, `/review`, and `/ship` steps in the three scenario docs are expected to satisfy
*both* the generic skill/persona checklists *and* these rules — e.g. the `code-reviewer` persona
in scenario 3 should flag a promo-code test that isn't Given/When/Then-named just as readily as
it flags a missing null check.

This demo deliberately sticks to `agent-skills` alone — no other skill/agent library is mixed
in, even where one might otherwise complement it.

## Running the app

```bash
./mvnw spring-boot:run
```

Seeded on startup with two products (`SKU-001`, `SKU-002`). Swagger UI at
`/swagger-ui.html`, OpenAPI spec at `/v3/api-docs`, H2 console at `/h2-console`.

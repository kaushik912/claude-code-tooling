# Scenario 3 — Migration: Spring Boot 3.2.0 → 3.4.x, then ship-gate the upgrade

`pom.xml` really is pinned to `spring-boot-starter-parent` `3.2.0` today (check it — line 8).
The task: bump to `3.4.x`, bump `springdoc-openapi-starter-webmvc-ui` from `2.3.0` to `2.8.6`
(matching the version already used in the sibling `agent-testing` project), and validate nothing
broke — then run the upgrade through a release gate before merging.

**Pipeline**: `deprecation-and-migration` (plan the bump) → `/build`/`/test` per stage →
**`/ship`** (`code-reviewer` + `security-auditor` + `test-engineer` personas, parallel fan-out) →
GO/NO-GO + rollback plan.

This is the doc that showcases the *persona orchestration* layer — scenarios 1 and 2 were single
skill → skill → skill handoffs; `/ship` is the one place multiple personas run **in parallel in
one turn** and get merged, per `docs/agents.md`'s fan-out pattern (personas never call each other
directly — only a command orchestrates the fan-out).

---

## 1. `deprecation-and-migration` — plan the bump, don't just edit the version

The skill's process, applied here:

1. **Read release notes for every minor in the jump** (3.2 → 3.3 → 3.4), not just the target —
   a deprecation introduced in 3.3 and removed in 3.4 is invisible if you diff 3.2 straight
   against 3.4 release notes alone.
2. **Scan for deprecated API usage in this codebase specifically.** For this app, the relevant
   checks are:
   - `spring.jpa.hibernate.ddl-auto=create-drop` in `application.properties` — still valid, but
     3.3 changed some Hibernate defaults (open-in-view warning behavior); confirm no other
     property in this file relies on a changed default.
   - No usage of anything from `spring-boot-starter-security` or `WebSecurityConfigurerAdapter`
     in this codebase (it doesn't have Spring Security at all) — so the most common 3.x
     migration pain point doesn't apply here; the skill should note that as a checked-and-clear
     item, not skip the check silently.
   - `springdoc-openapi` 2.3.0 → 2.8.6 is a larger jump than the Boot bump; check springdoc's own
     changelog for breaking changes to annotation behavior (`@Operation`, `@Tag` usage in
     `ProductController`/`OrderController` is stable API, low risk).
3. **Stage the upgrade** rather than one big edit: bump the parent version alone first, build,
   fix anything that breaks; then bump springdoc alone, build again. Two independent variables,
   two independent build-green checkpoints — if something breaks you know which bump caused it.

## 2. Stage 1 — bump `spring-boot-starter-parent`

```xml
<!-- pom.xml -->
<version>3.4.0</version> <!-- was 3.2.0 -->
```

```bash
./mvnw clean test
```

Expected: green, since this app has no deprecated-API usage per step 1's scan. If a real project
had, e.g., a custom `WebMvcConfigurer` method removed in the target version, this is the
checkpoint where the build tells you before you've also changed springdoc.

## 3. Stage 2 — bump `springdoc-openapi`

```xml
<version>2.8.6</version> <!-- was 2.3.0 -->
```

```bash
./mvnw clean test
curl http://localhost:8080/v3/api-docs | jq '.info'   # manual smoke check
```

## 4. `/ship` — the parallel release gate

Once both stages are green, `/ship` is the command that decides whether this merges. It fans out
three personas **in parallel, in a single turn** — this is the mechanical difference from
scenarios 1-2, where each skill ran, finished, and hands off. Per `docs/agents.md`, the personas
do not talk to each other; `/ship` alone reads all three outputs and merges them:

- **`code-reviewer`** — reviews the actual diff (two `pom.xml` version bumps). Low surface area,
  but should confirm the `java-best-practices.md` standing rule's "keep dependency graph small"
  isn't violated and that no transitive-dependency version got silently pinned that shouldn't be.
- **`security-auditor`** — checks whether either bump touches a CVE. This is exactly the kind of
  check worth automating for a dependency bump specifically: springdoc 2.3.0 → 2.8.6 spans
  several patch releases that may include CVE fixes, which is itself a reason *to* ship, not a
  blocker — the auditor's job is to surface that context in the decision, not just look for new
  risk.
- **`test-engineer`** — confirms `./mvnw test` was actually run at both checkpoints (not just the
  final state), and flags that this app's test suite (3 tests, all in `product/`) has zero
  coverage on `OrderService`/`OrderController` — a real gap this migration didn't introduce but
  that a release gate is a legitimate place to flag.

### Sample merged `/ship` output

```markdown
## Ship decision: Spring Boot 3.2.0 → 3.4.0 + springdoc 2.3.0 → 2.8.6

**Verdict: GO**, with one follow-up ticket required before the *next* release.

- code-reviewer: clean, two-line diff, staged correctly. No new lint/style issues.
- security-auditor: springdoc 2.8.6 resolves 2 low-severity CVEs present in 2.3.0 (advisory
  check — verify current CVE feed at ship time). No new CVEs introduced by the Boot bump.
- test-engineer: both staged builds green. BLOCKING for *this* diff: none. Follow-up (non-
  blocking): OrderService/OrderController have no test coverage — file a ticket, don't hold this
  upgrade hostage to unrelated pre-existing debt.

### Rollback plan
Revert the two-line pom.xml diff (parent version, springdoc version) and redeploy the previous
build artifact. No data migration, no schema change in this bump — rollback is a clean version
pin revert, not a database rollback.
```

That "GO, with a non-blocking follow-up filed separately" shape is the point: the gate merges
three independent judgments into one decision instead of a human skimming three separate reports
and guessing which findings are actually blocking.

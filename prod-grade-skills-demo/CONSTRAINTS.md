# Constraints

Last reviewed: 2026-09-22 by kaushik912

## Floor (always enforced, no setup required)

- No new suppression comments: `@SuppressWarnings`, empty `catch {}`
- No unimplemented stubs: `throw new UnsupportedOperationException(...)`, `TODO`/`FIXME` standing
  in for real logic
- No skipped (`@Disabled`) or deleted tests without a reason in the commit message
- No secrets in source (checked by `gitleaks`, see below)
- This file does not get weakened to make a change pass

Verified clean on the current branch (2026-09-22): no suppressions, no empty catches, no
disabled tests, no secrets.

## Enforced with numbers

| Dimension | Rule | Checked by | Runs at |
|-----------|------|-----------|---------|
| Types | Zero compile errors | `./mvnw compile` | every edit (`scripts/check-fast.sh`) |
| Secrets | No secrets in source | `gitleaks detect --source . --redact --no-banner` | every edit (`scripts/check-fast.sh`) |
| Tests | Full suite passes | `./mvnw test` | task end (`scripts/check-task.sh`) |
| Coverage | Line coverage ≥ 81% (ratchet, see below) | `./mvnw verify` (`jacoco-maven-plugin` check goal, `pom.xml`) | review/CI (`scripts/check-full.sh`) |

No linter is configured in this project (no Checkstyle/Spotless/PMD in `pom.xml`) — adding one is
out of scope for this pass; flagging it as a gap rather than inventing a config.

## Measured, not yet enforced

| Metric | Today (2026-09-22) | Direction |
|--------|-------|-----------|
| Line coverage (JaCoCo, `target/site/jacoco/jacoco.csv`) | 82.02% (73/89 lines) | must not fall below 81% (ratchet, ~1% tolerance) |

## Exceptions

None currently.

## Enforcement level

- **Floor**: blocks always.
- **Types, secrets, tests**: block at their stage (edit loop / task end).
- **Coverage**: **warn-only for the first two weeks** (until 2026-10-06), then block. This gives
  time to tune the 81% ratchet before it can fail a build.

## Speed budget

- Edit loop (`scripts/check-fast.sh`): compile + gitleaks, a few seconds.
- Task end (`scripts/check-task.sh`): + full test suite (~30s locally, well under the 90s budget).
- Review/CI (`scripts/check-full.sh`): + coverage gate, no time limit.

## Dropped from scope

Dependency vulnerability scanning (`osv-scanner`) was set up and run (found 3 findings, all
rooted in the intentionally-pinned Spring Boot 3.2.0 — see `docs/scenarios/03-migration-spring-boot-upgrade.md`)
then removed for simplicity at the user's request (2026-09-22). Re-add if/when that pin is lifted
or dependency risk becomes a concern again.

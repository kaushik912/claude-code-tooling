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
| Security: dependencies | No *new* direct-dependency findings beyond the 3 baselined below | `osv-scanner scan --experimental-no-resolve .` | review/CI (`scripts/check-full.sh`) |

No linter is configured in this project (no Checkstyle/Spotless/PMD in `pom.xml`) — adding one is
out of scope for this pass; flagging it as a gap rather than inventing a config.

## Measured, not yet enforced

| Metric | Today (2026-09-22) | Direction |
|--------|-------|-----------|
| Line coverage (JaCoCo, `target/site/jacoco/jacoco.csv`) | 82.02% (73/89 lines) | must not fall below 81% (ratchet, ~1% tolerance) |
| Direct-dependency vulnerabilities (`osv-scanner --experimental-no-resolve`) | 3 findings (see Exceptions) | must not grow |
| Transitive-dependency vulnerabilities (`osv-scanner -r .`, full resolution) | ~80+ findings observed in one successful run | informational only — see note below |

**Note on `osv-scanner -r .` (transitive resolution):** this needs network access to deps.dev to
resolve Spring Boot's managed versions; in this environment it succeeded once (showing ~80
findings, all rooted in the Spring Boot 3.2.0 / Tomcat 10.1.16 / Spring Framework 6.1.1 pin) then
failed on every retry ("Attempted to scan lockfile but failed"). `scripts/check-full.sh` uses
`--experimental-no-resolve` instead — it only sees the 3 *direct* declared dependencies but runs
reliably offline-ish (still needs to reach the OSV API, not deps.dev). Prefer `osv-scanner -r .`
in CI where network is reliable, for full transitive coverage.

## Exceptions

| ID | Rule | Path | Reason | Owner | Expires |
|----|------|------|--------|-------|---------|
| S1 | Security: dependencies (3 direct-dep findings: `h2` CVSS 9.8, `spring-boot-starter-actuator` CVSS 8.2, `spring-boot-starter-web` CVSS 9.8) | `pom.xml` (`spring-boot-starter-parent` pinned to `3.2.0`) | Intentional, checked-in baseline for `docs/scenarios/03-migration-spring-boot-upgrade.md` — the pin exists specifically so that scenario can demo the upgrade workflow. Fixing it here would defeat that scenario. | kaushik912 | 2026-12-22 (tracks the scenario 3 walkthrough; revisit if it's still pinned by then) |

## Enforcement level

- **Floor**: blocks always.
- **Types, secrets, tests**: block at their stage (edit loop / task end).
- **Coverage, dependency scan**: **warn-only for the first two weeks** (until 2026-10-06), then
  block. This gives time to tune the 81% ratchet and confirm the dependency baseline is stable
  before either one can fail a build.

## Speed budget

- Edit loop (`scripts/check-fast.sh`): compile + gitleaks, a few seconds.
- Task end (`scripts/check-task.sh`): + full test suite (~30s locally, well under the 90s budget).
- Review/CI (`scripts/check-full.sh`): + coverage gate + dependency scan, no time limit.

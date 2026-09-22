#!/usr/bin/env bash
# Review/CI checks: task checks + coverage gate + dependency scan. No time budget.
set -euo pipefail
cd "$(dirname "$0")/.."

./scripts/check-task.sh
./mvnw -q verify   # runs jacoco:check -- fails the build if line coverage drops below the ratchet floor

# osv-scanner exits non-zero on ANY finding, including the 3 baselined in CONSTRAINTS.md
# (exception S1 -- pinned Spring Boot 3.2.0, tracked for docs/scenarios/03-migration-spring-boot-upgrade.md).
# Fail only if a NEW finding shows up beyond that baseline.
BASELINE=3
findings=$(osv-scanner scan --experimental-no-resolve . 2>&1 | grep -c "osv.dev" || true)
echo "osv-scanner: $findings finding(s) (baseline: $BASELINE, see CONSTRAINTS.md exception S1)"
if [ "$findings" -gt "$BASELINE" ]; then
    echo "FAIL: new dependency vulnerability findings beyond the baseline -- investigate before adding an exception" >&2
    exit 1
fi
# Full transitive scan (`osv-scanner scan -r .`) is more thorough but depends on deps.dev and was
# flaky in this environment -- prefer it in CI where network is reliable.

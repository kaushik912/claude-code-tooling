#!/usr/bin/env bash
# Review/CI checks: task checks + coverage gate. No time budget.
set -euo pipefail
cd "$(dirname "$0")/.."

./scripts/check-task.sh
./mvnw -q verify   # runs jacoco:check -- fails the build if line coverage drops below the ratchet floor

#!/usr/bin/env bash
# Task-end checks: fast checks + full test suite with coverage report. Target: under 90s.
set -euo pipefail
cd "$(dirname "$0")/.."

./scripts/check-fast.sh
./mvnw -q test

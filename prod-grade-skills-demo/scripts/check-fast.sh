#!/usr/bin/env bash
# Edit-loop checks: compile (types) + secrets. Keep this fast -- see CONSTRAINTS.md.
set -euo pipefail
cd "$(dirname "$0")/.."

./mvnw -q compile
gitleaks detect --source . --redact --no-banner

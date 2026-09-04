#!/usr/bin/env bash
set -uo pipefail

ROOT="${1:-$(pwd)}"

if [[ ! -d "$ROOT" ]]; then
  echo "Directory not found: $ROOT" >&2
  exit 1
fi

printf 'Scanning for Maven projects under %s\n\n' "$ROOT"

found=0
pass=0
fail=0

for pom in $(find "$ROOT" -name pom.xml -type f | sort); do
  project_dir="$(dirname "$pom")"
  project_name="$(basename "$project_dir")"
  found=$((found + 1))

  echo "=== $project_name ==="
  if (cd "$project_dir" && mvn -q -DskipTests compile); then
    echo "PASS: $project_name"
    pass=$((pass + 1))
  else
    echo "FAIL: $project_name"
    fail=$((fail + 1))
  fi
  echo

done

printf '\nSummary\n'
printf 'Projects checked: %s\n' "$found"
printf 'PASS: %s\n' "$pass"
printf 'FAIL: %s\n' "$fail"

if [[ "$fail" -gt 0 ]]; then
  exit 1
fi

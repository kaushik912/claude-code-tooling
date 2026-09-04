#!/usr/bin/env bash
set -euo pipefail

cd "${1:-$(pwd)}"

for d in */; do
  if [ -d "$d/.git" ]; then
    if ! git -C "$d" remote get-url origin >/dev/null 2>&1; then
      echo "$d"
    fi
  fi
done

#!/usr/bin/env bash
set -euo pipefail

if [[ $# -ne 1 ]]; then
  echo "Usage: $0 <topic-folder>/<project-name>" >&2
  exit 1
fi

PROJ="$1"

cat <<EOF
cd /home/kaush/github_projs/$PROJ
git branch -M main
git remote add origin https://github.com/kaushik912/$PROJ.git
git push -u origin main
EOF

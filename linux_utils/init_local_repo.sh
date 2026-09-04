#!/usr/bin/env bash
set -euo pipefail

if [[ $# -ne 1 ]]; then
  echo "Usage: $0 /path/to/project" >&2
  exit 1
fi

DIR="$1"

if [[ ! -d "$DIR" ]]; then
  echo "Directory does not exist: $DIR" >&2
  exit 1
fi

DIR="$(cd "$DIR" && pwd)"

if [[ -d "$DIR/.git" ]]; then
  echo "Already a git repo: $DIR"
  exit 0
fi

cd "$DIR"

git init

git branch -M main >/dev/null 2>&1 || true

if ! git config user.name >/dev/null 2>&1; then
  git config user.name "Local User"
fi

if ! git config user.email >/dev/null 2>&1; then
  git config user.email "local.user@example.com"
fi

python3 /home/kaush/github_projs/claude-code-tooling/linux_utils/create_repo_gitignores.py --root "$DIR" --force

git add .

if git diff --cached --quiet --ignore-submodules --; then
  echo "No staged changes to commit in $DIR"
  exit 0
fi

git commit -m "Initial commit"

echo "Initialized and committed locally: $DIR"

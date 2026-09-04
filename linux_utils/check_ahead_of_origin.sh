#!/usr/bin/env bash
set -euo pipefail

# Script to check which repos are ahead of origin
# Shows repos with unpushed commits

WORKSPACE_DIR="${1:-.}"
AHEAD_REPOS=()

echo "Checking repos in $WORKSPACE_DIR..."
echo ""

# Iterate through all directories
for dir in "$WORKSPACE_DIR"/*; do
  if [[ ! -d "$dir" ]]; then
    continue
  fi

  # Check if it's a git repo
  if [[ ! -d "$dir/.git" ]]; then
    continue
  fi

  repo_name=$(basename "$dir")
  
  # Get current branch
  cd "$dir"
  
  current_branch=$(git rev-parse --abbrev-ref HEAD 2>/dev/null || echo "detached")
  
  # Check if origin exists
  if ! git remote get-url origin &>/dev/null; then
    echo "⚠️  $repo_name: No origin remote"
    cd - > /dev/null
    continue
  fi

  # Count commits ahead of origin
  commits_ahead=$(git rev-list --count "$current_branch@{u}".."$current_branch" 2>/dev/null || echo "0")
  
  if [[ "$commits_ahead" -gt 0 ]]; then
    AHEAD_REPOS+=("$repo_name: $commits_ahead commit(s)")
    echo "✓ $repo_name ($current_branch): $commits_ahead commit(s) ahead of origin"
  fi
  
  cd - > /dev/null
done

echo ""
echo "================================"
echo "Summary:"
if [[ ${#AHEAD_REPOS[@]} -eq 0 ]]; then
  echo "No repos are ahead of origin"
else
  echo "${#AHEAD_REPOS[@]} repo(s) ahead of origin:"
  for repo in "${AHEAD_REPOS[@]}"; do
    echo "  - $repo"
  done
fi

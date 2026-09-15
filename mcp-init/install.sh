#!/usr/bin/env bash
# Installs a bash alias for a script in this directory, pointed at wherever
# this script currently lives. Re-run after moving/re-cloning the repo and
# the alias self-corrects — no manual path editing.
set -euo pipefail

ALIAS_NAME="mcp-init"
TARGET_SCRIPT="mcp-init.py"
RUNNER="python3"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ALIAS_LINE="alias ${ALIAS_NAME}=\"${RUNNER} ${SCRIPT_DIR}/${TARGET_SCRIPT}\""
BASHRC="${HOME}/.bashrc"

if grep -qF "alias ${ALIAS_NAME}=" "$BASHRC" 2>/dev/null; then
  sed -i "\|alias ${ALIAS_NAME}=|c\\${ALIAS_LINE}" "$BASHRC"
  echo "Updated existing '${ALIAS_NAME}' alias in ${BASHRC}"
else
  {
    echo ""
    echo "# ${ALIAS_NAME}: interactive project-level .mcp.json creator (claude-code-tooling/mcp-init)"
    echo "${ALIAS_LINE}"
  } >> "$BASHRC"
  echo "Added '${ALIAS_NAME}' alias to ${BASHRC}"
fi

echo "Run 'source ${BASHRC}' or open a new shell to use it."

#!/usr/bin/env bash
# On-demand diagnostic agent for the hello-demo workspace. Reuses (or creates)
# a Claude agent named "diagnoser" in an "agent" tab and asks it to explain
# the most recent backend error. Safe to re-run any time; pass a custom
# question as $1 to ask something else instead.
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LOG_FILE="$ROOT/backend/logs/server.log"
question="${1:-}"

workspace_id=$(herdr workspace list | jq -r '.result.workspaces[] | select(.label=="hello-demo") | .workspace_id' | head -1)
if [ -z "$workspace_id" ]; then
  echo "No 'hello-demo' workspace found. Run ./start-herdr-tabs.sh first." >&2
  exit 1
fi

tabs_json=$(herdr tab list --workspace "$workspace_id")
panes_json=$(herdr pane list --workspace "$workspace_id")

pane_for_label() {
  local label="$1"
  local tab_id
  tab_id=$(echo "$tabs_json" | jq -r --arg l "$label" '.result.tabs[] | select(.label==$l) | .tab_id')
  echo "$panes_json" | jq -r --arg t "$tab_id" '.result.panes[] | select(.tab_id==$t) | .pane_id'
}

backend_pane_id=$(pane_for_label backend)
logs_pane_id=$(pane_for_label logs)

agent_pane_id=$(herdr agent list | jq -r '.result.agents[] | select(.name=="diagnoser") | .pane_id' | head -1)

if [ -z "$agent_pane_id" ]; then
  agent_json=$(herdr tab create --workspace "$workspace_id" --cwd "$ROOT" --label diagnoser --focus)
  agent_pane_id=$(echo "$agent_json" | jq -r '.result.root_pane.pane_id')
  herdr agent start diagnoser --kind claude --pane "$agent_pane_id"
fi

if [ -n "$question" ]; then
  prompt="$question Use \`herdr pane read $backend_pane_id --lines 30\` and \`herdr pane read $logs_pane_id --lines 10\` if you need the backend stack trace or request log."
else
  last_error=$(grep "ERROR:" "$LOG_FILE" 2>/dev/null | tail -1 || true)
  if [ -z "$last_error" ]; then
    echo "No errors found in $LOG_FILE." >&2
    exit 1
  fi
  prompt="The most recent backend error is: $last_error. Run \`herdr pane read $backend_pane_id --lines 30\` to see the stack trace and \`herdr pane read $logs_pane_id --lines 10\` to see the request log line. In 2-3 sentences, say what broke and how to fix it."
fi

herdr agent prompt diagnoser "$prompt" --wait --until idle --until done --until blocked --timeout 120000

echo "--- diagnoser output ---"
herdr agent read diagnoser --lines 40

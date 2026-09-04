#!/usr/bin/env bash
# Adds a "monitor" tab to the running hello-demo workspace with a Claude
# agent that watches backend/logs/server.log and diagnoses each new
# ERROR-tagged line as it appears. Runs until Ctrl-C.
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LOG_FILE="$ROOT/backend/logs/server.log"

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

monitor_json=$(herdr tab create --workspace "$workspace_id" --cwd "$ROOT" --label monitor --focus)
monitor_pane_id=$(echo "$monitor_json" | jq -r '.result.root_pane.pane_id')

herdr agent start monitor --kind claude --pane "$monitor_pane_id"

echo "Watching $LOG_FILE for new ERROR lines. Ctrl-C to stop."

last_count=$(wc -l < "$LOG_FILE" 2>/dev/null || echo 0)

while true; do
  sleep 3
  cur_count=$(wc -l < "$LOG_FILE" 2>/dev/null || echo 0)
  if [ "$cur_count" -gt "$last_count" ]; then
    new_lines=$(tail -n "$((cur_count - last_count))" "$LOG_FILE")
    last_count=$cur_count
    error_line=$(echo "$new_lines" | grep "ERROR:" | tail -1 || true)
    if [ -n "$error_line" ]; then
      prompt="A new error just appeared in backend/logs/server.log: $error_line. Run \`herdr pane read $backend_pane_id --lines 30\` for the stack trace, then say what broke and how to fix it in 2-3 sentences."
      herdr agent prompt monitor "$prompt" --wait --until idle --until done --until blocked --timeout 120000
      echo "--- monitor diagnosis ($(date -u +%FT%TZ)) ---"
      herdr agent read monitor --lines 40
    fi
  fi
done

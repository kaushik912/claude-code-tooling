#!/usr/bin/env bash
# Sets up a herdr workspace with 3 tabs for the hello-demo app:
#   frontend -> vite dev server
#   backend  -> express dev server
#   logs     -> tail of backend request log
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
FRONTEND_DIR="$ROOT/frontend"
BACKEND_DIR="$ROOT/backend"

mkdir -p "$BACKEND_DIR/logs"
touch "$BACKEND_DIR/logs/server.log"

ws_json=$(herdr workspace create --cwd "$ROOT" --label hello-demo --focus)
workspace_id=$(echo "$ws_json" | jq -r '.result.workspace.workspace_id')
frontend_tab_id=$(echo "$ws_json" | jq -r '.result.tab.tab_id')
frontend_pane_id=$(echo "$ws_json" | jq -r '.result.root_pane.pane_id')

herdr tab rename "$frontend_tab_id" frontend
herdr pane run "$frontend_pane_id" "cd '$FRONTEND_DIR' && npm run dev"

backend_json=$(herdr tab create --workspace "$workspace_id" --cwd "$BACKEND_DIR" --label backend --no-focus)
backend_pane_id=$(echo "$backend_json" | jq -r '.result.root_pane.pane_id')
herdr pane run "$backend_pane_id" "npm run dev"

logs_json=$(herdr tab create --workspace "$workspace_id" --cwd "$BACKEND_DIR" --label logs --focus)
logs_pane_id=$(echo "$logs_json" | jq -r '.result.root_pane.pane_id')
herdr pane run "$logs_pane_id" "tail -f logs/server.log"

echo "Workspace '$workspace_id' ready: frontend, backend, logs tabs started."

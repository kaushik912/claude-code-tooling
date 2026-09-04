#!/usr/bin/env python3
import json
import sys
from datetime import datetime, timezone

AUDIT_LOG_PATH = "/home/kaush/github_projs/claude-code-tooling/mycustom_hooks/.claude/hooks/audit.jsonl"


def audit(tool_input):
    entry = {
        "timestamp": datetime.now(timezone.utc).isoformat(),
        "tool": tool_input.get("tool_name", "unknown"),
        "command": tool_input.get("tool_input", {}).get("command", ""),
        "file": tool_input.get("tool_input", {}).get("file_path", ""),
        "session_id": tool_input.get("session_id", "unknown"),
    }
    with open(AUDIT_LOG_PATH, "a") as f:
        f.write(json.dumps(entry) + "\n")


if __name__ == "__main__":
    try:
        payload = json.load(sys.stdin)
    except (json.JSONDecodeError, ValueError):
        sys.exit(0)
    audit(payload)

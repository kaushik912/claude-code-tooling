#!/usr/bin/env python3
import json
import os
import sys
import urllib.request


def build_summary(session_id):
    return f"✅ Claude Code finished responding in mycustom_hooks (session {session_id})"


def post_to_slack(text):
    webhook_url = os.environ.get("SLACK_WEBHOOK_URL")
    if not webhook_url:
        return
    payload = json.dumps({"text": text}).encode("utf-8")
    req = urllib.request.Request(
        webhook_url, data=payload, headers={"Content-Type": "application/json"}
    )
    try:
        urllib.request.urlopen(req, timeout=5)
    except Exception:
        pass


if __name__ == "__main__":
    try:
        payload = json.load(sys.stdin)
    except (json.JSONDecodeError, ValueError):
        sys.exit(0)
    session_id = payload.get("session_id", "unknown")
    post_to_slack(build_summary(session_id))

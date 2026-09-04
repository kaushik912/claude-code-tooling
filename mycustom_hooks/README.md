# mycustom_hooks

Custom Claude Code hooks for this project.

## What are hooks?

Hooks are shell commands Claude Code runs automatically on lifecycle events
(e.g. `PreToolUse`, `PostToolUse`, `Stop`, `UserPromptSubmit`). Configured in
`.claude/settings.json` (shared, committed) or `.claude/settings.local.json`
(personal, gitignored).

## Secrets

Hooks often need secrets (webhook URLs, API keys, tokens) to notify external
services. Keep these in `.claude/settings.local.json` under `env`, **never**
in `settings.json`. `settings.local.json` is gitignored in this repo, so
personal secrets don't leak into version control.

```json
{
  "env": {
    "SLACK_WEBHOOK_URL": "<your-webhook-url>"
  }
}
```

## Example hook

Notify Slack when Claude finishes responding (`Stop` event). Rather than a
one-line `curl`, this is a script (`.claude/hooks/slack_session_summary.py`)
so it can build a real session summary: it reads the session's entries from
`.claude/hooks/audit.jsonl` (written by the `PreToolUse` audit hook), counts
tool usage, lists touched files, and posts the summary to Slack using the
webhook URL from `env` above.

```json
{
  "hooks": {
    "Stop": [
      {
        "hooks": [
          {
            "type": "command",
            "command": "/path/to/.venv/bin/python3 /path/to/.claude/hooks/slack_session_summary.py 2>/dev/null || true"
          }
        ]
      }
    ]
  }
}
```

## Future ideas

- **PreToolUse guard**: block `rm -rf`, `git push --force`, etc. before they run.
- **Audit log**: append every tool call to `.claude/hooks/audit.jsonl` for later review.
- **Desktop notification**: `notify-send` on `Stop` when a long task finishes.
- **Lint/format on save**: `PostToolUse` hook on `Edit`/`Write` to auto-run linters.
- **Secret scanner**: `PreToolUse` hook on `Bash`/`Edit` that greps staged content for
  API-key-shaped strings before allowing the action.

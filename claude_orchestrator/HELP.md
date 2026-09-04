# Setup (one-time)
python3 -m venv .venv
.venv/bin/pip install anyio claude-code-sdk

# AI Invocation - directory of prompt .md files, with timeout (seconds)
.venv/bin/python pipeline/ai_workflow_runner.py --dir samples --timeout 120

# AI Invocation - single prompt file
.venv/bin/python pipeline/ai_workflow_runner.py --file samples/a.md --timeout 120

# AI Invocation using JSON workflow
.venv/bin/python pipeline/ai_workflow_runner.py --json inputs/aitest.json --timeout 120

# NOTE: each prompt runs as `claude -p <prompt> --dangerously-skip-permissions`
# (permission checks disabled) - review prompt files before running.
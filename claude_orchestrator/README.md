# claude-orchestrator

Headless runner that batches prompts through the `claude` CLI. Feed it prompt files, a directory of prompts, or a JSON workflow — it executes each via `claude -p ... --dangerously-skip-permissions`, logs everything, and optionally auto-commits.

## Architecture

- `pipeline/ai_workflow_runner.py` — CLI entrypoint. Modes: `--file`, `--dir` (all `*.md` in a folder), `--json` (ordered workflow spec). Per-prompt or global `--timeout`. Logs to `claude_execution_<ts>.log`.
- `pipeline/claude_prompt_executor.py` — executes a single prompt.
  - `execute_prompt_cli()` — shells out to `claude` CLI, parses `stream-json` output, dumps raw stdout to `claude_cli_execution_<ts>.log`. **Used by default.**
  - `execute_prompt()` — alt path via `claude_code_sdk` (hardcoded cwd/tools, currently unused by the runner).
- `docprompts/` — per-project prompt templates (this README's prompt included) for driving doc generation.
- `samples/` — example single-line prompt files for smoke-testing the runner.
- `results/` — generated outputs land here (e.g. `expense-tracker_README.md`).

## Key facts

- Permissions are **skipped** on every run (`--dangerously-skip-permissions`) — review prompt files before executing.
- Git auto-commit exists but is **disabled** by default (`ENABLE_GIT_COMMITS = False` in `ai_workflow_runner.py`).
- JSON workflow schema: `{"ai_prompts": [{"type": "file"|"dir", "value": "<path>", "order": int, "timeout": int}]}`, sorted by `order`.

## Running via cron

```
PATH=/home/kaush/.local/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin
TZ=Asia/Kolkata
0 0 * * * cd /home/kaush/github_projs/claude-code-tooling/claude_orchestrator && .venv/bin/python pipeline/ai_workflow_runner.py --dir docprompts --timeout 300 >> /home/kaush/github_projs/claude-code-tooling/claude_orchestrator/results/cron.log 2>&1
```

## Usage

```bash
# one-time setup
python3 -m venv .venv
.venv/bin/pip install anyio claude-code-sdk

# run a directory of prompts
.venv/bin/python pipeline/ai_workflow_runner.py --dir samples --timeout 120

# run a single prompt file
.venv/bin/python pipeline/ai_workflow_runner.py --file samples/a.md --timeout 120

# run a JSON-defined workflow
.venv/bin/python pipeline/ai_workflow_runner.py --json inputs/aitest.json --timeout 120
```

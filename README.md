# claude-code-tooling

Tools and utilities built around [Claude Code](https://claude.com/claude-code) —
CLI helpers, orchestration scripts, and hooks. Each subdirectory is
self-contained; clone this repo and `cd` into the one you need.

## Tools

### [agent-porter](agent-porter/)
Converts and installs agent definitions between Claude Code's `.claude/agents/*.md`
subagent format and GitHub Copilot's `.github/agents/*.agent.md` custom-agent
format.

```bash
git clone https://github.com/kaushik912/claude-code-tooling.git
cd claude-code-tooling/agent-porter
npm install
npm link   # optional: makes `agent-porter` runnable from anywhere
```

See [agent-porter/README.md](agent-porter/README.md) for full usage.

### [batch-utils](batch-utils/)
Python scripts to batch source files into a single markdown document (for
sharing code where attachments are limited, or feeding code to an AI) and
unbatch them back to their original file structure.

```bash
cd claude-code-tooling/batch-utils
python3 batch_files.py <src-dir>
python3 unbatch_files.py <batched-file>
```

No install step — copy the two scripts into any repo, or run them from here.
See [batch-utils/README.md](batch-utils/README.md).

### [claude_orchestrator](claude_orchestrator/)
Headless runner that batches prompts through the `claude` CLI (`claude -p ...`) —
feed it a prompt file, a directory of prompts, or a JSON workflow spec. Used for
automated doc-generation via cron.

```bash
cd claude-code-tooling/claude_orchestrator
python3 -m venv .venv
.venv/bin/pip install anyio claude-code-sdk
.venv/bin/python pipeline/ai_workflow_runner.py --dir docprompts --timeout 300
```

See [claude_orchestrator/README.md](claude_orchestrator/README.md) for
architecture and the cron setup.

### [mycustom_hooks](mycustom_hooks/)
Example Claude Code lifecycle hooks: `PreToolUse` audit logging to a local
JSONL file, and a `Stop`-event Slack session summary.

```bash
cd claude-code-tooling/mycustom_hooks
python3 -m venv .venv   # scripts are pure stdlib, no dependencies to install
```

Then point a project's `.claude/settings.json` at the hook scripts here — see
[mycustom_hooks/README.md](mycustom_hooks/README.md) for the full config and
how to keep secrets (Slack webhook URL, etc.) out of version control.

## Other projects in this repo

`agent-testing`, `herdr_demo`, `linux_utils`, `tmux-claude`, `topClaudeSkills` —
see each directory for details.

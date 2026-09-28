# topClaudePlugins

Reference repo for the Claude Code plugins I use. All are enabled via `.claude/settings.json` (`enabledPlugins`) — no stray skill/command files in the repo.

## Plugins

| Plugin | Marketplace | Benefit |
|--------|-------------|---------|
| superpowers | claude-plugins-official | Structured dev flow (brainstorm → plan → execute → review), TDD, systematic debugging, verification before "done" |
| example-skills | anthropic-agent-skills | Anthropic's reference skills: frontend-design, doc-coauthoring, mcp-builder, webapp-testing, skill-creator, etc. |
| code-simplifier | claude-plugins-official | Refactors recent code for clarity/consistency without changing behavior |
| terrashark | terrashark | Terraform diagnostics against HashiCorp best practices |
| ai-toolkit (Spartan) | spartan-marketplace | Workflow commands (`/spartan:*`): spec → plan → build → PR, quality gates, Terraform, Next.js, Kotlin scaffolding |
| caveman | caveman | Ultra-compressed replies — fewer output tokens, same technical accuracy; brief commit/review helpers |
| planetscale | claude-plugins-official | Safe SQL branching, schema/index/N+1 best practices, MySQL/Postgres/Vitess guidance, MCP access |
| context7 | claude-plugins-official | Up-to-date library/framework docs via MCP instead of stale training data |
| modern-web-guidance | claude-plugins-official | Current modern-web and Chrome-extension guidance |
| vercel | claude-plugins-official | Deploy/env/status commands, Next.js, AI SDK, workflow, caching, functions skills, plus deploy/perf/AI agents and MCP |

## Why plugins (not local skills)

- One line in `settings.json` per plugin; reproducible across machines.
- Updates come from the marketplace, no vendored `SKILL.md` copies to drift.
- Nothing to clean up in `.claude/`, `.agents/`, or `skills-lock.json`-style lockfiles.

## Setup

```
/plugin marketplace add <owner/repo>      # add a marketplace
/plugin install <plugin>@<marketplace>    # install (project scope: --scope project)
```

Or add to `.claude/settings.json`:

```json
{ "enabledPlugins": { "superpowers@claude-plugins-official": true } }
```

Restart the session after changing plugins.

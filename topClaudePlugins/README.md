# topClaudePlugins

Reference repo for the Claude Code plugins I use. All are enabled via `.claude/settings.json` (`enabledPlugins`) — no stray skill/command files in the repo.

## Plugins

### superpowers

- **Marketplace:** claude-plugins-official
- **Benefit:** Structured dev flow (brainstorm → plan → execute → review), TDD, systematic debugging, verification before "done".

### example-skills

- **Marketplace:** anthropic-agent-skills
- **Benefit:** Anthropic's reference skills: frontend-design, doc-coauthoring, mcp-builder, webapp-testing, skill-creator, etc.

### code-simplifier

- **Marketplace:** claude-plugins-official
- **Benefit:** Refactors recent code for clarity/consistency without changing behavior.

### terrashark

- **Marketplace:** terrashark
- **Benefit:** Terraform diagnostics against HashiCorp best practices.

### ai-toolkit (Spartan)

- **Marketplace:** spartan-marketplace
- **Benefit:** Workflow commands (`/spartan:*`): spec → plan → build → PR, quality gates, Terraform, Next.js, Kotlin scaffolding.

### caveman

- **Marketplace:** caveman
- **Benefit:** Ultra-compressed replies — fewer output tokens, same technical accuracy; brief commit/review helpers.

### planetscale

- **Marketplace:** claude-plugins-official
- **Benefit:** Safe SQL branching, schema/index/N+1 best practices, MySQL/Postgres/Vitess guidance, MCP access.

### context7

- **Marketplace:** claude-plugins-official
- **Benefit:** Up-to-date library/framework docs via MCP instead of stale training data.

### modern-web-guidance

- **Marketplace:** claude-plugins-official
- **Benefit:** Current modern-web and Chrome-extension guidance.

### vercel

- **Marketplace:** claude-plugins-official
- **Benefit:** Deploy/env/status commands, Next.js, AI SDK, workflow, caching, functions skills, plus deploy/perf/AI agents and MCP.

### fullstack-dev-skills

- **Marketplace:** fullstack-dev-skills
- **Benefit:** 67 skills for full-stack devs: 12 language experts (Python, TS, Go, Rust, Java, Kotlin, etc.), backend/frontend/mobile frameworks, DevOps, security, testing, plus Jira/Confluence project-management workflows.

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

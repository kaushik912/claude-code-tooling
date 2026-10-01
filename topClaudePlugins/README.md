# topClaudePlugins

Reference repo for the Claude Code plugins I use. Each is enabled via `.claude/settings.json` (`enabledPlugins`) — no stray skill/command files in the repo.

Run commands from the target project dir (project scope is tied to cwd). Add the marketplace once per machine, then install. `claude-plugins-official` ships built in; if missing: `claude plugin marketplace add anthropics/claude-plugins-official`.

## Plugins

### superpowers  *(enabled here)*

- **Marketplace:** claude-plugins-official
- **Benefit:** Structured dev flow (brainstorm → plan → execute → review), TDD, systematic debugging, verification before "done".

```bash
claude plugin install superpowers@claude-plugins-official --scope project
```

### example-skills  *(enabled here)*

- **Marketplace:** anthropic-agent-skills
- **Benefit:** Anthropic's reference skills: frontend-design, doc-coauthoring, mcp-builder, webapp-testing, skill-creator, etc.

```bash
claude plugin marketplace add anthropics/skills
claude plugin install example-skills@anthropic-agent-skills --scope project
```

### context7  *(enabled here)*

- **Marketplace:** claude-plugins-official
- **Benefit:** Up-to-date library/framework docs via MCP instead of stale training data.

```bash
claude plugin install context7@claude-plugins-official --scope project
```

### code-simplifier

- **Marketplace:** claude-plugins-official
- **Benefit:** Refactors recent code for clarity/consistency without changing behavior.

```bash
claude plugin install code-simplifier@claude-plugins-official --scope project
```

### terrashark

- **Marketplace:** terrashark
- **Benefit:** Terraform diagnostics against HashiCorp best practices.

```bash
claude plugin marketplace add LukasNiessen/terrashark
claude plugin install terrashark@terrashark --scope project
```

### ai-toolkit (Spartan)

- **Marketplace:** spartan-marketplace
- **Benefit:** Workflow commands (`/spartan:*`): spec → plan → build → PR, quality gates, Terraform, Next.js, Kotlin scaffolding.

```bash
claude plugin marketplace add spartan-stratos/spartan-ai-toolkit
claude plugin install ai-toolkit@spartan-marketplace --scope project
```

### caveman

- **Marketplace:** caveman
- **Benefit:** Ultra-compressed replies — fewer output tokens, same technical accuracy; brief commit/review helpers.

```bash
claude plugin marketplace add JuliusBrussee/caveman
claude plugin install caveman@caveman --scope project
```

### planetscale

- **Marketplace:** claude-plugins-official
- **Benefit:** Safe SQL branching, schema/index/N+1 best practices, MySQL/Postgres/Vitess guidance, MCP access.

```bash
claude plugin install planetscale@claude-plugins-official --scope project
```

### modern-web-guidance

- **Marketplace:** claude-plugins-official
- **Benefit:** Current modern-web and Chrome-extension guidance.

```bash
claude plugin install modern-web-guidance@claude-plugins-official --scope project
```

### vercel

- **Marketplace:** claude-plugins-official
- **Benefit:** Deploy/env/status commands, Next.js, AI SDK, workflow, caching, functions skills, plus deploy/perf/AI agents and MCP.

```bash
claude plugin install vercel@claude-plugins-official --scope project
```

### fullstack-dev-skills

- **Marketplace:** fullstack-dev-skills
- **Benefit:** 67 skills for full-stack devs: 12 language experts (Python, TS, Go, Rust, Java, Kotlin, etc.), backend/frontend/mobile frameworks, DevOps, security, testing, plus Jira/Confluence project-management workflows.

```bash
claude plugin marketplace add jeffallan/claude-skills
claude plugin install fullstack-dev-skills@fullstack-dev-skills --scope project
```

## Uninstall

```bash
claude plugin uninstall <plugin>@<marketplace> --scope project   # also drops the enabledPlugins flag
```

Example (run from the project dir):

```bash
claude plugin uninstall caveman@caveman --scope project
```

List what's enabled: `cplug-on` (alias in `~/.bashrc`; wraps `claude plugin list --json | jq`).

## Why plugins (not local skills)

- One line in `settings.json` per plugin; reproducible across machines.
- Updates come from the marketplace, no vendored `SKILL.md` copies to drift.
- Nothing to clean up in `.claude/`, `.agents/`, or `skills-lock.json`-style lockfiles.

## Manual enable

Instead of `install`, add to `.claude/settings.json`:

```json
{ "enabledPlugins": { "superpowers@claude-plugins-official": true } }
```

Restart the session after changing plugins.

# topClaudeSkills

Reference repo tracking the top Claude Code skills/plugins I've installed, why, and how to reinstall them.

## Installed

| # | Skill | Install command | Purpose |
|---|-------|------------------|---------|
| 1 | superpowers | `/plugin marketplace add obra/superpowers-marketplace` + `/plugin install superpowers@superpowers-marketplace` | Structured dev sequence (clarify → spec → plan → execute → review) + TDD |
| 2 | vercel-react-best-practices | `npx skills@latest add vercel/react-best-practices` | Clean React/TS fundamentals (hooks, prop interfaces, error handling) |
| 3 | frontend-design | bundled with Claude Code | Steers AI UI design away from generic defaults |
| 4 | spartan-ai-toolkit | `npx @c0x12c/ai-toolkit@latest --local` | Sequential quality gates: typecheck → lint → test → review |
| 5 | mattpocock/skills | `npx skills@latest add mattpocock/skills/write-a-prd` (+ `request-refactor-plan`, `git-guardrails-claude-code`) | TS-focused PRD/refactor-plan/TDD workflows |
| 6 | planetscale | `npx skills@latest add planetscale/skills` | Safe SQL branching, indexing/N+1 best practices |
| 7 | web-design-guidelines | `npx skills@latest add vercel-labs/web-interface-guidelines` | Audits UI against Vercel interface guidelines |
| 8 | doc-coauthoring | `/plugin marketplace add anthropics/skills` + `/plugin install example-skills@anthropic-agent-skills` | Human-led doc outlining + drafting |
| 9 | code-simplifier | `/plugin marketplace add anthropics/claude-plugins-official` + `/plugin install code-simplifier@claude-plugins-official` | Refactors recent code for readability, no behavior change |
| 10 | TerraShark | `/plugin marketplace add LukasNiessen/terrashark` + `/plugin install terrashark` | Terraform diagnostics against HashiCorp best practices |
| 11 | find-skills | `npx skills add https://github.com/vercel-labs/skills --skill find-skills` | Discovers/installs skills from the open agent skills ecosystem (skills.sh) via `npx skills find`/`add` |

## Status

All installed at **project scope** (this repo's `.claude/` and `.agents/skills/`), not user/global.

- 1, 8, 9, 10 (superpowers, doc-coauthoring, code-simplifier, TerraShark) — installed via `claude plugin marketplace add` + `claude plugin install ... --scope project`.
- 2 (vercel-react-best-practices) — actual package is `vercel-labs/agent-skills@vercel-react-best-practices` (the `vercel/react-best-practices` repo in the original command doesn't exist).
- 3 (frontend-design) — bundled with Claude Code, nothing to install.
- 4 (spartan-ai-toolkit) — installed `core` pack only (`--packs=core`), not `--all`, to avoid pulling in unrelated stacks (Kotlin/Micronaut, Terraform, UX).
- 5 (mattpocock/skills) — only `git-guardrails-claude-code` exists in the repo today; `write-a-prd` and `request-refactor-plan` are gone (repo evolved). User declined substitutes (`to-spec`, `improve-codebase-architecture`).
- 6 (planetscale) — installed all skills in `planetscale/skills` (14 skills).
- 7 (web-design-guidelines) — actual package is `vercel-labs/agent-skills@web-design-guidelines` (the `vercel-labs/web-interface-guidelines` repo in the original command has no SKILL.md).

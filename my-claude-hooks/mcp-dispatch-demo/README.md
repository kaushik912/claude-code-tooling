# MCP Dispatch Demo — Forced Eval Hook Routing Two MCP Servers

Second example of the forced-eval hook pattern (see `../reliable-activation`),
this time routing between two MCP-backed skills instead of code-generation
rules.

## Files
- `.claude/skills/mysql-mcp-query/SKILL.md` — DB question skill, calls the
  `mysql` MCP server
- `.claude/skills/jira-mcp-lookup/SKILL.md` — Jira ticket skill, calls the
  `Atlassian` MCP server
- `.claude/hooks/forced-eval-hook.sh` — UserPromptSubmit hook, injects
  mandatory eval/activate/implement sequence (identical to
  `reliable-activation`'s)
- `.claude/settings.json` — registers the hook
- `.mcp.json` — mysql + Atlassian MCP server config (same as
  `reliable-activation`)

## Test
```
cd mcp-dispatch-demo
claude
```

Prompt 1 (DB): `How many tables are in the database?`
Expect: `mysql-mcp-query` - YES, `jira-mcp-lookup` - NO →
`Skill(mysql-mcp-query)` → live `mcp__mysql__mysql_query` call → plain-language
answer.

Prompt 2 (Jira): `What's the status of SCRUM-8?`
Expect: `jira-mcp-lookup` - YES, `mysql-mcp-query` - NO →
`Skill(jira-mcp-lookup)` → live `mcp__Atlassian__getJiraIssue` call →
plain-language answer.

Each prompt should activate only the matching skill — the other one should
be marked NO.

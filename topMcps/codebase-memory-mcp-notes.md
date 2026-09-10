# codebase-memory-mcp — getting started

1. Install binary (once per machine):
   ```
   curl -fsSL https://raw.githubusercontent.com/DeusData/codebase-memory-mcp/main/install.sh | bash -s -- --skip-config
   ```
2. Per project, add the MCP server:
   ```
   cd <project> && claude mcp add -s project codebase-memory-mcp \
     -e CBM_ALLOWED_ROOT=$(pwd) -- /home/kaush/.local/bin/codebase-memory-mcp
   ```
   Avoid `codebase-memory-mcp install` for this — it's not per-project, it
   globally rewrites `~/.claude.json` + agents/hooks across Claude Code,
   OpenCode, Copilot CLI, Pi. Use `claude mcp add` instead.
3. `claude` → approve server → `/mcp` to confirm connected.
4. Say "Index this project" (or `index_repository(repo_path=<project>)`).
5. Use `list_projects`, `search_graph`, `trace_path`, etc.

**Sample repo with it configured**: `/home/kaush/github_projs/spring-learning/.mcp.json`

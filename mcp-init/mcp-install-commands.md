# MCP install commands — reference

Or run `./mcp-init.py` (from the target project dir) to pick one interactively —
it just runs these same commands out of `mcp-registry.json`, and can save any
new pasted `claude mcp add` command for next time.

All via `claude mcp add` from inside the target project dir (`-s project` writes `.mcp.json` there; add `-s project` if you want it shared/committed, omit for local-only).

## codebase-memory-mcp
```
curl -fsSL https://raw.githubusercontent.com/DeusData/codebase-memory-mcp/main/install.sh | bash -s -- --skip-config
claude mcp add -s project codebase-memory-mcp -e CBM_ALLOWED_ROOT=$(pwd) -- /home/kaush/.local/bin/codebase-memory-mcp
```
See `../topMcps/codebase-memory-mcp-notes.md` for gotchas.

## mysql (@benborla29/mcp-server-mysql)
```
claude mcp add -s project mysql \
  -e MYSQL_HOST=localhost -e MYSQL_PORT=3306 -e MYSQL_USER=root \
  -e MYSQL_PASS='${MYSQL_PWD}' -e MYSQL_DB=<your_db> \
  -- npx -y @benborla29/mcp-server-mysql
```
No separate install — npx pulls it on first run.

## Atlassian
```
claude mcp add -s project Atlassian -- npx -y mcp-remote@latest https://mcp.atlassian.com/v1/mcp/authv2
```
First connect triggers OAuth in browser.

## context7
```
claude mcp add -s project context7 --transport http https://mcp.context7.com/mcp
```
Remote, no local install. (Alt: `npx -y @upstash/context7-mcp` if you want a local stdio server instead.)

## openapi-mcp (local, custom)
Lives at `ai-mcp-lab/openapi-mcp` (moved there from `spec-driven/custom-spec-repo`
on 2026-09-08). `custom-spec-repo/.mcp.json` was fixed to point at the new path.
```
claude mcp add -s project openapi-mcp -- uv --directory /home/kaush/github_projs/ai-mcp-lab/openapi-mcp run openapi-mcp
```

## weather / quotes (local custom demo, ai-mcp-lab/weathermcp)
These are your own FastMCP servers, not a package — start them, then point
Claude at the running URL:
```
cd ai-mcp-lab/weathermcp && pip install -r requirements.txt   # (use a venv)
python weather_mcp.py   # serves on :8001
python quote_mcp.py     # serves on :8002
claude mcp add -s project weather --transport http http://127.0.0.1:8001/mcp
claude mcp add -s project quotes  --transport http http://127.0.0.1:8002/mcp
```

# cart-discount-bug

Practice project for DebugMCP CLI + Python (debugpy).

Expected: `SAVE10` on a 50.0 cart -> 45.0, `HALF` -> 25.0.
Actual: 49.9 and 49.5.

## Setup (venv, project-local)
```bash
cd /home/kaush/github_projs/claude-code-tooling/debugging-ideas/cart-discount-bug
python3 -m venv .venv
.venv/bin/pip install debugpy
npm install debugmcp            # local, not global
npx debugmcp adapter add python --command "$PWD/.venv/bin/python -m debugpy.adapter"
npx debugmcp adapter validate python
```

## Register with Claude Code (project scope)
```bash
claude mcp add -s project debugmcp -- npx debugmcp serve --stdio
```

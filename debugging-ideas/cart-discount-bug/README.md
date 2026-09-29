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
claude mcp add -s project debugmcp-cli -- npx debugmcp serve --stdio
```
Server name is `debugmcp-cli` in `.mcp.json`.

## Usage tip
`start_debugging` returns before the breakpoint hits. Follow with `get_debug_status` + `waitForPauseSeconds` (e.g. 15) to catch the pause.

## Manual debugging (VS Code)
`.vscode/launch.json` has "Python: cart.py" (uses `.venv`). Open this folder as workspace, breakpoint on `cart.py:15`, F5.

## MY NOTE
- The steps are fine but there is some mouse-terminal issue , claude abruptly gets stopped and there are garbage characters seen in the terminal whenever i move my mouse. 
- Need to check if this issue is across agents or just claude.
- Same issue seen in github copilot as well.
- Good news: So the vscode extension for this `ozzafar.debugmcpextension` is working fine.
- Good news: it worked in claude code chat(extension) instead of the cli. No garbled text issue. So that's one workaround.
- Tip: Always first setup a manual debug session and then it's much easier to reason.
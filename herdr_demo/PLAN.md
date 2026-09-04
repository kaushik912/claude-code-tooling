# Herdr learning demo: React + Node "Hello" app across 3 tabs

## Context
User wants a hands-on way to learn `herdr` (installed CLI at `~/.local/bin/herdr`, a
tmux-like workspace/tab/pane manager for coding-agent dev workflows). The vehicle is a
minimal full-stack app (React textfield+button -> Node backend returns `Hello $text`),
run so each concern (frontend dev server, backend dev server, request logs) lives in
its own herdr tab inside one workspace — mirroring a real multi-process dev setup.

`herdr_demo/` currently contains only `quick_guide.md` (a personal cheat-sheet, not
code). Confirmed via `herdr tab --help` / `herdr workspace --help` / `herdr pane
run|split --help` that the right primitives are:
- `herdr workspace create --cwd <path> --label <text> [--focus|--no-focus]`
- `herdr tab create --workspace <id> --cwd <path> --label <text> [--focus|--no-focus]`
- `herdr pane list --workspace <id>` (to find the pane id a new tab created)
- `herdr pane run <pane_id> "<command>"` (to launch the process inside that pane)

Node v24 / npm 11 are available, so backend = Node+Express (no venv needed, one
toolchain end-to-end, per user's choice).

## Setup step 0
Copy this plan file into the repo as `herdr_demo/PLAN.md` (first thing done during
implementation), so it's kept alongside the code for reference.

## App structure

```
herdr_demo/
├── quick_guide.md          (existing, untouched)
├── backend/
│   ├── package.json
│   └── server.js
├── frontend/                (Vite React scaffold: npm create vite@latest . -- --template react)
│   └── src/App.jsx          (edited)
└── start-herdr-tabs.sh      (new: sets up the 3-tab workspace)
```

### `backend/server.js`
- Express app on port 3001, `cors` enabled for `http://localhost:5173`.
- Middleware that logs every request as one line (`timestamp METHOD path -> status`)
  to `backend/logs/server.log` (create `logs/` dir if missing) — this is what the
  "logs" tab tails. Regular server startup/reload output still goes to stdout (what
  the "backend" tab shows), so the two tabs show genuinely different things.
- `GET /hello?text=...` (or `POST /hello { text }`) -> `{ message: "Hello " + text }`.

### `frontend/src/App.jsx`
- `useState` for input text and response message.
- Textfield + button; on click, `fetch` to `http://localhost:3001/hello?text=...`,
  render `Hello <text>` from the response.
- Keep it to one component, no routing/state library.

### `start-herdr-tabs.sh`
Wraps the herdr CLI calls so the 3-tab layout is reproducible with one command:
1. `herdr workspace create --cwd <herdr_demo abs path> --label hello-demo --focus`
   — capture the workspace id from its output.
2. `herdr tab create --workspace <id> --cwd <herdr_demo>/frontend --label frontend --no-focus`
   → `herdr pane list --workspace <id>` to find the new pane id →
   `herdr pane run <pane_id> "npm run dev"`.
3. Same pattern for `--label backend --cwd .../backend`, running `npm run dev`
   (add a `dev` script using `node --watch server.js`, or plain `node server.js`).
4. Same pattern for `--label logs --cwd .../backend --focus`, running
   `tail -f logs/server.log`.

Note for implementation: confirm the actual output format of `herdr workspace create`
/ `herdr tab create` (likely JSON over the socket API) while writing the script, and
parse ids accordingly (e.g. with `jq`). Adjust the id-capture step if the format
differs from assumption.

## Verification
1. `cd backend && npm install && node server.js`, then `curl "localhost:3001/hello?text=World"`
   → expect `{"message":"Hello World"}` and a new line appended to `backend/logs/server.log`.
2. `cd frontend && npm install && npm run dev`, open the browser, type text, click the
   button, confirm `Hello <text>` renders and the network call succeeds (check devtools).
3. Stop the manual runs, then run `./start-herdr-tabs.sh` from `herdr_demo/` and confirm:
   - 3 tabs appear, labeled `frontend`, `backend`, `logs`.
   - frontend tab shows Vite dev server output and the app is reachable in a browser.
   - backend tab shows Express/node startup output.
   - logs tab shows live-appended request lines as you use the UI.

# Herdr Quick Guide

Herdr is a terminal multiplexer for workspaces, tabs, panes, and coding agents.
The commands below target the current stable CLI and work on Linux/macOS.

## Install and start

```bash
curl -fsSL https://herdr.dev/install.sh | sh
herdr --version
herdr
```

Use Homebrew instead when preferred:

```bash
brew install herdr
```

Herdr launches or attaches to the default session. Start it from a project
directory so the initial workspace opens in the right location.

## Install integrations

Integrations provide native session restore and/or more accurate agent state.
Install only the agents you use:

```bash
herdr integration install claude
herdr integration install codex
herdr integration install copilot
herdr integration install pi
herdr integration install opencode
herdr integration status
```

List supported integration commands with:

```bash
herdr integration
```

To remove one:

```bash
herdr integration uninstall claude
```

## Install the agent skill

The skill teaches a coding agent how to inspect and control Herdr from inside
a managed pane:

```bash
npx skills add herdrdev/herdr --skill herdr -g
```

The bundled, release-matched copy is also available with:

```bash
herdr --skill
```

The agent must be started inside Herdr. The skill intentionally does nothing
outside a Herdr pane (`HERDR_ENV=1`).

## Keyboard shortcuts

- Start with `prefix+?`, this will give you the complete list of shortcuts.
- Inspect the name of your session using `echo $HERDR_SESSION`

The default prefix is `Ctrl-b`: press it, release it, then press the action
key. Mouse controls are supported too.

| Action | Shortcut |
| --- | --- |
| New tab | `prefix+c` |
| Split right | `prefix+v` |
| Split down | `prefix+-` |
| Move between panes | `prefix+h/j/k/l` |
| Next/previous tab | `prefix+n` / `prefix+p` |
| Navigate workspaces | `prefix+w` |
| New workspace | `prefix+Shift+n` |
| Zoom pane | `prefix+z` |
| Copy mode | `prefix+[` |
| Show all shortcuts | `prefix+?` |
| Detach and leave work running | `prefix+q` |

## Daily CLI commands

Most commands return JSON, so capture IDs from the response instead of
guessing them.

```bash
# Inspect the current session
herdr status
herdr workspace list
herdr pane list --workspace w1
herdr agent list

# Create a workspace without moving focus
herdr workspace create --cwd ~/project --label project --no-focus

# Create a pane, then run a command in it
herdr pane split --current --direction right --cwd "$PWD" --no-focus
herdr pane run <pane-id> "npm test"
herdr pane wait-output <pane-id> --match "passed" --timeout 120000
herdr pane read <pane-id> --source recent-unwrapped --lines 120

# Attach to a named session or return to the default session
herdr --session work
herdr session list
herdr session attach work
```

Use `--current` for the calling pane. An omitted target can refer to the
pane focused in the Herdr UI instead.

## Run and coordinate agents

Create the pane first; `agent start` requires an available shell pane.

```bash
herdr pane split --current --direction right --cwd "$PWD" --no-focus
herdr agent start reviewer --kind claude --pane <pane-id>
herdr agent prompt reviewer "Review the current diff and report actionable findings." --wait --timeout 120000
herdr agent read reviewer --source recent-unwrapped --lines 120
herdr agent wait reviewer
```

Supported agent kinds vary by release. Inspect the installed list with:

```bash
herdr agent
```

Agent states mean `working`, `idle`, `done`, `blocked`, or `unknown`.
`unknown` means Herdr cannot classify the screen; it does not mean the work
succeeded.

## Detach, restore, and stop

Detach with `prefix+q` or close the terminal. Agents and the server continue
running; launch `herdr` again to reattach.

Stop panes and end the default session only when intended:

```bash
herdr server stop
```

For a named session:

```bash
herdr session stop work
```

## Troubleshooting

```bash
herdr status server
herdr status client
herdr integration status
herdr agent list
herdr pane read <pane-id> --source detection --lines 50
herdr agent explain <agent-or-pane-id>
```

If an agent is not detected, check that it is running inside Herdr and that
its integration was installed for the same user account. After upgrading,
restart or reattach the session if the client/server versions differ.

## References

- Install: https://herdr.dev/docs/install/
- Quick start: https://herdr.dev/docs/quick-start/
- Keyboard: https://herdr.dev/docs/keyboard/
- CLI reference: https://herdr.dev/docs/cli-reference/
- Integrations: https://herdr.dev/docs/integrations/
- Agent skill: https://herdr.dev/docs/agent-skill/
# tmux + Claude Code: Parallel Python Bug Lab

Three independent bugs in three claude worktrees, visible in one tmux session.

## Setup: Create repo + bugs

```bash
mkdir -p ~/~/github_projs//python-bug-lab
cd ~/~/github_projs//python-bug-lab
git init -b main

cat > buglab.py <<'PY'
from __future__ import annotations


def calculate_total(prices: list[float], tax_rate: float) -> float:
    """Return the tax-inclusive total."""
    subtotal = sum(prices)
    return round(subtotal + subtotal * tax_rate / 100, 2)


def normalize_username(username: str) -> str:
    """Normalize a username for lookup."""
    return username.strip().lower()


def chunked(items: list[object], size: int) -> list[list[object]]:
    """Split items into lists of at most size elements."""
    if size <= 0:
        raise ValueError("size must be positive")
    return [items[i : i + size] for i in range(0, len(items) - size, size)]
PY

cat > test_buglab.py <<'PY'
import pytest

from buglab import calculate_total, chunked, normalize_username


def test_calculate_total():
    assert calculate_total([100.0, 50.0], 18) == 177.0


def test_normalize_username():
    assert normalize_username("  Alice.Example  ") == "alice.example"


def test_chunked():
    assert chunked([1, 2, 3, 4, 5], 2) == [[1, 2], [3, 4], [5]]


def test_chunked_rejects_non_positive_size():
    with pytest.raises(ValueError):
        chunked([1, 2], 0)
PY

cat > README.md <<'MD'
# Python Bug Lab
Three independent bugs fixed in parallel.
MD

python3 -m venv .venv
. .venv/bin/activate
python -m pip install pytest
pytest -q

git add .
git commit -m 'Create Python bug lab'
```

Tests will fail on `chunked`. The other two bugs are latent (tests will be written by claude).

## Launch: tmux + three claude worktrees

Create launcher script:

```bash
cat > start-lab.sh <<'SH'
#!/usr/bin/env bash
set -euo pipefail

SESSION="bug-lab"
ROOT="$(pwd)"

# Kill any existing session to start fresh
tmux kill-session -t "$SESSION" 2>/dev/null || true

# Create session with first window (fix-total)
tmux new-session -d -s "$SESSION" -x 200 -y 50 \
  -c "$ROOT" \
  -n fix-total

# Add three more windows: fix-username, fix-chunking, control
tmux new-window -t "$SESSION" -n fix-username -c "$ROOT"
tmux new-window -t "$SESSION" -n fix-chunking -c "$ROOT"
tmux new-window -t "$SESSION" -n control -c "$ROOT"

# Launch claude in each bug-fix window
# Window 0: fix-total
tmux send-keys -t "$SESSION:fix-total" \
  "claude --worktree fix-total -- 'Fix only calculate_total. Inspect buglab.py, write focused tests, run pytest. Do not edit normalize_username or chunked. Show diff.'" \
  Enter

# Window 1: fix-username
tmux send-keys -t "$SESSION:fix-username" \
  "claude --worktree fix-username -- 'Fix only normalize_username. Inspect buglab.py, write tests to reveal bug, run pytest. Do not edit calculate_total or chunked. Show diff.'" \
  Enter

# Window 2: fix-chunking
tmux send-keys -t "$SESSION:fix-chunking" \
  "claude --worktree fix-chunking -- 'Fix only chunked. Inspect buglab.py, write tests for boundaries and partial chunks, run pytest. Do not edit calculate_total or normalize_username. Show diff.'" \
  Enter

# Window 3: control (manual terminal)
tmux send-keys -t "$SESSION:control" "echo 'Control window. Use: tmux list-windows -t $SESSION; git worktree list'" Enter

# Attach to session, start at window 0
tmux select-window -t "$SESSION:fix-total"
exec tmux attach -t "$SESSION"
SH

chmod +x start-lab.sh
./start-lab.sh
```

**Navigation:**
- `Ctrl-b 0` → fix-total window
- `Ctrl-b 1` → fix-username window  
- `Ctrl-b 2` → fix-chunking window
- `Ctrl-b 3` → control/bash window
- `Ctrl-b d` → detach

## Monitor progress (from control window or new terminal)

List worktrees:
```bash
git worktree list
```

Check one worktree status:
```bash
git -C .claude/worktrees/fix-total status
git -C .claude/worktrees/fix-username status
git -C .claude/worktrees/fix-chunking status
```

Capture last 30 lines of each window without switching:
```bash
tmux capture-pane -t "bug-lab:fix-total" -p -S -30
tmux capture-pane -t "bug-lab:fix-username" -p -S -30
tmux capture-pane -t "bug-lab:fix-chunking" -p -S -30
```

View the session:
```bash
tmux attach -t bug-lab
```

## Review: diffs and test results

After claude finishes all three sessions, inspect each worktree:

```bash
echo "=== fix-total ==="
git -C .claude/worktrees/fix-total diff

echo "=== fix-username ==="
git -C .claude/worktrees/fix-username diff

echo "=== fix-chunking ==="
git -C .claude/worktrees/fix-chunking diff
```

Test each independently:
```bash
for wt in fix-total fix-username fix-chunking; do
  echo "=== Testing $wt ==="
  (cd ".claude/worktrees/$wt" && . .venv/bin/activate && pytest -v)
done
```

## Merge to main

First, check what branches were created:
```bash
git branch --all
```

Then merge each to main:
```bash
git switch main

# Use the actual branch names from 'git branch'. Typically they match worktree names:
git merge --no-ff worktree-fix-total -m 'Fix calculate_total'
git merge --no-ff worktree-fix-username -m 'Fix normalize_username'
git merge --no-ff worktree-fix-chunking -m 'Fix chunked'

# Verify all tests pass on main
pytest -v
```

## Cleanup

```bash
git worktree remove .claude/worktrees/fix-total
git worktree remove .claude/worktrees/fix-username
git worktree remove .claude/worktrees/fix-chunking

tmux kill-session -t bug-lab
```

## How it works

- `--worktree fix-name` creates isolated git checkout + branch per bug
- `tmux new-window` creates visual containers; each runs one claude session
- Each claude session modifies only its own worktree (isolated files + git state)
- tmux windows let you watch all three in parallel
- You manually review diffs and merge successful fixes back to main

No race conditions (worktrees are independent). No merge conflicts (different functions). Three bugs fixed in parallel, visible in one screen.

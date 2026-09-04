**Tmux prefix:** `ctrl+a`  
Press prefix first, then the command key.

**Sessions**
```bash
tmux                    # start a session
tmux new -s name        # create named session
tmux ls                 # list sessions
tmux attach -t name     # attach to session
tmux kill-session -t name
tmux rename-session -t old new
```

**Windows**
```text
prefix c       Create window
prefix n       Next window
prefix p       Previous window
prefix 0..9    Select window
prefix ,       Rename window
prefix &       Close window
```

**Panes**
```text
prefix %       Split vertically
prefix "       Split horizontally
prefix arrow   Move between panes
prefix o       Cycle panes
prefix x       Close pane
prefix z       Zoom/unzoom pane
prefix q       Show pane numbers
prefix {       Move pane left/up
prefix }       Move pane right/down
```

**Detach and navigation**
```text
prefix d       Detach from session
prefix s       Choose a session
prefix w       Choose a window interactively
prefix l       Switch to last window
prefix t       Show clock
```

**Useful commands**
```bash
tmux new -s work
tmux attach -t work
tmux attach                    # attach to most recent session
tmux kill-server               # terminate all tmux sessions
tmux capture-pane -p           # print pane contents
tmux source-file ~/.tmux.conf  # reload configuration
```

**Copy mode**
```text
prefix [       Enter copy mode
Space          Start selection
Enter          Copy selection
q              Exit copy mode
prefix ]       Paste
```

**Common configuration**
```tmux
set -g mouse on
set -g history-limit 10000
set -g default-terminal "screen-256color"
```

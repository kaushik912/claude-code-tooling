# Claude Cron Debugging

To check whether a Claude process is alive:

```bash
pgrep -af claude
ps aux | grep claude
```

Prefer having the Python script capture the subprocess PID and check whether the process still exists:

```python
from pathlib import Path

Path(f"/proc/{pid}").exists()
```

Alternatively, use `psutil`:

```python
psutil.Process(pid).is_running()
```

If Claude spawns child processes, inspect them with:

```bash
pgrep -P <parent_pid>
ps --forest -p <pid>
```

For cron jobs, also check the complete job process rather than only Claude:

```bash
pgrep -af "cron_script.py"
```

This confirms whether the script and its Claude subprocess are still running.
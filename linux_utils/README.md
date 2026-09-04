# linux_utils

Personal shell/Python utility scripts for managing local git repos, Maven builds, and misc dev tasks.

## Scripts

| Script | Purpose |
|---|---|
| `init_local_repo.sh /path` | `git init` a directory, set branch `main`, ensure user.name/email set. |
| `show_git_push_steps.sh <project-name>` | Print the `git remote add` + `push -u origin main` commands for a new GitHub repo (`kaushik912/<project>`). |
| `check_ahead_of_origin.sh [workspace]` | List repos under a workspace dir with unpushed commits on current branch. |
| `list_missing_remotes.sh [workspace]` | List repos under a workspace dir with no `origin` remote configured. |
| `create_repo_gitignores.py [--root DIR] [--stacks java,python,node] [--dry-run] [--force]` | Interactively (or via flags) assemble a `.gitignore` from `gitignore_sections/*.txt` (common + chosen stacks). |
| `build_maven_projects.sh [root]` | Find all `pom.xml` under root, run `mvn -q -DskipTests compile` on each, report pass/fail summary. |
| `clear_log_files.sh [--dry-run] DIRECTORY` | Recursively delete `*.log` files under DIRECTORY; `--dry-run` lists without deleting. |
| `utc_ist_convert.sh [ist HH:MM \| utc HH:MM]` | Convert between UTC and IST (no args = current time); also prints cron minute/hour fields for `ist` mode. |

## Layout

- `gitignore_sections/` — reusable `.gitignore` snippets (`common`, `java`, `python`, `node`) consumed by `create_repo_gitignores.py`.

## Conventions

- Bash scripts use `set -euo pipefail` (or `-uo pipefail` where partial failure is expected) and print `Usage:` on bad args.
- Scripts are standalone, no shared config; run directly (`./script.sh`) or via `python3`.

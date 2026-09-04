#!/usr/bin/env python3
"""Interactively create a .gitignore for a single repo directory.

Prompts for which stack(s) apply (Java / Python / Node / Global) and
assembles the .gitignore from section files in gitignore_sections/.
A "common" section (editor/OS noise, env files) is always included.

Usage:
  python3 linux_utils/create_repo_gitignores.py                # target = cwd, interactive
  python3 linux_utils/create_repo_gitignores.py --root /path
  python3 linux_utils/create_repo_gitignores.py --stacks java,node   # skip prompt
  python3 linux_utils/create_repo_gitignores.py --dry-run
  python3 linux_utils/create_repo_gitignores.py --force
"""

from __future__ import annotations

import argparse
from pathlib import Path

SECTIONS_DIR = Path(__file__).resolve().parent / "gitignore_sections"
STACK_ORDER = ["java", "python", "node"]
STACK_LABELS = {"java": "Java", "python": "Python", "node": "Node"}


def load_section(name: str) -> str:
    return (SECTIONS_DIR / f"{name}_gitignore.txt").read_text(encoding="utf-8")


def prompt_stacks() -> list[str]:
    print("Which stack(s) apply to this repo?")
    print("  1) Java")
    print("  2) Python")
    print("  3) Node")
    print("  4) Global (all-inclusive)")
    raw = input("Enter choice(s), comma-separated (e.g. 1,3): ").strip()

    choice_map = {"1": "java", "2": "python", "3": "node", "4": "global"}
    picked = {choice_map.get(c.strip(), c.strip().lower()) for c in raw.split(",") if c.strip()}

    if "global" in picked or not picked:
        return list(STACK_ORDER)

    return [s for s in STACK_ORDER if s in picked]


def build_gitignore(stacks: list[str]) -> str:
    parts = [load_section("common")]
    for stack in STACK_ORDER:
        if stack in stacks:
            parts.append(load_section(stack))
    return "\n".join(parts)


def main() -> int:
    parser = argparse.ArgumentParser(description="Create a .gitignore for a repo directory.")
    parser.add_argument("--root", type=Path, default=Path.cwd(), help="Target repo directory")
    parser.add_argument("--stacks", help="Comma-separated stacks (java,python,node,global) to skip the prompt")
    parser.add_argument("--dry-run", action="store_true", help="Print the result without writing")
    parser.add_argument("--force", action="store_true", help="Overwrite an existing .gitignore")
    args = parser.parse_args()

    root = args.root.resolve()
    if not root.is_dir():
        raise SystemExit(f"Not a directory: {root}")

    if args.stacks:
        requested = {s.strip().lower() for s in args.stacks.split(",") if s.strip()}
        stacks = list(STACK_ORDER) if "global" in requested else [s for s in STACK_ORDER if s in requested]
    else:
        stacks = prompt_stacks()

    print(f"Including: common, {', '.join(STACK_LABELS[s] for s in stacks) or '(none)'}")

    gitignore_path = root / ".gitignore"
    if gitignore_path.exists() and not args.force:
        raise SystemExit(f"{gitignore_path} already exists. Use --force to overwrite.")

    content = build_gitignore(stacks)

    if args.dry_run:
        print(f"--- Would write {gitignore_path} ---")
        print(content)
    else:
        gitignore_path.write_text(content, encoding="utf-8")
        print(f"Created {gitignore_path}")

    return 0


if __name__ == "__main__":
    raise SystemExit(main())

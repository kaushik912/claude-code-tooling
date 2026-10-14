#!/usr/bin/env python3
"""Audit Maven projects for required files (README.md, CLAUDE.md).

Walks ROOT (default: cwd) recursively. Every dir holding a MARKERS file is one
project, so multi-module poms give one row per module. Prints a table and a
`Passed: x/y` footer; exit 1 if any project is missing a required file.

Extend by editing MARKERS (what makes a project), REQUIRED (what it must have)
or SKIP_DIRS, or per run with --require / --marker / --skip.
"""
import argparse
import os
import sys
from pathlib import Path

MARKERS = ["pom.xml"]
REQUIRED = ["README.md", "CLAUDE.md"]
SKIP_DIRS = {
    ".git", "node_modules", "target", ".venv", "src",
    "enterprise-repos", "enterprise_repos", "reference_repos",  # not owned
}


def find_projects(root, markers=MARKERS, skip=SKIP_DIRS):
    """Yield dirs under root (inclusive) that contain any marker file."""
    for dirpath, dirnames, filenames in os.walk(root):
        dirnames[:] = sorted(d for d in dirnames if d not in skip)
        if any(m in filenames for m in markers):
            yield Path(dirpath)


def audit(project, required=REQUIRED):
    """{file: present?} for one project dir."""
    return {name: (project / name).is_file() for name in required}


def render(rows, required):
    """rows: [(label, {file: bool})] -> table text with an x/y footer."""
    width = max([len("PROJECT")] + [len(label) for label, _ in rows])
    head = "  ".join([f"{'PROJECT':<{width}}"] + [f"{n:<{len(n)}}" for n in required])
    lines = [head, "-" * len(head)]
    for label, found in rows:
        marks = [f"{'✓' if found[n] else '✗':<{len(n)}}" for n in required]
        lines.append("  ".join([f"{label:<{width}}"] + marks))
    passed = sum(all(found.values()) for _, found in rows)
    per_file = "  ".join(f"{n} {sum(found[n] for _, found in rows)}/{len(rows)}" for n in required)
    lines += ["", per_file, f"Passed: {passed}/{len(rows)}"]
    return "\n".join(lines)


def main(argv=None):
    p = argparse.ArgumentParser(description=__doc__.splitlines()[0])
    p.add_argument("root", nargs="?", default=".", help="dir to scan (default: cwd)")
    p.add_argument("--require", nargs="+", default=REQUIRED, metavar="FILE", help=f"required files (default: {' '.join(REQUIRED)})")
    p.add_argument("--marker", nargs="+", default=MARKERS, metavar="FILE", help=f"project marker files (default: {' '.join(MARKERS)})")
    p.add_argument("--skip", nargs="+", default=[], metavar="DIR", help=f"extra dir names to skip (always skipped: {' '.join(sorted(SKIP_DIRS))})")
    args = p.parse_args(argv)

    root = Path(args.root).resolve()
    skip = SKIP_DIRS | set(args.skip)
    rows = []
    for proj in find_projects(root, args.marker, skip):
        label = proj.name if proj == root else str(proj.relative_to(root))
        rows.append((label, audit(proj, args.require)))
    if not rows:
        print(f"No projects (markers: {' '.join(args.marker)}) under {root}")
        return 0
    print(render(rows, args.require))
    return 0 if all(all(f.values()) for _, f in rows) else 1


if __name__ == "__main__":
    sys.exit(main())

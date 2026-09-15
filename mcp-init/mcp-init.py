#!/usr/bin/env python3
"""Interactive project-level .mcp.json creator. Run with cwd = target project."""
import json
import os
import subprocess

REGISTRY = os.environ.get("MCP_INIT_REGISTRY") or os.path.join(
    os.path.dirname(os.path.abspath(__file__)), "mcp-registry.json"
)


def load():
    with open(REGISTRY) as f:
        return json.load(f)


def save(reg):
    with open(REGISTRY, "w") as f:
        json.dump(reg, f, indent=2)
        f.write("\n")


def run(cmd):
    print(f"+ {cmd}")
    subprocess.run(cmd, shell=True)
    if "<" in cmd and ">" in cmd:
        print("NOTE: placeholder(s) like <...> in that command — edit .mcp.json to fill them in.")


def add_new(reg):
    pasted = input('Paste your full command (e.g. "claude mcp add -s project foo -- npx -y foo-mcp"):\n').strip()
    if not pasted:
        print("Nothing pasted, skipping.")
        return
    name = input("Short name to save this under: ").strip()
    if not name:
        print("No name given, skipping save — running once without saving.")
        run(pasted)
        return
    reg[name] = pasted
    save(reg)
    print(f"Saved '{name}' to {REGISTRY}")
    run(pasted)


def main():
    while True:
        reg = load()
        keys = list(reg.keys())
        print(f"\nMCPs available (from {REGISTRY}):")
        for i, k in enumerate(keys, 1):
            print(f"  {i:2}) {k}")
        print("   N) Add a new one (paste a claude mcp add command)")
        print("   Q) Quit")

        choice = input("Pick one: ").strip()
        if choice.lower() == "q":
            break
        elif choice.lower() == "n":
            add_new(reg)
        elif choice.isdigit() and 1 <= int(choice) <= len(keys):
            run(reg[keys[int(choice) - 1]])
        else:
            print("Not a valid choice.")

        if input("Add another? [y/N]: ").strip().lower() != "y":
            break


if __name__ == "__main__":
    main()

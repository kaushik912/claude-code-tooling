# CLI skill vs MCP for MySQL — cost notes (not a rule)

Benchmarked `mysql-query` CLI skill (dbq.sh) vs `@benborla29/mcp-server-mysql` MCP,
4 queries (2 simple, 2 join/aggregation) against dummy `bench_*` tables in
`calendar_db`. Raw runs: `cli-mysql-bench-v{1,2,3}.md` here, `mcp-mysql-bench-v{1,2,3}.md`
under `topMcps/`. All 3 runs, both tools, returned identical correct results —
this is a cost/token comparison, not a correctness one.

## Results

| Run | Prompt style | CLI cost | MCP cost | CLI cache-read | MCP cache-read |
|---|---|---|---|---|---|
| v1 | literal SQL given | $0.2051 | $0.1396 | 452.6k | 233.2k |
| v2 | literal SQL given | $0.2016 | $0.1287 | 454.8k | 182.9k |
| v3 | plain English, model writes SQL | $0.1885 | $0.2264 | 395.8k | 594.0k |

## Takeaway

- **Literal SQL handed over → MCP cheaper** (~35% less). It just executes; no
  schema exploration needed.
- **Plain English, model has to figure out schema itself → CLI cheaper** (~20% less
  here, $0.0379 diff). MCP's cache-read blew up to 594k vs its own 183–233k baseline —
  looks like the MCP server re-injects schema/table context per tool call, so
  exploration cost compounds. The CLI skill pays for schema discovery once
  (`DESCRIBE`/`SHOW`) and it's cheap from cache after that.

This matters because plain-English-to-MCP (not raw SQL) is the normal usage pattern
here, e.g. asking MCP to poke at the DB while debugging.

## Security: read-only enforcement

MCP (`@benborla29/mcp-server-mysql`) defaults `ALLOW_INSERT_OPERATION` /
`_UPDATE_` / `_DELETE_` / `_DDL_OPERATION` all to `false` — write/DDL is off unless
explicitly enabled via env var, enforced inside the server binary behind a fixed
tool interface. `topMcps/.mcp.json` doesn't set any of these, so it's read-only.

CLI skill's guardrail is a single grep regex in `dbq.sh` (allow-list on the first
keyword + a check for a second statement after `;`). Two gaps:
- `SELECT ... INTO OUTFILE '/path'` starts with `SELECT` so it passes the allow-list,
  but writes a file to disk — not a DB write, but a write.
- Bigger one: the guardrail only holds if the agent actually calls `dbq.sh`. The
  session also has raw `Bash` and `db.cnf` sits there in plaintext — nothing stops
  a direct `mysql --defaults-extra-file=db.cnf -e "DROP TABLE ..."` that skips the
  wrapper entirely (e.g. if a prompt-injected instruction talks the agent into it).

So MCP's read-only boundary is structural (enforced server-side, no escape hatch
without separate credentials); the CLI skill's is a convention the agent has to
choose to follow, on top of a script that itself has one known hole. Confirms your
instinct — MCP is the safer default against write-style injection, even though the
cost benchmark above sometimes favors CLI.

**Fix for CLI**: the real hole is `db.cnf` using `root` (full privileges), not the
regex. Point `db.cnf` at a DB user with only `SELECT` granted (`GRANT SELECT ON
calendar_db.* TO 'readonly'@'localhost'`) and the bypass-the-wrapper attack dies at
the DB layer regardless of what SQL text reaches it — same structural guarantee MCP
gets, no app-level enforcement to trust. Worth doing whether or not the CLI skill
ends up being the cost-preferred path.

## Caveat

n=1 per condition on the plain-English case (v3) — one more literal-SQL sample (v1,
v2) agree with each other, but the plain-English flip is a single run. Treat as a
lead worth knowing, not a settled rule: **when a task calls for exploratory,
plain-English DB digging (e.g. mid-debug), consider reaching for the CLI skill over
MCP** — but re-run v3-style before leaning on the exact numbers.

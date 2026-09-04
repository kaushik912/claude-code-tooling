---
name: no-lint-config
description: Repo has no checkstyle.xml or PMD ruleset — "project checkstyle/PMD rule" checks have nothing to check against
metadata:
  type: project
---

No checkstyle or PMD config file exists anywhere in the repo (searched for `*checkstyle*`, `*pmd*`, no pom.xml plugin entries either) as of commit 93b5d53.

**Why:** The review instructions ask to check "violations of the project's checkstyle/PMD rules," but there is no formal rule set to violate yet. Silently skipping this section looks like an oversight; better to state the gap.

**How to apply:** Until a checkstyle/PMD config is added to `pom.xml` or the repo root, explicitly note in reviews that this check is a no-op due to missing config, and fall back to general Java style conventions (consistent indentation, no unused imports, final where sensible) instead of inventing project-specific rules. If a config is later added, update this memory with its location and key rules.

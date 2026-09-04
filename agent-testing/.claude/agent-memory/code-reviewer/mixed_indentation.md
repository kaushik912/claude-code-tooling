---
name: mixed-indentation
description: Spring Initializr-generated files use tab indentation, hand-written files use 4-space indentation — inconsistent within the repo
metadata:
  type: project
---

`AgentTestingApplication.java` and `AgentTestingApplicationTests.java` (both Spring Initializr defaults) are tab-indented. `HelloController.java` (hand-added) is 4-space indented. Confirmed via `grep -P "^\t"` at commit 93b5d53.

**Why:** No checkstyle config exists yet ([[no-lint-config]]) so nothing enforces one style, and the inconsistency will spread as more files are hand-written next to generated ones.

**How to apply:** Flag new files that don't match the indentation style of the file(s) they sit next to. Recommend picking one style (spaces is more common in hand-written Spring code) and applying it repo-wide once a checkstyle config is introduced, rather than fixing file-by-file.

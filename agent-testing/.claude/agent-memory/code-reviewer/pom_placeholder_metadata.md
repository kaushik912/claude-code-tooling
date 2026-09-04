---
name: pom-placeholder-metadata
description: pom.xml has empty Initializr placeholder tags (description, url, license, developer, scm) never filled in
metadata:
  type: project
---

`pom.xml` (commit 93b5d53) has empty self-closing tags left over from Spring Initializr: `<description/>`, `<url/>`, `<licenses><license/></licenses>`, `<developers><developer/></developers>`, and an empty `<scm>` block.

**Why:** Harmless at build time (build succeeds, verified via `mvnw dependency:tree`), but empty license/scm blocks are the kind of thing that trips up release tooling or `maven-release-plugin`/central-publishing validation later, and looks unfinished in a repo that already has a real README and CI-ready structure.

**How to apply:** Low-priority cleanup item — mention once, don't re-flag every review unless the user is preparing to publish an artifact or cut a release, at which point these become blocking (Maven Central requires non-empty license/scm/developers).

---
name: verify-before-flagging
description: Don't flag dependency version combos as broken from version numbers alone — actually resolve/boot/curl and confirm before reporting
metadata:
  type: feedback
---

On first read, `springdoc-openapi-starter-webmvc-ui:2.8.6` alongside `spring-boot-starter-parent:4.1.1` (Spring Framework 7.0.9) looked like a likely version-skew bug, since springdoc 2.x historically targeted Spring Framework 6 / Boot 3. Ran `mvnw dependency:tree` (resolved fine), `mvnw test` (passed), and `mvnw spring-boot:run` + curl against `/hello`, `/v3/api-docs`, `/swagger-ui.html`, `/actuator/health` — all returned expected 200/302, confirming the combo works at runtime.

**Why:** Reporting a plausible-but-unverified version incompatibility as a finding would have been a false positive. Actually running the build/app caught that it works, avoiding wasted user time chasing a non-issue.

**How to apply:** For any dependency-version concern in this repo (or similar small Maven/Boot projects), spend the ~1 minute to run `dependency:tree`, `test`, and a quick boot+curl smoke test before reporting it as a finding. Only report as a real issue if resolution fails, tests fail, or the app throws/500s.

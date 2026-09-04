---
name: feedback-jira-description-visibility
description: Always re-check a Jira issue's description directly before/while implementing, even if prior triage reported it empty
metadata:
  type: feedback
---

On SCRUM-7, the parent/orchestrating task said triage had confirmed the ticket had no description, so scope was agreed with the user out-of-band (basic actuator health only). Partway through, calling `getJiraIssue`/`transitionJiraIssue` on the same ticket returned a real description field containing concrete Gherkin AC (`GET /api/v1/ping` → "pong") that didn't match the agreed scope at all.

**Why:** Triage summaries or earlier tool calls can be stale, use a narrower `fields` list, or simply be wrong — the ticket may have been edited after triage ran. Silently trusting "no description" from upstream context led to building the wrong endpoint first.

**How to apply:** Before starting implementation on any Jira-sourced ticket, call `getJiraIssue` yourself with `fields: ["description", "summary", "status", "issuetype"]` (or `*all`) to confirm current state, even if the task prompt already asserts what's in it. If a mismatch is found mid-task, don't just silently redo work — implement what the real AC needs, and leave a Jira comment flagging the discrepancy explicitly so the user can reconcile deliberately-scoped work vs. actual ticket text.

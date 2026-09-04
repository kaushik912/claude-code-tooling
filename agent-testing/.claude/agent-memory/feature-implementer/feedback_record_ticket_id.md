---
name: feedback-record-ticket-id
description: Always record the Jira ticket ID (not just conventions learned) when finishing a ticket implementation
metadata:
  type: feedback
---

When a feature-implementation task for a Jira ticket wraps up, explicitly save/update a memory entry with the ticket ID, one-line summary, and completion date — don't let it get folded away into only "conventions learned" notes.

**Why:** After implementing SCRUM-6, the ticket ID itself wasn't saved anywhere in memory — only reusable code conventions were. This was self-identified during unprompted follow-up work, not something the user asked for; it should still be part of the default end-of-task checklist going forward.

**How to apply:** At the end of any ticket implementation in this repo, update [[project_agent_testing_conventions]] "Tickets implemented here" list (or equivalent) with the ticket ID/summary/date, and cross-link to [[reference_jira_scrum]] for where to fetch full details. Do this even if the user doesn't explicitly ask "did you save the ticket ID".

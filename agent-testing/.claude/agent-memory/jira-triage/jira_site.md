---
name: jira-site
description: Jira cloud site URL for this user's Atlassian MCP tickets (needed as cloudId param)
metadata:
  type: reference
---

User's Jira site: `https://kaushik2016.atlassian.net/` — pass `kaushik2016.atlassian.net` as the `cloudId` param to Atlassian MCP tools (getJiraIssue, searchJiraIssuesUsingJql). Cloud ID resolves to `61e40543-9327-41b7-96d3-4ccd7dd74102`.

Project key `SCRUM` (project name "myJIRA") is the default/only project on this site so far.

Guessing other hostnames (e.g. `<username>.atlassian.net` variants) either 404s or resolves to an ungranted cloud ID — don't waste turns guessing, ask the user directly if the site is unknown.

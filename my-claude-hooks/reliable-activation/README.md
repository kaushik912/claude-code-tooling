# Reliable Activation — Forced Eval Hook Demo

E2e example from forced-eval-llm.md.

## Files
- `.claude/skills/db-schema-guide/SKILL.md` — DB schema rules skill
- `.claude/skills/api-security-check/SKILL.md` — API security rules skill
- `.claude/hooks/forced-eval-hook.sh` — UserPromptSubmit hook, injects mandatory eval/activate/implement sequence
- `.claude/settings.json` — registers the hook
- `user-registration-service/` — worked example output: Spring Boot registration endpoint built under this hook (UUID PK, timestamps, BCrypt hashing, bean validation, rate limiting)

## Test
```
cd reliable-activation
claude
```
Prompt: `Write a Spring Boot REST endpoint to register a new user and store their record in PostgreSQL.`

Expect: YES/NO eval line per skill → `Skill(...)` tool calls → implementation using both skills' rules (UUID PK, timestamps, bean validation, rate limiting).

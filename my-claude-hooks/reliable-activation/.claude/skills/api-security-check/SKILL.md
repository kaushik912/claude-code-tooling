---
name: api-security-check
description: Use when writing API routes or authentication handlers — verifies JWT, enforces Bean Validation input checks, and checks rate limiting.
---

# API Security Skill

When activated:
- Verify JWT tokens in request headers (Spring Security filter).
- Enforce input validation using `jakarta.validation` annotations (`@Valid`, `@NotBlank`, `@Email`, etc.) on request DTOs.
- Implement rate limiting checks.

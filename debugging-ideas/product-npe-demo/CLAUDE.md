# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Purpose

This is **not a normal app to develop** — it's a deliberately-planted runtime bug used to practice AI-assisted debugging. Do not "fix" anything by inspection alone unless the user explicitly asks for a fix; the point of the exercise is to reproduce the failure at runtime and diagnose it from there.

See `README.md` for the scenario writeup and `../scenarios.md` for two other planted scenarios (Hibernate lazy-loading trap, cache staleness) that are documented but not yet implemented in this repo.

## Commands

```bash
./mvnw spring-boot:run       # run the app (port 8080)
./mvnw test                  # run all tests
./mvnw test -Dtest=ProductNpeDemoApplicationTests#contextLoads   # run a single test
./mvnw compile                # compile only
```

Swagger UI: http://localhost:8080/swagger-ui.html
OpenAPI spec: http://localhost:8080/v3/api-docs

## Architecture

Spring Boot 4 (Java 21) + H2 in-memory DB + Spring Data JPA. Single vertical slice, no layering beyond the standard Controller → Service → Repository:

- `ProductController` — one endpoint, `GET /products/{id}/category`, delegates straight to the service.
- `ProductService` — looks up the product via the repository and returns its category.
- `ProductRepository` — plain `JpaRepository<Product, Long>`, no custom queries.
- `Product` — JPA entity (`id`, `name`, `category`).
- `src/main/resources/data.sql` — seed data; `spring.jpa.defer-datasource-initialization=true` + `spring.sql.init.mode=always` in `application.properties` makes Hibernate create the schema first, then this SQL populates it.
- `spring.jpa.hibernate.ddl-auto=create-drop` — schema is rebuilt from entities on every run, so seed data is the only source of truth for what's in the DB.

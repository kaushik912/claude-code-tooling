# user-registration-service

User registration REST API demo (Spring Boot + Postgres).

## Stack
- Java 17, Spring Boot 4.1.0, Maven wrapper (`./mvnw`)
- webmvc, data-jpa, validation, PostgreSQL driver, Lombok, spring-security-crypto (hashing only, no full Security), springdoc-openapi 2.8.6
- Tests: JUnit 5, H2 in-memory (`src/test/resources/application.properties`)

## Commands
- Run: `./mvnw spring-boot:run` (needs Postgres up + env vars below; port 8080 default)
- Test: `./mvnw test` (H2, no Postgres needed)
- Package: `./mvnw clean package`

## Env vars (see `.env.example`)
- `DB_URL` (optional; defaults to localhost:5432/user_registration), `DB_USERNAME`, `DB_PASSWORD` (no defaults)
- Postgres required at runtime; Docker not required by repo (no compose file)

## Layout (`com.kaushik.userregistration`)
- `user/`: `UserController` (`POST /api/users/register`), `UserService`, `UserRepository`, `User`, `EmailAlreadyExistsException`, `user/dto/`
- `config/`: `SecurityBeansConfig`, `RateLimitFilter`
- `exception/`: `GlobalExceptionHandler`, `ApiError`

## Gotchas
- `RateLimitFilter`: in-memory, per client IP, 5 req/60s on `/api/users/register`, then 429; single-instance only, state persists across tests in same context
- `ddl-auto=update`, `open-in-view=false`
- Swagger UI at default `/swagger-ui.html`

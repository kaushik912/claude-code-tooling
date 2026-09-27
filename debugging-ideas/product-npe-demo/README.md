# product-npe-demo

A deliberately-planted runtime bug for practicing AI/Claude-assisted debugging —
not a bug to fix by reading the code, but a scenario to *diagnose at runtime*.

## The setup

Spring Boot 4 + H2 + JPA. One entity, `Product(id, name, category)`, seeded via
`data.sql`. One endpoint:

```
GET /products/{id}/category
```

...which fetches the product and returns `category.toUpperCase()`.

## The bug

`category` is a nullable DB column. Seed data has it populated for every
product **except one** (id `4`, "Gift Card"). The code looks completely fine
on inspection — there's no obvious null-check missing that jumps out, and
every other product works:

```
GET /products/1/category  -> 200 "ELECTRONICS"
GET /products/2/category  -> 200 "KITCHEN"
GET /products/3/category  -> 200 "FURNITURE"
GET /products/4/category  -> 500 NullPointerException
GET /products/5/category  -> 200 "STATIONERY"
```

The failure only surfaces when you hit the one record with a NULL category —
i.e. it's data-dependent, not visible from a static code read. That's the
point: the exercise is to reproduce it, trace it back to `ProductService`,
and diagnose *why* it's data-dependent, not just find the null-check-shaped
line.

## Running it

```bash
./mvnw spring-boot:run
```

Then:

```bash
curl http://localhost:8080/products/1/category   # 200 OK
curl http://localhost:8080/products/4/category   # 500 NullPointerException
```

Swagger UI: http://localhost:8080/swagger-ui.html

## Scenario 2: reviews count

A second entity, `Review`, was added with a relationship back to `Product`.
One more endpoint:

```
GET /products/{id}/reviews-count
```

...which fetches the product and returns how many reviews it has.

Some requests work fine, some don't:

```
GET /products/1/category         -> 200 "ELECTRONICS"
GET /products/1/reviews-count    -> 500 Internal Server Error
```

Nothing about the controller or service code looks obviously wrong on a
read-through. Reproduce it, look at the stack trace, and trace *why* this
particular call path fails while the rest of the app is fine.

## Other planted scenarios

See [`../scenarios.md`](../scenarios.md) for one more runtime-bug scenario
(cache staleness) — documented but not yet implemented.

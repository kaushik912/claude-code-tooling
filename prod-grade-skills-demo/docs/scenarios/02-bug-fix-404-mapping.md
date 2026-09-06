# Scenario 2 — Bug fix: unknown product id returns 500, not 404

**Bug** (real, checked-in, not hypothetical): `GET /api/products/{id}` with an id that doesn't
exist returns HTTP `500` with a stack trace, instead of `404`.

Reproduce it yourself right now:

```bash
./mvnw spring-boot:run &
curl -i http://localhost:8080/api/products/999999
# HTTP/1.1 500 ...
```

Root cause: `ProductService.getById` (`src/main/java/com/example/skillsdemo/product/ProductService.java:29`)
throws a bare `RuntimeException`, which Spring's default error handling maps to `500` because it
isn't a `ResponseStatusException` or annotated `@ResponseStatus`, and there's no
`@ControllerAdvice` in the app to translate it.

**Pipeline**: `debugging-and-error-recovery` (reproduce → isolate → root-cause → fix) →
`test-driven-development` (red-first regression test) → `/review` (`code-reviewer` persona).

---

## 1. `debugging-and-error-recovery` — reproduce, isolate, root-cause

The skill's process is deliberately not "just fix it":

1. **Reproduce**: the `curl` above, or a failing `@SpringBootTest` hitting the real endpoint.
2. **Isolate**: is this a controller problem, a service problem, or missing global error
   handling? Tracing the call — `ProductController.getProduct` (line 35) calls
   `productService.getById(id)` with no try/catch, and there's no `@ControllerAdvice` anywhere in
   `com.example.skillsdemo` — narrows it to "missing error-mapping layer", not a logic bug in
   `getById` itself (the lookup logic is correct; only the *failure signal* is wrong).
3. **Root-cause, not symptom-patch**: the tempting shortcut is
   `catch (RuntimeException e) { return ResponseEntity.notFound().build(); }` inside the
   controller — that's a symptom patch that would also swallow *unrelated* runtime exceptions
   (e.g. a real NPE bug) and report them as 404s, hiding real failures. The skill's "root cause"
   step should reject that in favor of a typed exception + global mapping.

## 2. The fix

A typed exception, thrown from the service (same file, same line as today's bare throw):

```java
// src/main/java/com/example/skillsdemo/product/ProductNotFoundException.java
public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long id) {
        super("Product not found: " + id);
    }
}
```

```java
// ProductService.getById — replaces the seeded bug
public Product getById(Long id) {
    return productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));
}
```

And one global handler (the same seam scenario 1's plan flagged reusing for
`InvalidPromoCodeException`):

```java
// src/main/java/com/example/skillsdemo/GlobalExceptionHandler.java
@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    ResponseEntity<Map<String, String>> handleNotFound(ProductNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
    }
}
```

## 3. Regression test — red first, per `testing-style.md`

```java
@Test
void givenUnknownId_whenGetProduct_thenReturns404() {
    // Given
    long unknownId = 999_999L;

    // When / Then
    mockMvc.perform(get("/api/products/{id}", unknownId))
        .andExpect(status().isNotFound());
}
```

Run it against the unfixed code first (confirm it fails with `500` expected `404`), then apply
the fix above and confirm green — this is the exact gap flagged in
`src/test/java/com/example/skillsdemo/product/ProductServiceTests.java`'s trailing comment.

## 4. `/review` — confirm the fix, not just the symptom

`/review` dispatches the `code-reviewer` persona (5-axis: correctness, readability,
architecture, security, performance) against the diff: the new `ProductNotFoundException`, the
`GlobalExceptionHandler`, and the regression test. On the "correctness" axis it should confirm
the handler is scoped to `ProductNotFoundException` specifically — not a broad `RuntimeException`
catch that would mask unrelated bugs as 404s, which is exactly the shortcut step 1 rejected. On
"architecture" it should note `GlobalExceptionHandler` is a reusable seam other exception types
(e.g. an `OrderNotFoundException` from `OrderService.getById`, which has the same unmapped-500
issue today) can be added to later, rather than each controller growing its own local handler.

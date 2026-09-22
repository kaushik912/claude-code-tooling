# Implementation Plan: Promo Code Discounts at Checkout

Source spec: `SPEC.md`.

## Overview
Add an optional `promoCode` to `POST /api/orders`. A hardcoded table of active codes discounts
`totalAmountCents` by a fixed percentage. An unknown/inactive code rejects the order with `400`
before any side effect (stock reservation, order persistence) happens. Three new files, three
edited files, no schema/dependency changes.

## Dependency Graph
```
InvalidPromoCodeException (new, no deps)
        │
        ▼
PromoCodeService (new) ──────────────┐
        │                            │
        ▼                            ▼
OrderService.placeOrder (edit)   GlobalExceptionHandler (new)
        │                            │
        ▼                            │
PlaceOrderRequest (edit)              │
        │                            │
        ▼                            │
OrderController (edit) ◄──────────────┘
```
`GlobalExceptionHandler` only depends on the exception type existing, so it can be built
alongside `OrderService`'s edit, but the web-layer test (Task 3) needs both wired together to
pass — so Task 3 is the integration point, not Task 2.

## Architecture Decisions
- **Hardcoded `Map<String, Integer>` in `PromoCodeService`**, not DB/config — per `SPEC.md`,
  moving to persistence is an explicit follow-up, not this task's scope.
- **`InvalidPromoCodeException` is unchecked** (extends `RuntimeException`), consistent with the
  existing `IllegalStateException` in `Product.reserveStock` and the (buggy) `RuntimeException`
  pattern in `ProductService.getById` — this repo doesn't use checked exceptions for domain
  errors.
- **New `GlobalExceptionHandler` (`@RestControllerAdvice`)**, not a `try/catch` in
  `OrderController`. No handler exists yet (verified: `grep -r "ExceptionHandler" src/main` →
  none). Scoped to `InvalidPromoCodeException` only — do **not** touch `ProductService.getById`'s
  404 bug (that's `docs/scenarios/02-bug-fix-404-mapping.md`, a separate task).
- **Promo validation runs before `product.reserveStock(...)`** inside `OrderService.placeOrder`
  — this ordering is the mechanism behind the spec's "no partial side effects" requirement, not
  a style choice. Tested explicitly in Task 2 via `verify(..., never())`.
- **`PlaceOrderRequest.promoCode` is a plain nullable `String`**, no `@NotNull`/`@NotBlank` —
  omitting it must behave exactly as today per spec.

## Task List

### Phase 1: Foundation
- [ ] Task 1: `PromoCodeService` + `InvalidPromoCodeException`
- [ ] Task 2: Wire promo into `OrderService.placeOrder`

### Checkpoint: Foundation
- [ ] `./mvnw test` passes (Tasks 1–2 tests included)
- [ ] `./mvnw compile` succeeds
- [ ] Promo logic is fully covered at the service layer, with zero HTTP/controller changes yet

### Phase 2: API Surface
- [ ] Task 3: Expose `promoCode` on the API (`PlaceOrderRequest`, `OrderController`,
      `GlobalExceptionHandler`)

### Checkpoint: Complete
- [ ] `./mvnw test` passes (full suite, including new web-layer test)
- [ ] Manual check: `./mvnw spring-boot:run`, then `POST /api/orders` with `SAVE10` → discounted
      total; with `NOPE` → `400` + `{"error": "invalid promo code"}`; with no `promoCode` →
      unchanged from current behavior
- [ ] Swagger UI (`/swagger-ui.html`) reflects the new optional `promoCode` field
- [ ] All `SPEC.md` success criteria met
- [ ] Ready for `/constraints` and `/review`

## Risks and Mitigations
| Risk | Impact | Mitigation |
|------|--------|------------|
| `GlobalExceptionHandler` accidentally also catches/masks `ProductService.getById`'s existing `RuntimeException`, silently "fixing" scenario 2's bug as a side effect | Med | Scope the handler's `@ExceptionHandler` to `InvalidPromoCodeException` specifically, not a broad `Exception`/`RuntimeException` catch-all |
| Integer division in discount math rounds unexpectedly (e.g. odd cents) | Low | Spec pins "rounded down" — assert exact expected values in tests, e.g. `10_000 * 10% = 9_000` (already integer-clean); add one odd-cents case (e.g. `properties: 9_999` with `SAVE10`) to confirm floor behavior |
| `quantity` × `priceCents` overflow on `long` for pathological inputs | Low | Out of scope — pre-existing behavior, unrelated to this feature |

## Open Questions
None outstanding — resolved during `/spec`.

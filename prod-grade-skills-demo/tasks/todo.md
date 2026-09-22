# Tasks: Promo Code Discounts at Checkout

Plan: `tasks/plan.md`. Spec: `SPEC.md`.

## Phase 1: Foundation

### Task 1: `PromoCodeService` + `InvalidPromoCodeException`
- [x] Done

**Description:** New `InvalidPromoCodeException` (unchecked, carries the rejected code) and new
`PromoCodeService` with a hardcoded `Map<String, Integer>` of active codes (`SAVE10`→10,
`SAVE20`→20) and an `apply(String code, long amountCents) -> long` method that discounts by
percentage (integer division, rounded down) or throws on unknown code.

**Acceptance criteria:**
- [x] `apply("SAVE10", 10_000)` returns `9_000`
- [x] `apply("NOPE", 10_000)` throws `InvalidPromoCodeException`
- [x] `InvalidPromoCodeException` message includes the rejected code

**Verification:**
- [x] Tests pass: `./mvnw test -Dtest=PromoCodeServiceTests`
- [x] Build succeeds: `./mvnw compile`

**Dependencies:** None

**Files:**
- `src/main/java/com/example/skillsdemo/order/PromoCodeService.java` (new)
- `src/main/java/com/example/skillsdemo/order/InvalidPromoCodeException.java` (new)
- `src/test/java/com/example/skillsdemo/order/PromoCodeServiceTests.java` (new)

**Scope:** XS (2 new source files, 1 test file)

---

### Task 2: Wire promo into `OrderService.placeOrder`
- [x] Done

**Description:** `OrderService.placeOrder` gains a `promoCode` parameter. When non-null, calls
`PromoCodeService.apply` on the base total **before** `product.reserveStock(...)`; when null,
behavior is unchanged. On invalid code, the exception propagates and neither `reserveStock` nor
`orderRepository.save` runs.

**Acceptance criteria:**
- [x] `placeOrder(id, qty, "SAVE10")` returns an `Order` with discounted `totalAmountCents`
- [x] `placeOrder(id, qty, "NOPE")` throws `InvalidPromoCodeException`; `reserveStock` and
      `orderRepository.save` are never invoked (Mockito `verify(..., never())`)
- [x] `placeOrder(id, qty, null)` produces byte-identical output to the current (pre-change)
      behavior

**Verification:**
- [x] Tests pass: `./mvnw test -Dtest=OrderServiceTests`
- [x] Build succeeds: `./mvnw compile`

**Dependencies:** Task 1

**Files:**
- `src/main/java/com/example/skillsdemo/order/OrderService.java` (edit)
- `src/test/java/com/example/skillsdemo/order/OrderServiceTests.java` (new — repo has no
  existing test for this class; use Mockito mocks for `OrderRepository`/`ProductService`,
  mirroring `ProductServiceTests`' Given/When/Then style)

**Scope:** S (1 edited file, 1 new test file)

## Checkpoint: Foundation
- [x] `./mvnw test` passes
- [x] `./mvnw compile` succeeds
- [ ] Review with human before proceeding to Phase 2

## Phase 2: API Surface

### Task 3: Expose `promoCode` on the API
- [ ] Not started

**Description:** `PlaceOrderRequest` gains an optional `promoCode` field (nullable `String`, no
validation annotation). `OrderController.placeOrder` passes it through to
`orderService.placeOrder`. New `GlobalExceptionHandler` (`@RestControllerAdvice`) maps
`InvalidPromoCodeException` → `400` with body `{"error": "invalid promo code"}`. Scoped to this
exception type only — must not intercept `ProductService.getById`'s existing `RuntimeException`.

**Acceptance criteria:**
- [ ] `POST /api/orders` with `promoCode: "SAVE10"` → `200`, discounted `totalAmountCents`
- [ ] `POST /api/orders` with `promoCode: "NOPE"` → `400`, body `{"error": "invalid promo code"}`,
      and no `Order` row written (assert via repository count or mock verification)
- [ ] `POST /api/orders` with no `promoCode` field → identical response to current behavior
- [ ] `GET /api/products/{unknownId}` still returns `500` (unchanged) — confirms the new handler
      didn't accidentally widen scope onto scenario 2's bug

**Verification:**
- [ ] Tests pass: `./mvnw test`
- [ ] Build succeeds: `./mvnw compile`
- [ ] Manual check: `./mvnw spring-boot:run`, exercise all three `promoCode` cases via
      `/swagger-ui.html` or curl

**Dependencies:** Task 2

**Files:**
- `src/main/java/com/example/skillsdemo/order/PlaceOrderRequest.java` (edit)
- `src/main/java/com/example/skillsdemo/order/OrderController.java` (edit)
- `src/main/java/com/example/skillsdemo/order/GlobalExceptionHandler.java` (new)
- `src/test/java/com/example/skillsdemo/order/OrderControllerTests.java` (new — `@SpringBootTest`
  + `TestRestTemplate`, or `@WebMvcTest` + `MockMvc`)

**Scope:** M (2 edited files, 2 new files)

## Checkpoint: Complete
- [ ] `./mvnw test` passes (full suite)
- [ ] All `SPEC.md` success criteria met
- [ ] Swagger UI reflects the new field
- [ ] Ready for `/constraints` and `/review`

# Scenario 1 — New feature: promo-code discounts at checkout

**Task**: `POST /api/orders` should accept an optional `promoCode`. A valid code discounts the
order total by a fixed percentage; an invalid/expired code returns `400`, not a silent no-op.

**Pipeline**: `/spec` → `/plan` → `/constraints` → build (`incremental-implementation` +
`test-driven-development`) → `/test` → `/review` (`code-reviewer` persona).

This is the single-thread lifecycle: one skill hands off to the next, no parallel fan-out (that
comes in scenario 3).

---

## 1. `/spec` — spec-driven-development

Run `/spec add promo code discounts to order checkout` from inside `prod-grade-skills-demo/`.
The skill refuses to let you skip straight to code — it interviews for the ambiguous edges
first, then writes `SPEC.md`. Expect it to ask things like:

- Percentage or fixed-amount discount? (assume percentage for this walkthrough)
- Case-sensitive codes? Single-use or reusable? Where do valid codes live — hardcoded table,
  config, DB?
- What HTTP status for an invalid code — `400` with a body, or silently ignore it?

Sample resulting `SPEC.md` excerpt:

```markdown
## Feature: Promo code discount at checkout

### Behavior
- `POST /api/orders` gains an optional `promoCode` field.
- A known, active code applies its `discountPercent` to `totalAmountCents`, rounded down.
- An unknown or inactive code returns 400 with `{"error": "invalid promo code"}` — the order is
  NOT placed (no partial side effects: no stock reserved, no order row written).
- Omitting `promoCode` behaves exactly as today.

### Out of scope
- Code expiry dates, per-user usage limits, stacking multiple codes — flagged as follow-ups.
```

That "no partial side effects" line matters — it's the difference between a feature spec and a
vague wish, and it directly shapes the implementation order in step 3 (validate the code
*before* calling `product.reserveStock(...)`).

## 2. `/plan` — planning-and-task-breakdown

`/plan` turns `SPEC.md` into an ordered task list, respecting existing seams
(`OrderService.placeOrder`, `com.example.skillsdemo.order`). Expected breakdown:

1. `PromoCode` value object / lookup (start with a hardcoded `Map`, per spec's "config" answer).
2. `PromoCodeService.apply(String code, long amountCents) -> long` — throws
   `InvalidPromoCodeException` on unknown/inactive code.
3. `OrderService.placeOrder` gains an overload/param for `promoCode`, calls
   `PromoCodeService.apply` **before** `product.reserveStock(...)`.
4. `PlaceOrderRequest` gains an optional `promoCode` field.
5. `OrderController` maps `InvalidPromoCodeException` → 400 (a `@ExceptionHandler` — the same
   shape needed for scenario 2's fix, so the plan should flag reusing one `GlobalExceptionHandler`
   rather than two ad-hoc handlers).

## 3. `/constraints` — lock the quality bar before touching code

`constraint-driven-development` writes/updates a contract file recording things like "every new
`@RestController` branch has a test asserting its HTTP status" and "no `catch (Exception e) {}`
swallow". Its job during `/build` is to watch the diff and object if a shortcut appears — e.g. if
the implementation is tempted to return `200` with a `"discountApplied": false` field instead of
a real `400`, constraints should flag that as silently weakening the spec's stated behavior.

## 4. Build — `incremental-implementation` + `test-driven-development`

Per the standing `testing-style.md` rule, tests are Given/When/Then. Red first:

```java
// src/test/java/com/example/skillsdemo/order/PromoCodeServiceTests.java
@Test
void givenUnknownCode_whenApply_thenThrowsInvalidPromoCodeException() {
    // Given
    var service = new PromoCodeService();

    // When / Then
    assertThatThrownBy(() -> service.apply("NOPE", 10_000))
        .isInstanceOf(InvalidPromoCodeException.class);
}

@Test
void givenActiveTenPercentCode_whenApply_thenDiscountsAmount() {
    // Given
    var service = new PromoCodeService();

    // When
    long result = service.apply("SAVE10", 10_000);

    // Then
    assertThat(result).isEqualTo(9_000);
}
```

Then the minimal `PromoCodeService`:

```java
@Service
public class PromoCodeService {
    private static final Map<String, Integer> ACTIVE_CODES = Map.of("SAVE10", 10, "SAVE20", 20);

    public long apply(String code, long amountCents) {
        Integer discountPercent = ACTIVE_CODES.get(code);
        if (discountPercent == null) {
            throw new InvalidPromoCodeException(code);
        }
        return amountCents - (amountCents * discountPercent / 100);
    }
}
```

`incremental-implementation` keeps this to one seam at a time: `PromoCodeService` lands and is
green before `OrderService.placeOrder` is touched, which lands and is green before
`OrderController`/`PlaceOrderRequest` change. `OrderService.placeOrder` (see
`src/main/java/com/example/skillsdemo/order/OrderService.java:24`) becomes:

```java
public Order placeOrder(Long productId, int quantity, String promoCode) {
    Product product = productService.getById(productId);
    long baseTotal = product.getPriceCents() * quantity;
    long totalAmountCents = promoCode == null ? baseTotal : promoCodeService.apply(promoCode, baseTotal);
    product.reserveStock(quantity); // after pricing/validation -- no partial side effects
    return orderRepository.save(new Order(productId, quantity, totalAmountCents, "PLACED"));
}
```

## 5. `/test` and `/review`

`/test` runs the full suite plus a coverage check against the `/constraints` contract. `/review`
dispatches the `code-reviewer` persona (5-axis: correctness, readability, architecture, security,
performance). Expect it to catch things a human skim might miss on axis "architecture" — e.g. if
`PromoCodeService` were injected into `OrderController` directly instead of through
`OrderService`, that's a layering violation the persona is specifically primed to flag per
`docs/agents.md`'s persona definitions.

## 6. Shipping this

Not covered here — scenario 3 walks the `/ship` fan-out gate in full, using the Spring Boot
upgrade as its example, but the same gate applies to any feature before merge.

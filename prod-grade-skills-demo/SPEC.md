# Spec: Promo Code Discounts at Checkout

## Objective
`POST /api/orders` accepts an optional `promoCode`. A known, active code discounts the order
total by a fixed percentage. An unknown/inactive code rejects the whole order with `400` — no
partial side effects (no stock reserved, no order row written). Omitting `promoCode` behaves
exactly as today.

User: caller of the Order API (any client). Success = valid codes reliably discount; invalid
codes reliably reject with no side effects; existing no-promo checkout path is unchanged.

## Tech Stack
Java 17, Spring Boot 3.2.0, Spring Data JPA, H2 (in-memory), springdoc-openapi (already present).
No new dependencies.

## Commands
- Build: `./mvnw compile`
- Test: `./mvnw test`
- Run: `./mvnw spring-boot:run`
- Full verify: `./mvnw verify`

## Project Structure
All new code lives under the existing `order` package — no new modules:
```
src/main/java/com/example/skillsdemo/order/
  PromoCodeService.java          → new: lookup + apply discount
  InvalidPromoCodeException.java → new: unchecked, carries the rejected code
  GlobalExceptionHandler.java    → new: @ControllerAdvice, maps exception → 400
  OrderService.java              → edit: placeOrder gains promoCode param
  OrderController.java           → edit: passes request.promoCode() through
  PlaceOrderRequest.java         → edit: adds optional promoCode field
src/test/java/com/example/skillsdemo/order/
  PromoCodeServiceTests.java     → new
  OrderServiceTests.java         → new (or extend if one exists)
```

## Code Style
Matches existing repo conventions (constructor injection, `record` DTOs, no comments beyond
Javadoc-style seam notes). Example — the core lookup:
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
`OrderService.placeOrder` validates/applies the promo **before** `product.reserveStock(...)` —
this ordering is load-bearing for the "no partial side effects" requirement, not stylistic.

## Testing Strategy
JUnit 5, Given/When/Then per `testing-style.md` (`given<Condition>_when<Action>_then<Outcome>`).
- Unit: `PromoCodeServiceTests` — known code discounts correctly; unknown code throws
  `InvalidPromoCodeException`; no `promoCode` (null) path unaffected.
- Unit/slice: `OrderServiceTests` — `placeOrder` with valid code discounts total; with invalid
  code throws and `orderRepository.save` / `product.reserveStock` are never called (verify via
  Mockito `verifyNoInteractions`/`verify(..., never())`).
- Web: controller test (MockMvc or `@SpringBootTest` + `TestRestTemplate`) asserting `POST
  /api/orders` with a bad code returns `400` and body `{"error": "invalid promo code"}`.
- Run via `./mvnw test`; no new test infra needed (H2 already in-memory, no Docker).

## Boundaries
- **Always do**: run `./mvnw test` before considering a task done; keep promo validation ahead
  of `reserveStock` in `placeOrder`; Given/When/Then test naming; discount math on `long` cents
  (no floating point).
- **Ask first**: introducing a DB table for promo codes (spec assumes hardcoded `Map` for now);
  changing `Order`'s persisted schema/columns; adding a new dependency.
- **Never do**: silently ignore an invalid code (must be `400`, not a no-op or `200` with a
  `discountApplied:false` flag); reserve stock or persist an order before promo validation
  succeeds; hardcode secrets (n/a here, but standing rule).

## Success Criteria
- `POST /api/orders` with a valid `promoCode` returns `200` with `totalAmountCents` discounted
  by the code's percentage (integer division, rounded down).
- `POST /api/orders` with an unknown/inactive `promoCode` returns `400`, body
  `{"error": "invalid promo code"}`, and no `Order` row is written, no stock reserved.
- `POST /api/orders` without `promoCode` is byte-for-byte unchanged from current behavior.
- All new/edited tests pass under `./mvnw test`; existing `ProductServiceTests` and
  `ProdGradeSkillsDemoApplicationTests` still pass.

## Open Questions
None — ambiguous edges (discount type, code source, invalid-code status, expiry/stacking scope)
resolved during spec review; see decisions baked into Objective/Boundaries above.

## Out of Scope (flagged follow-ups)
Code expiry dates, per-user/single-use redemption limits, stacking multiple codes, moving codes
from hardcoded `Map` to DB/config.

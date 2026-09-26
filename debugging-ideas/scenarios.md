# Runtime debug scenarios (not guessable from static code read)

1. **Nullable DB column NPE** (implementing now) — product.category is nullable.
   Service uppercases category. Seed data has category on all rows except one
   product. NPE only on that one product's request; code looks correct.

2. **Hibernate lazy-loading trap** — entity has LAZY @OneToMany. One call path
   accesses the collection after the session/tx closed (detached entity or
   async), throwing LazyInitializationException only on that path.

3. **Cache staleness / shared mutable state** — in-memory cache keyed or
   invalidated wrong. GET returns stale data only after an UPDATE has
   happened at least once; first run always looks correct.

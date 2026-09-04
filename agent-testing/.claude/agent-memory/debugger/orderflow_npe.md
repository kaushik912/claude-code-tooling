---
name: orderflow_npe
description: root cause of NPE in ControllerFlowController.OrderFlow.buildShippingLabel for guest orders
metadata:
  type: project
---

Fixed NPE at ControllerFlowController.java:64 (OrderFlow.buildShippingLabel): chain checked
order != null -> order.customer != null -> order.customer.address.city, skipping the
address != null check. Guest orders (OrderStore.lookup returns Customer("Guest", null) when
orderId null/"guest") have null address, so .city access threw NPE.

Fix: added `if (order.customer.address == null) return "... -> no address on file";` before
accessing .city/.zip.

Why: guest checkout flow intentionally has no address on file (see OrderStore.lookup comment).
How to apply: this repo (agent-testing) uses this file as a deliberate skeleton/test bed for NPE
bugs with deeply nested null chains — watch for similar unchecked chains (obj.a.b.c) elsewhere
if more "Flow" classes are added.

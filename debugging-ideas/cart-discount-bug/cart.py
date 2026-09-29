"""Shopping cart with a deliberate bug, for practicing DebugMCP."""

DISCOUNTS = {
    "SAVE10": 0.10,   # 10% off
    "HALF": 0.50,     # 50% off
}


def subtotal(items):
    return sum(i["price"] * i["qty"] for i in items)


def apply_discount(total, code):
    rate = DISCOUNTS.get(code, 0)
    return total * (1 - rate)


def checkout(items, code=None):
    total = subtotal(items)
    if code:
        total = apply_discount(total, code)
    return round(total, 2)


if __name__ == "__main__":
    cart = [
        {"name": "book", "price": 20.0, "qty": 2},
        {"name": "pen", "price": 2.5, "qty": 4},
    ]
    # subtotal = 50.0 -> with SAVE10 expected 45.0
    print("SAVE10 total:", checkout(cart, "SAVE10"))
    print("HALF total:  ", checkout(cart, "HALF"))

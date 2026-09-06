package com.example.skillsdemo.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PlaceOrderRequest(@NotNull Long productId, @Min(1) int quantity) {
}

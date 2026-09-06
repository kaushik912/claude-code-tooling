package com.example.skillsdemo.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateProductRequest(
		@NotBlank String sku,
		@NotBlank String name,
		@PositiveOrZero long priceCents,
		@Min(0) int stockQty) {
}

package com.example.skillsdemo.order;

public record OrderResponse(Long id, Long productId, int quantity, long totalAmountCents, String status) {

	public static OrderResponse from(Order order) {
		return new OrderResponse(order.getId(), order.getProductId(), order.getQuantity(),
				order.getTotalAmountCents(), order.getStatus());
	}
}

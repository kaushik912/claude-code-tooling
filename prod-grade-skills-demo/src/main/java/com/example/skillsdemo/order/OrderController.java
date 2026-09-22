package com.example.skillsdemo.order;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Order checkout")
public class OrderController {

	private final OrderService orderService;

	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@PostMapping
	@Operation(summary = "Place an order")
	public OrderResponse placeOrder(@Valid @RequestBody PlaceOrderRequest request) {
		var order = orderService.placeOrder(request.productId(), request.quantity(), request.promoCode());
		return OrderResponse.from(order);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get an order by id")
	public OrderResponse getOrder(@PathVariable Long id) {
		return OrderResponse.from(orderService.getById(id));
	}
}

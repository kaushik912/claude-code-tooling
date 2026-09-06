package com.example.skillsdemo.order;

import org.springframework.stereotype.Service;

import com.example.skillsdemo.product.Product;
import com.example.skillsdemo.product.ProductService;

@Service
public class OrderService {

	private final OrderRepository orderRepository;
	private final ProductService productService;

	public OrderService(OrderRepository orderRepository, ProductService productService) {
		this.orderRepository = orderRepository;
		this.productService = productService;
	}

	/**
	 * Baseline checkout flow -- no promo codes yet. See
	 * docs/scenarios/01-new-feature-promo-code.md for the feature walkthrough
	 * that extends this method.
	 */
	public Order placeOrder(Long productId, int quantity) {
		Product product = productService.getById(productId);
		product.reserveStock(quantity);
		long totalAmountCents = product.getPriceCents() * quantity;
		return orderRepository.save(new Order(productId, quantity, totalAmountCents, "PLACED"));
	}

	public Order getById(Long id) {
		return orderRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Order not found: " + id));
	}
}

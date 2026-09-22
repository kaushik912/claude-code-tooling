package com.example.skillsdemo.order;

import org.springframework.stereotype.Service;

import com.example.skillsdemo.product.Product;
import com.example.skillsdemo.product.ProductService;

@Service
public class OrderService {

	private final OrderRepository orderRepository;
	private final ProductService productService;
	private final PromoCodeService promoCodeService;

	public OrderService(OrderRepository orderRepository, ProductService productService,
			PromoCodeService promoCodeService) {
		this.orderRepository = orderRepository;
		this.productService = productService;
		this.promoCodeService = promoCodeService;
	}

	public Order placeOrder(Long productId, int quantity, String promoCode) {
		Product product = productService.getById(productId);
		long baseTotal = product.getPriceCents() * quantity;
		long totalAmountCents = promoCode == null ? baseTotal : promoCodeService.apply(promoCode, baseTotal);
		product.reserveStock(quantity); // after pricing/validation -- no partial side effects
		return orderRepository.save(new Order(productId, quantity, totalAmountCents, "PLACED"));
	}

	public Order getById(Long id) {
		return orderRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Order not found: " + id));
	}
}

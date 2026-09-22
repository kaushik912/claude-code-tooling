package com.example.skillsdemo.order;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.skillsdemo.product.Product;
import com.example.skillsdemo.product.ProductService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class OrderControllerTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private ProductService productService;

	@Autowired
	private OrderRepository orderRepository;

	@Test
	void givenValidPromoCode_whenPlaceOrder_thenReturns200WithDiscountedTotal() {
		// Given
		Product product = productService.create("SKU-PROMO-1", "Promo Widget", 10_000, 10);
		var request = new PlaceOrderRequest(product.getId(), 1, "SAVE10");

		// When
		ResponseEntity<OrderResponse> response = restTemplate.postForEntity("/api/orders", request, OrderResponse.class);

		// Then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().totalAmountCents()).isEqualTo(9_000);
	}

	@Test
	void givenInvalidPromoCode_whenPlaceOrder_thenReturns400AndPersistsNoOrder() {
		// Given
		Product product = productService.create("SKU-PROMO-2", "Promo Widget 2", 10_000, 10);
		var request = new PlaceOrderRequest(product.getId(), 1, "NOPE");
		long ordersBefore = orderRepository.count();

		// When
		ResponseEntity<String> response = restTemplate.postForEntity("/api/orders", request, String.class);

		// Then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody()).contains("invalid promo code");
		assertThat(orderRepository.count()).isEqualTo(ordersBefore);
	}

	@Test
	void givenNoPromoCode_whenPlaceOrder_thenReturns200WithFullPrice() {
		// Given
		Product product = productService.create("SKU-PROMO-3", "Promo Widget 3", 10_000, 10);
		var request = new PlaceOrderRequest(product.getId(), 2, null);

		// When
		ResponseEntity<OrderResponse> response = restTemplate.postForEntity("/api/orders", request, OrderResponse.class);

		// Then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().totalAmountCents()).isEqualTo(20_000);
	}

	@Test
	void givenUnknownProductId_whenGetProduct_thenReturns404() {
		// Bug: docs/scenarios/02-bug-fix-404-mapping.md -- unknown id used to surface
		// as a bare 500; ProductNotFoundException + GlobalExceptionHandler now map it to 404.

		// When
		// Before fix: 500. Now: 404 with a "Product not found" message.
		ResponseEntity<String> response = restTemplate.getForEntity("/api/products/999999", String.class);

		// Then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(response.getBody()).contains("Product not found");
	}
}

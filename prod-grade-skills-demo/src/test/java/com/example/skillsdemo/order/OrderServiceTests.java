package com.example.skillsdemo.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.skillsdemo.product.Product;
import com.example.skillsdemo.product.ProductService;

@ExtendWith(MockitoExtension.class)
class OrderServiceTests {

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private ProductService productService;

	private OrderService orderService;

	@BeforeEach
	void setUp() {
		orderService = new OrderService(orderRepository, productService, new PromoCodeService());
	}

	@Test
	void givenNoPromoCode_whenPlaceOrder_thenChargesFullPrice() {
		// Given
		Product product = new Product("SKU-1", "Widget", 10_000, 5);
		when(productService.getById(1L)).thenReturn(product);
		when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// When
		Order order = orderService.placeOrder(1L, 2, null);

		// Then
		assertThat(order.getTotalAmountCents()).isEqualTo(20_000);
	}

	@Test
	void givenValidPromoCode_whenPlaceOrder_thenDiscountsTotal() {
		// Given
		Product product = new Product("SKU-1", "Widget", 10_000, 5);
		when(productService.getById(1L)).thenReturn(product);
		when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// When
		Order order = orderService.placeOrder(1L, 1, "SAVE10");

		// Then
		assertThat(order.getTotalAmountCents()).isEqualTo(9_000);
	}

	@Test
	void givenInvalidPromoCode_whenPlaceOrder_thenThrowsAndPersistsNothing() {
		// Given
		Product product = mock(Product.class);
		when(productService.getById(1L)).thenReturn(product);

		// When / Then
		assertThatThrownBy(() -> orderService.placeOrder(1L, 1, "NOPE"))
				.isInstanceOf(InvalidPromoCodeException.class);
		verify(product, never()).reserveStock(anyInt());
		verifyNoInteractions(orderRepository);
	}
}

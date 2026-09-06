package com.example.skillsdemo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.skillsdemo.product.ProductService;

@Component
class DataSeeder implements CommandLineRunner {

	private final ProductService productService;

	DataSeeder(ProductService productService) {
		this.productService = productService;
	}

	@Override
	public void run(String... args) {
		productService.create("SKU-001", "Mechanical Keyboard", 8_999, 25);
		productService.create("SKU-002", "USB-C Dock", 4_499, 40);
	}
}

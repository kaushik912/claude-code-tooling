package com.example.skillsdemo.product;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ProductService {

	private final ProductRepository productRepository;

	public ProductService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	public List<Product> findAll() {
		return productRepository.findAll();
	}

	public Product create(String sku, String name, long priceCents, int stockQty) {
		return productRepository.save(new Product(sku, name, priceCents, stockQty));
	}

	/**
	 * NOTE: this is the seeded bug for docs/scenarios/02-bug-fix-404-mapping.md --
	 * an unknown id currently surfaces as a bare RuntimeException, which Spring
	 * maps to a 500 instead of a 404. See that doc for the fix walkthrough.
	 */
	public Product getById(Long id) {
		return productRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Product not found: " + id));
	}
}

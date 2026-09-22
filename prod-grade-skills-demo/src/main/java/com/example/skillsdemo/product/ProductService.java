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

	public Product getById(Long id) {
		return productRepository.findById(id)
				.orElseThrow(() -> new ProductNotFoundException(id));
	}
}

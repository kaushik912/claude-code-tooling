package com.example.skillsdemo.product;

import java.util.List;

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
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Product catalog and inventory")
public class ProductController {

	private final ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@GetMapping
	@Operation(summary = "List all products")
	public List<ProductResponse> listProducts() {
		return productService.findAll().stream().map(ProductResponse::from).toList();
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get a product by id")
	public ProductResponse getProduct(@PathVariable Long id) {
		return ProductResponse.from(productService.getById(id));
	}

	@PostMapping
	@Operation(summary = "Create a product")
	public ProductResponse createProduct(@Valid @RequestBody CreateProductRequest request) {
		var product = productService.create(request.sku(), request.name(), request.priceCents(), request.stockQty());
		return ProductResponse.from(product);
	}
}

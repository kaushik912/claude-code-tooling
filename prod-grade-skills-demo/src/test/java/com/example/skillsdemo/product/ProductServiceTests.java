package com.example.skillsdemo.product;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ProductServiceTests {

	@Autowired
	private ProductRepository productRepository;

	private ProductService productService;

	@BeforeEach
	void setUp() {
		productService = new ProductService(productRepository);
	}

	@Test
	void givenNewProduct_whenCreate_thenPersistedWithGivenFields() {
		// Given
		String sku = "SKU-TEST";

		// When
		Product created = productService.create(sku, "Test Widget", 1_000, 5);

		// Then
		assertThat(created.getId()).isNotNull();
		assertThat(created.getSku()).isEqualTo(sku);
		assertThat(created.getStockQty()).isEqualTo(5);
	}

	@Test
	void givenExistingProduct_whenGetById_thenReturnsIt() {
		// Given
		Product saved = productService.create("SKU-EXISTING", "Existing Widget", 2_000, 10);

		// When
		Product found = productService.getById(saved.getId());

		// Then
		assertThat(found.getSku()).isEqualTo("SKU-EXISTING");
	}

	// NOTE: there is intentionally no test here for an unknown id -- that gap,
	// and the 500-instead-of-404 bug it hides, is the subject of
	// docs/scenarios/02-bug-fix-404-mapping.md.
}

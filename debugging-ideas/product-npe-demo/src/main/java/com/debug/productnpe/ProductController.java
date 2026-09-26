package com.debug.productnpe;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Get a product's category, upper-cased")
    @GetMapping("/products/{id}/category")
    public String getUpperCasedCategory(@PathVariable Long id) {
        return productService.getUpperCasedCategory(id);
    }
}

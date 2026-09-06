package com.example.skillsdemo.product;

public record ProductResponse(Long id, String sku, String name, long priceCents, int stockQty) {

	public static ProductResponse from(Product product) {
		return new ProductResponse(product.getId(), product.getSku(), product.getName(), product.getPriceCents(),
				product.getStockQty());
	}
}

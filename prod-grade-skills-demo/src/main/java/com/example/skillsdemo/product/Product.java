package com.example.skillsdemo.product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String sku;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private long priceCents;

	@Column(nullable = false)
	private int stockQty;

	protected Product() {
		// JPA
	}

	public Product(String sku, String name, long priceCents, int stockQty) {
		this.sku = sku;
		this.name = name;
		this.priceCents = priceCents;
		this.stockQty = stockQty;
	}

	public Long getId() {
		return id;
	}

	public String getSku() {
		return sku;
	}

	public String getName() {
		return name;
	}

	public long getPriceCents() {
		return priceCents;
	}

	public int getStockQty() {
		return stockQty;
	}

	public void reserveStock(int quantity) {
		if (quantity > stockQty) {
			throw new IllegalStateException("Insufficient stock for product " + sku);
		}
		this.stockQty -= quantity;
	}
}

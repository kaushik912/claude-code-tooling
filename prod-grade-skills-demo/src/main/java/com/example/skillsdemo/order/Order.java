package com.example.skillsdemo.order;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders") // "order" is a reserved SQL keyword
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long productId;

	@Column(nullable = false)
	private int quantity;

	@Column(nullable = false)
	private long totalAmountCents;

	@Column(nullable = false)
	private String status;

	protected Order() {
		// JPA
	}

	public Order(Long productId, int quantity, long totalAmountCents, String status) {
		this.productId = productId;
		this.quantity = quantity;
		this.totalAmountCents = totalAmountCents;
		this.status = status;
	}

	public Long getId() {
		return id;
	}

	public Long getProductId() {
		return productId;
	}

	public int getQuantity() {
		return quantity;
	}

	public long getTotalAmountCents() {
		return totalAmountCents;
	}

	public String getStatus() {
		return status;
	}
}

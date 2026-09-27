package com.debug.productnpe;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String comment;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    protected Review() {
    }

    public Review(String comment, Product product) {
        this.comment = comment;
        this.product = product;
    }

    public Long getId() {
        return id;
    }

    public String getComment() {
        return comment;
    }

    public Product getProduct() {
        return product;
    }
}

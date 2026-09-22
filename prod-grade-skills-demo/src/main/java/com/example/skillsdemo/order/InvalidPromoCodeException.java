package com.example.skillsdemo.order;

public class InvalidPromoCodeException extends RuntimeException {

	public InvalidPromoCodeException(String code) {
		super("Invalid promo code: " + code);
	}
}

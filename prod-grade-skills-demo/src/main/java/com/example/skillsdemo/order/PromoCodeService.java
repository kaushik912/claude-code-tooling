package com.example.skillsdemo.order;

import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class PromoCodeService {

	private static final Map<String, Integer> ACTIVE_CODES = Map.of("SAVE10", 10, "SAVE20", 20);

	public long apply(String code, long amountCents) {
		Integer discountPercent = ACTIVE_CODES.get(code);
		if (discountPercent == null) {
			throw new InvalidPromoCodeException(code);
		}
		return amountCents - (amountCents * discountPercent / 100);
	}
}

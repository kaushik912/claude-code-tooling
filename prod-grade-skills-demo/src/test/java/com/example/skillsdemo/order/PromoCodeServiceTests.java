package com.example.skillsdemo.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class PromoCodeServiceTests {

	private final PromoCodeService service = new PromoCodeService();

	@Test
	void givenActiveTenPercentCode_whenApply_thenDiscountsAmount() {
		// Given
		String code = "SAVE10";

		// When
		long result = service.apply(code, 10_000);

		// Then
		assertThat(result).isEqualTo(9_000);
	}

	@Test
	void givenActiveTwentyPercentCode_whenApply_thenDiscountsAmount() {
		// Given
		String code = "SAVE20";

		// When
		long result = service.apply(code, 10_000);

		// Then
		assertThat(result).isEqualTo(8_000);
	}

	@Test
	void givenOddAmount_whenApplyTenPercent_thenRoundsDown() {
		// Given
		String code = "SAVE10";

		// When
		long result = service.apply(code, 9_999);

		// Then
		assertThat(result).isEqualTo(9_999 - 999);
	}

	@Test
	void givenUnknownCode_whenApply_thenThrowsInvalidPromoCodeException() {
		// Given
		String code = "NOPE";

		// When / Then
		assertThatThrownBy(() -> service.apply(code, 10_000))
				.isInstanceOf(InvalidPromoCodeException.class)
				.hasMessageContaining(code);
	}
}

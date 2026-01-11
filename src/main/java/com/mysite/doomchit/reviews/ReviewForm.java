package com.mysite.doomchit.reviews;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewForm {
	
	@NotBlank(message = "내용을 입력해 주세요.")
	private String content;
	
	@NotNull(message = "평점을 선택해 주세요.")
    @DecimalMin(value = "1.0", inclusive = true, message = "평점은 1.0 이상이어야 합니다.")
    @DecimalMax(value = "5.0", inclusive = true, message = "평점은 5.0 이하여야 합니다.")
    private BigDecimal rating;
}

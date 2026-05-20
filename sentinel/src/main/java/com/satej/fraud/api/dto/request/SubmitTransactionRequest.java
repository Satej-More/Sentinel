package com.satej.fraud.api.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SubmitTransactionRequest(
		@NotBlank @Size(max = 64) String externalTransactionId,
		@NotBlank @Size(max = 64) String userId,
		@NotNull @DecimalMin(value = "0.01", message = "amount must be greater than zero") BigDecimal amount,
		@NotBlank @Pattern(regexp = "^[A-Za-z]{3}$", message = "currency must be a 3-letter ISO code") String currency,
		@NotBlank @Size(max = 64) String merchantId,
		@Size(max = 45) String ipAddress,
		@Size(max = 128) String deviceId) {
}

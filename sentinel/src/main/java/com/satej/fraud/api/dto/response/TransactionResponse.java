package com.satej.fraud.api.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.satej.fraud.domain.model.TransactionStatus;

public record TransactionResponse(
		UUID id,
		String externalTransactionId,
		String userId,
		BigDecimal amount,
		String currency,
		String merchantId,
		String ipAddress,
		String deviceId,
		TransactionStatus status,
		Instant createdAt) {
}

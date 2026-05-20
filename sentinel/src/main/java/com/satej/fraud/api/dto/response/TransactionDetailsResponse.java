package com.satej.fraud.api.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.satej.fraud.domain.model.TransactionStatus;

public record TransactionDetailsResponse(
		UUID id,
		String externalTransactionId,
		String userId,
		BigDecimal amount,
		String currency,
		String merchantId,
		String ipAddress,
		String deviceId,
		TransactionStatus status,
		Instant createdAt,
		Integer fraudScore,
		List<FraudReasonResponse> reasons) {
}

package com.satej.fraud.application.port.in;

import java.math.BigDecimal;

public record SubmitTransactionCommand(
		String externalTransactionId,
		String userId,
		BigDecimal amount,
		String currency,
		String merchantId,
		String ipAddress,
		String deviceId) {
}

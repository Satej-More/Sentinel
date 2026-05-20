package com.satej.fraud.domain.fraud.model;

import java.math.BigDecimal;

import com.satej.fraud.domain.model.Transaction;

public record FraudEvaluationContext(
		Transaction transaction,
		int recentUserTransactionCount,
		boolean merchantBlacklisted,
		boolean ipSuspicious,
		BigDecimal largeAmountThreshold,
		int velocityThreshold,
		int velocityWindowSeconds) {
}

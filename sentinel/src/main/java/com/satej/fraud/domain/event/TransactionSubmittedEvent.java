package com.satej.fraud.domain.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.satej.fraud.domain.model.Transaction;

public record TransactionSubmittedEvent(
		UUID transactionId,
		String externalTransactionId,
		String userId,
		BigDecimal amount,
		String currency,
		String merchantId,
		Instant submittedAt) {

	public static TransactionSubmittedEvent from(Transaction transaction) {
		return new TransactionSubmittedEvent(
				transaction.getId(),
				transaction.getExternalTransactionId(),
				transaction.getUserId(),
				transaction.getAmount(),
				transaction.getCurrency(),
				transaction.getMerchantId(),
				transaction.getCreatedAt());
	}
}

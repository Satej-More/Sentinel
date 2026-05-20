package com.satej.fraud.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Transaction {

	private final UUID id;
	private final String externalTransactionId;
	private final String userId;
	private final BigDecimal amount;
	private final String currency;
	private final String merchantId;
	private final String ipAddress;
	private final String deviceId;
	private final TransactionStatus status;
	private final Instant createdAt;

	private Transaction(
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
		this.id = id;
		this.externalTransactionId = externalTransactionId;
		this.userId = userId;
		this.amount = amount;
		this.currency = currency;
		this.merchantId = merchantId;
		this.ipAddress = ipAddress;
		this.deviceId = deviceId;
		this.status = status;
		this.createdAt = createdAt;
	}

	public static Transaction createNew(
			String externalTransactionId,
			String userId,
			BigDecimal amount,
			String currency,
			String merchantId,
			String ipAddress,
			String deviceId) {
		Objects.requireNonNull(externalTransactionId, "externalTransactionId must not be null");
		Objects.requireNonNull(userId, "userId must not be null");
		Objects.requireNonNull(amount, "amount must not be null");
		Objects.requireNonNull(currency, "currency must not be null");
		Objects.requireNonNull(merchantId, "merchantId must not be null");

		if (externalTransactionId.isBlank()) {
			throw new IllegalArgumentException("externalTransactionId must not be blank");
		}
		if (userId.isBlank()) {
			throw new IllegalArgumentException("userId must not be blank");
		}
		if (merchantId.isBlank()) {
			throw new IllegalArgumentException("merchantId must not be blank");
		}
		if (amount.signum() <= 0) {
			throw new IllegalArgumentException("amount must be greater than zero");
		}
		String normalizedCurrency = currency.trim().toUpperCase();
		if (!normalizedCurrency.matches("[A-Z]{3}")) {
			throw new IllegalArgumentException("currency must be a 3-letter ISO code");
		}

		return new Transaction(
				UUID.randomUUID(),
				externalTransactionId.trim(),
				userId.trim(),
				amount,
				normalizedCurrency,
				merchantId.trim(),
				trimToNull(ipAddress),
				trimToNull(deviceId),
				TransactionStatus.RECEIVED,
				Instant.now());
	}

	public static Transaction restore(
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
		return new Transaction(
				id,
				externalTransactionId,
				userId,
				amount,
				currency,
				merchantId,
				ipAddress,
				deviceId,
				status,
				createdAt);
	}

	private static String trimToNull(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		return value.trim();
	}

	public UUID getId() {
		return id;
	}

	public String getExternalTransactionId() {
		return externalTransactionId;
	}

	public String getUserId() {
		return userId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public String getCurrency() {
		return currency;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public TransactionStatus getStatus() {
		return status;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public Transaction withStatus(TransactionStatus newStatus) {
		return restore(
				id,
				externalTransactionId,
				userId,
				amount,
				currency,
				merchantId,
				ipAddress,
				deviceId,
				newStatus,
				createdAt);
	}

	public boolean isEligibleForFraudEvaluation() {
		return status == TransactionStatus.RECEIVED;
	}
}

package com.satej.fraud.infrastructure.persistence.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.satej.fraud.domain.model.TransactionStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
		name = "transactions",
		uniqueConstraints = @UniqueConstraint(name = "uk_transactions_external_id", columnNames = "external_transaction_id"))
@EntityListeners(AuditingEntityListener.class)
public class TransactionJpaEntity {

	@Id
	private UUID id;

	@Column(name = "external_transaction_id", nullable = false, length = 64)
	private String externalTransactionId;

	@Column(name = "user_id", nullable = false, length = 64)
	private String userId;

	@Column(nullable = false, precision = 19, scale = 4)
	private BigDecimal amount;

	@Column(nullable = false, length = 3)
	private String currency;

	@Column(name = "merchant_id", nullable = false, length = 64)
	private String merchantId;

	@Column(name = "ip_address", length = 45)
	private String ipAddress;

	@Column(name = "device_id", length = 128)
	private String deviceId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 32)
	private TransactionStatus status;

	@CreatedDate
	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	public TransactionJpaEntity() {
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getExternalTransactionId() {
		return externalTransactionId;
	}

	public void setExternalTransactionId(String externalTransactionId) {
		this.externalTransactionId = externalTransactionId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public TransactionStatus getStatus() {
		return status;
	}

	public void setStatus(TransactionStatus status) {
		this.status = status;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}
}

package com.satej.fraud.domain.fraud.model;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.satej.fraud.domain.model.TransactionStatus;

public class FraudEvaluation {

	private final UUID id;
	private final UUID transactionId;
	private final int fraudScore;
	private final TransactionStatus decision;
	private final List<FraudReason> reasons;
	private final Instant evaluatedAt;

	private FraudEvaluation(
			UUID id,
			UUID transactionId,
			int fraudScore,
			TransactionStatus decision,
			List<FraudReason> reasons,
			Instant evaluatedAt) {
		this.id = id;
		this.transactionId = transactionId;
		this.fraudScore = fraudScore;
		this.decision = decision;
		this.reasons = List.copyOf(reasons);
		this.evaluatedAt = evaluatedAt;
	}

	public static FraudEvaluation create(
			UUID transactionId,
			int fraudScore,
			TransactionStatus decision,
			List<FraudReason> reasons) {
		Objects.requireNonNull(transactionId, "transactionId must not be null");
		Objects.requireNonNull(decision, "decision must not be null");
		Objects.requireNonNull(reasons, "reasons must not be null");

		int normalizedScore = Math.max(0, Math.min(fraudScore, 100));
		return new FraudEvaluation(UUID.randomUUID(), transactionId, normalizedScore, decision, reasons, Instant.now());
	}

	public static FraudEvaluation restore(
			UUID id,
			UUID transactionId,
			int fraudScore,
			TransactionStatus decision,
			List<FraudReason> reasons,
			Instant evaluatedAt) {
		return new FraudEvaluation(id, transactionId, fraudScore, decision, reasons, evaluatedAt);
	}

	public UUID getId() {
		return id;
	}

	public UUID getTransactionId() {
		return transactionId;
	}

	public int getFraudScore() {
		return fraudScore;
	}

	public TransactionStatus getDecision() {
		return decision;
	}

	public List<FraudReason> getReasons() {
		return reasons;
	}

	public Instant getEvaluatedAt() {
		return evaluatedAt;
	}
}

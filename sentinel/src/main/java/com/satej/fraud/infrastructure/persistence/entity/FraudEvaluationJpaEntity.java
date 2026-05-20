package com.satej.fraud.infrastructure.persistence.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.satej.fraud.domain.model.TransactionStatus;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "fraud_evaluations")
public class FraudEvaluationJpaEntity {

	@Id
	private UUID id;

	@Column(name = "transaction_id", nullable = false, unique = true)
	private UUID transactionId;

	@Column(name = "fraud_score", nullable = false)
	private int fraudScore;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 32)
	private TransactionStatus decision;

	@Column(name = "evaluated_at", nullable = false)
	private Instant evaluatedAt;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "fraud_evaluation_reasons", joinColumns = @JoinColumn(name = "evaluation_id"))
	private List<FraudReasonEmbeddable> reasons = new ArrayList<>();

	public FraudEvaluationJpaEntity() {
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(UUID transactionId) {
		this.transactionId = transactionId;
	}

	public int getFraudScore() {
		return fraudScore;
	}

	public void setFraudScore(int fraudScore) {
		this.fraudScore = fraudScore;
	}

	public TransactionStatus getDecision() {
		return decision;
	}

	public void setDecision(TransactionStatus decision) {
		this.decision = decision;
	}

	public Instant getEvaluatedAt() {
		return evaluatedAt;
	}

	public void setEvaluatedAt(Instant evaluatedAt) {
		this.evaluatedAt = evaluatedAt;
	}

	public List<FraudReasonEmbeddable> getReasons() {
		return reasons;
	}

	public void setReasons(List<FraudReasonEmbeddable> reasons) {
		this.reasons = reasons;
	}
}

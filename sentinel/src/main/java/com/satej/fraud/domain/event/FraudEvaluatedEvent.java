package com.satej.fraud.domain.event;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.satej.fraud.domain.fraud.model.FraudEvaluation;
import com.satej.fraud.domain.fraud.model.FraudReason;
import com.satej.fraud.domain.model.TransactionStatus;

public record FraudEvaluatedEvent(
		UUID transactionId,
		UUID evaluationId,
		int fraudScore,
		TransactionStatus decision,
		List<FraudReason> reasons,
		Instant evaluatedAt) {

	public FraudEvaluatedEvent {
		reasons = List.copyOf(reasons);
	}

	public static FraudEvaluatedEvent from(FraudEvaluation evaluation) {
		return new FraudEvaluatedEvent(
				evaluation.getTransactionId(),
				evaluation.getId(),
				evaluation.getFraudScore(),
				evaluation.getDecision(),
				evaluation.getReasons(),
				evaluation.getEvaluatedAt());
	}
}

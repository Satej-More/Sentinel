package com.satej.fraud.api.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.satej.fraud.domain.model.TransactionStatus;

public record FraudEvaluationResponse(
		UUID evaluationId,
		UUID transactionId,
		int fraudScore,
		TransactionStatus decision,
		TransactionStatus transactionStatus,
		List<FraudReasonResponse> reasons,
		Instant evaluatedAt) {
}

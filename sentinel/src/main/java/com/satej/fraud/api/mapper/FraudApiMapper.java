package com.satej.fraud.api.mapper;

import org.springframework.stereotype.Component;

import com.satej.fraud.api.dto.response.FraudEvaluationResponse;
import com.satej.fraud.api.dto.response.FraudReasonResponse;
import com.satej.fraud.domain.fraud.model.FraudEvaluation;
import com.satej.fraud.domain.fraud.model.FraudReason;

@Component
public class FraudApiMapper {

	public FraudEvaluationResponse toResponse(FraudEvaluation evaluation) {
		return new FraudEvaluationResponse(
				evaluation.getId(),
				evaluation.getTransactionId(),
				evaluation.getFraudScore(),
				evaluation.getDecision(),
				evaluation.getDecision(),
				evaluation.getReasons().stream().map(this::toReasonResponse).toList(),
				evaluation.getEvaluatedAt());
	}

	private FraudReasonResponse toReasonResponse(FraudReason reason) {
		return new FraudReasonResponse(reason.ruleCode(), reason.message());
	}
}

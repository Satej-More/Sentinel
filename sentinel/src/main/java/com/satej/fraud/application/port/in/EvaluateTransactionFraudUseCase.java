package com.satej.fraud.application.port.in;

import java.util.UUID;

import com.satej.fraud.domain.fraud.model.FraudEvaluation;

public interface EvaluateTransactionFraudUseCase {

	FraudEvaluation evaluate(UUID transactionId);
}

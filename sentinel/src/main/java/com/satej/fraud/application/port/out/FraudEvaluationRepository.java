package com.satej.fraud.application.port.out;

import java.util.Optional;
import java.util.UUID;

import com.satej.fraud.domain.fraud.model.FraudEvaluation;

public interface FraudEvaluationRepository {

	FraudEvaluation save(FraudEvaluation evaluation);

	Optional<FraudEvaluation> findByTransactionId(UUID transactionId);
}

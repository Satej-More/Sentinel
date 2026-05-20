package com.satej.fraud.infrastructure.persistence.adapter;

import org.springframework.stereotype.Component;

import com.satej.fraud.domain.fraud.model.FraudEvaluation;
import com.satej.fraud.domain.fraud.model.FraudReason;
import com.satej.fraud.infrastructure.persistence.entity.FraudEvaluationJpaEntity;
import com.satej.fraud.infrastructure.persistence.entity.FraudReasonEmbeddable;

@Component
public class FraudEvaluationPersistenceMapper {

	public FraudEvaluationJpaEntity toEntity(FraudEvaluation evaluation) {
		FraudEvaluationJpaEntity entity = new FraudEvaluationJpaEntity();
		entity.setId(evaluation.getId());
		entity.setTransactionId(evaluation.getTransactionId());
		entity.setFraudScore(evaluation.getFraudScore());
		entity.setDecision(evaluation.getDecision());
		entity.setEvaluatedAt(evaluation.getEvaluatedAt());
		entity.setReasons(evaluation.getReasons().stream()
				.map(reason -> new FraudReasonEmbeddable(reason.ruleCode(), reason.message()))
				.toList());
		return entity;
	}

	public FraudEvaluation toDomain(FraudEvaluationJpaEntity entity) {
		return FraudEvaluation.restore(
				entity.getId(),
				entity.getTransactionId(),
				entity.getFraudScore(),
				entity.getDecision(),
				entity.getReasons().stream()
						.map(embeddable -> new FraudReason(embeddable.getRuleCode(), embeddable.getMessage()))
						.toList(),
				entity.getEvaluatedAt());
	}
}

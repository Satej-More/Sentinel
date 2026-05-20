package com.satej.fraud.infrastructure.persistence.adapter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.satej.fraud.application.port.out.FraudEvaluationRepository;
import com.satej.fraud.domain.fraud.model.FraudEvaluation;

@Component
public class FraudEvaluationRepositoryAdapter implements FraudEvaluationRepository {

	private final FraudEvaluationJpaRepository jpaRepository;
	private final FraudEvaluationPersistenceMapper mapper;

	public FraudEvaluationRepositoryAdapter(
			FraudEvaluationJpaRepository jpaRepository,
			FraudEvaluationPersistenceMapper mapper) {
		this.jpaRepository = jpaRepository;
		this.mapper = mapper;
	}

	@Override
	public FraudEvaluation save(FraudEvaluation evaluation) {
		return mapper.toDomain(jpaRepository.save(mapper.toEntity(evaluation)));
	}

	@Override
	public Optional<FraudEvaluation> findByTransactionId(UUID transactionId) {
		return jpaRepository.findByTransactionId(transactionId).map(mapper::toDomain);
	}
}

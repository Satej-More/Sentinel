package com.satej.fraud.infrastructure.persistence.adapter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.satej.fraud.infrastructure.persistence.entity.FraudEvaluationJpaEntity;

public interface FraudEvaluationJpaRepository extends JpaRepository<FraudEvaluationJpaEntity, UUID> {

	Optional<FraudEvaluationJpaEntity> findByTransactionId(UUID transactionId);
}

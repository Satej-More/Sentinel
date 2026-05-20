package com.satej.fraud.infrastructure.persistence.adapter;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.satej.fraud.infrastructure.persistence.entity.TransactionJpaEntity;

public interface TransactionJpaRepository extends JpaRepository<TransactionJpaEntity, UUID> {

	boolean existsByExternalTransactionId(String externalTransactionId);

	Optional<TransactionJpaEntity> findByExternalTransactionId(String externalTransactionId);

	long countByUserIdAndCreatedAtAfter(String userId, Instant createdAt);
}

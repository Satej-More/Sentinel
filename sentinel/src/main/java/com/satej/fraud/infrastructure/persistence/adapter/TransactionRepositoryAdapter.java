package com.satej.fraud.infrastructure.persistence.adapter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.satej.fraud.application.port.out.TransactionRepository;
import com.satej.fraud.domain.model.Transaction;
import com.satej.fraud.infrastructure.persistence.entity.TransactionJpaEntity;

@Component
public class TransactionRepositoryAdapter implements TransactionRepository {

	private final TransactionJpaRepository jpaRepository;
	private final TransactionPersistenceMapper mapper;

	public TransactionRepositoryAdapter(
			TransactionJpaRepository jpaRepository,
			TransactionPersistenceMapper mapper) {
		this.jpaRepository = jpaRepository;
		this.mapper = mapper;
	}

	@Override
	public boolean existsByExternalTransactionId(String externalTransactionId) {
		return jpaRepository.existsByExternalTransactionId(externalTransactionId);
	}

	@Override
	public Transaction save(Transaction transaction) {
		TransactionJpaEntity saved = jpaRepository.save(mapper.toEntity(transaction));
		return mapper.toDomain(saved);
	}

	@Override
	public Optional<Transaction> findById(UUID id) {
		return jpaRepository.findById(id).map(mapper::toDomain);
	}

	@Override
	public Optional<Transaction> findByExternalTransactionId(String externalTransactionId) {
		return jpaRepository.findByExternalTransactionId(externalTransactionId).map(mapper::toDomain);
	}

	@Override
	public Transaction update(Transaction transaction) {
		TransactionJpaEntity existing = jpaRepository
				.findById(transaction.getId())
				.orElseThrow(() -> new IllegalStateException("Transaction not found for update: " + transaction.getId()));
		existing.setStatus(transaction.getStatus());
		return mapper.toDomain(jpaRepository.save(existing));
	}

	@Override
	public java.util.List<Transaction> findAll() {
		return jpaRepository.findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"))
				.stream()
				.map(mapper::toDomain)
				.collect(java.util.stream.Collectors.toList());
	}
}

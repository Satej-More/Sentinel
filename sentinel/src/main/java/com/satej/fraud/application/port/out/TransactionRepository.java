package com.satej.fraud.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.satej.fraud.domain.model.Transaction;

public interface TransactionRepository {

	boolean existsByExternalTransactionId(String externalTransactionId);

	Transaction save(Transaction transaction);

	Optional<Transaction> findById(UUID id);

	Optional<Transaction> findByExternalTransactionId(String externalTransactionId);

	Transaction update(Transaction transaction);

	List<Transaction> findAll();
}

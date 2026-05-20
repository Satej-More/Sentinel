package com.satej.fraud.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.satej.fraud.application.port.in.GetTransactionByExternalIdUseCase;
import com.satej.fraud.application.port.in.GetTransactionByIdUseCase;
import com.satej.fraud.application.port.out.TransactionRepository;
import com.satej.fraud.domain.exception.TransactionNotFoundException;
import com.satej.fraud.domain.model.Transaction;

@Service
public class GetTransactionService implements GetTransactionByIdUseCase, GetTransactionByExternalIdUseCase {

	private final TransactionRepository transactionRepository;

	public GetTransactionService(TransactionRepository transactionRepository) {
		this.transactionRepository = transactionRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public Transaction getById(UUID id) {
		return transactionRepository.findById(id).orElseThrow(() -> new TransactionNotFoundException(id));
	}

	@Override
	@Transactional(readOnly = true)
	public Transaction getByExternalTransactionId(String externalTransactionId) {
		if (externalTransactionId == null || externalTransactionId.isBlank()) {
			throw new IllegalArgumentException("externalTransactionId must not be blank");
		}

		String normalizedExternalId = externalTransactionId.trim();
		return transactionRepository
				.findByExternalTransactionId(normalizedExternalId)
				.orElseThrow(() -> new TransactionNotFoundException(normalizedExternalId));
	}
}

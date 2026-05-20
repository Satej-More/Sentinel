package com.satej.fraud.domain.exception;

import java.util.UUID;

public class TransactionNotFoundException extends RuntimeException {

	public TransactionNotFoundException(UUID id) {
		super("Transaction not found with id: " + id);
	}

	public TransactionNotFoundException(String externalTransactionId) {
		super("Transaction not found with external id: " + externalTransactionId);
	}
}

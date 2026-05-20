package com.satej.fraud.domain.exception;

public class DuplicateTransactionException extends RuntimeException {

	public DuplicateTransactionException(String externalTransactionId) {
		super("Transaction already exists with external id: " + externalTransactionId);
	}
}

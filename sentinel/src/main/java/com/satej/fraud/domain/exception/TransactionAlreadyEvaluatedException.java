package com.satej.fraud.domain.exception;

import java.util.UUID;

import com.satej.fraud.domain.model.TransactionStatus;

public class TransactionAlreadyEvaluatedException extends RuntimeException {

	public TransactionAlreadyEvaluatedException(UUID transactionId, TransactionStatus currentStatus) {
		super("Transaction %s has already been evaluated (current status: %s)".formatted(transactionId, currentStatus));
	}
}

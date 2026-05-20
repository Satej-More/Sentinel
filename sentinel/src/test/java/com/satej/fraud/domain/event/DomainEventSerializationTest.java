package com.satej.fraud.domain.event;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.satej.fraud.domain.fraud.model.FraudEvaluation;
import com.satej.fraud.domain.fraud.model.FraudReason;
import com.satej.fraud.domain.model.Transaction;
import com.satej.fraud.domain.model.TransactionStatus;

class DomainEventSerializationTest {

	@Test
	void transactionSubmittedEvent_fromTransaction_mapsFields() {
		Transaction transaction = Transaction.createNew(
				"txn-event", "user-1", new BigDecimal("10.00"), "USD", "merchant-1", "1.1.1.1", null);
		TransactionSubmittedEvent event = TransactionSubmittedEvent.from(transaction);

		assertEquals(transaction.getId(), event.transactionId());
		assertEquals(transaction.getExternalTransactionId(), event.externalTransactionId());
		assertEquals(transaction.getCreatedAt(), event.submittedAt());
	}

	@Test
	void fraudEvaluatedEvent_fromEvaluation_mapsFields() {
		UUID transactionId = UUID.randomUUID();
		FraudEvaluation evaluation = FraudEvaluation.create(
				transactionId,
				55,
				TransactionStatus.UNDER_REVIEW,
				List.of(new FraudReason("LARGE_AMOUNT", "Amount too high")));

		FraudEvaluatedEvent event = FraudEvaluatedEvent.from(evaluation);

		assertEquals(evaluation.getTransactionId(), event.transactionId());
		assertEquals(evaluation.getFraudScore(), event.fraudScore());
		assertEquals(evaluation.getDecision(), event.decision());
		assertEquals(1, event.reasons().size());
		assertEquals(evaluation.getEvaluatedAt(), event.evaluatedAt());
	}
}

package com.satej.fraud.infrastructure.messaging.kafka;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.satej.fraud.application.port.in.EvaluateTransactionFraudUseCase;
import com.satej.fraud.domain.event.TransactionSubmittedEvent;
import com.satej.fraud.domain.exception.TransactionAlreadyEvaluatedException;
import com.satej.fraud.domain.fraud.model.FraudEvaluation;
import com.satej.fraud.domain.model.TransactionStatus;

@ExtendWith(MockitoExtension.class)
class TransactionSubmittedEventConsumerTest {

	@Mock
	private EvaluateTransactionFraudUseCase evaluateTransactionFraudUseCase;

	@InjectMocks
	private TransactionSubmittedEventConsumer consumer;

	@Test
	void onTransactionSubmitted_triggersFraudEvaluation() {
		UUID transactionId = UUID.randomUUID();
		TransactionSubmittedEvent event = new TransactionSubmittedEvent(
				transactionId,
				"txn-1",
				"user-1",
				new BigDecimal("20.00"),
				"USD",
				"merchant-1",
				Instant.now());
		FraudEvaluation evaluation = FraudEvaluation.create(
				transactionId, 0, TransactionStatus.APPROVED, java.util.List.of());
		when(evaluateTransactionFraudUseCase.evaluate(transactionId)).thenReturn(evaluation);

		consumer.onTransactionSubmitted(event, "transaction-submitted", 1L);

		verify(evaluateTransactionFraudUseCase).evaluate(transactionId);
	}

	@Test
	void onTransactionSubmitted_alreadyEvaluated_isIgnored() {
		UUID transactionId = UUID.randomUUID();
		TransactionSubmittedEvent event = new TransactionSubmittedEvent(
				transactionId,
				"txn-2",
				"user-2",
				new BigDecimal("20.00"),
				"USD",
				"merchant-2",
				Instant.now());
		when(evaluateTransactionFraudUseCase.evaluate(transactionId))
				.thenThrow(new TransactionAlreadyEvaluatedException(transactionId, TransactionStatus.APPROVED));

		consumer.onTransactionSubmitted(event, "transaction-submitted", 2L);

		verify(evaluateTransactionFraudUseCase).evaluate(transactionId);
	}
}

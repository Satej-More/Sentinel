package com.satej.fraud.application.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.satej.fraud.application.port.in.SubmitTransactionCommand;
import com.satej.fraud.application.port.out.TransactionRepository;
import com.satej.fraud.application.port.out.TransactionSubmittedEventPublisher;
import com.satej.fraud.application.port.out.TransactionVelocityPort;
import com.satej.fraud.config.FraudProperties;
import com.satej.fraud.domain.event.TransactionSubmittedEvent;
import com.satej.fraud.domain.model.Transaction;

@ExtendWith(MockitoExtension.class)
class SubmitTransactionServiceTest {

	@Mock
	private TransactionRepository transactionRepository;

	@Mock
	private TransactionSubmittedEventPublisher transactionSubmittedEventPublisher;

	@Mock
	private TransactionVelocityPort transactionVelocityPort;

	@Mock
	private FraudProperties fraudProperties;

	@InjectMocks
	private SubmitTransactionService submitTransactionService;

	@Test
	void submit_withoutActiveTransaction_publishesEventAndRecordsVelocity() {
		SubmitTransactionCommand command = new SubmitTransactionCommand(
				"txn-publish", "user-1", new BigDecimal("12.00"), "USD", "merchant-1", null, null);
		Transaction created = Transaction.createNew(
				command.externalTransactionId(),
				command.userId(),
				command.amount(),
				command.currency(),
				command.merchantId(),
				command.ipAddress(),
				command.deviceId());

		when(transactionRepository.existsByExternalTransactionId(command.externalTransactionId())).thenReturn(false);
		when(transactionRepository.save(any(Transaction.class))).thenReturn(created);
		when(fraudProperties.getVelocityWindowSeconds()).thenReturn(30);

		submitTransactionService.submit(command);

		verify(transactionSubmittedEventPublisher).publish(any(TransactionSubmittedEvent.class));
		verify(transactionVelocityPort)
				.recordTransaction(eq("user-1"), eq(created.getId()), eq(created.getCreatedAt()), eq(30));
	}
}

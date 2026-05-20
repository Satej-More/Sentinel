package com.satej.fraud.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.satej.fraud.application.port.in.SubmitTransactionCommand;
import com.satej.fraud.application.port.in.SubmitTransactionUseCase;
import com.satej.fraud.application.port.out.TransactionRepository;
import com.satej.fraud.application.port.out.TransactionSubmittedEventPublisher;
import com.satej.fraud.application.port.out.TransactionVelocityPort;
import com.satej.fraud.config.FraudProperties;
import com.satej.fraud.domain.event.TransactionSubmittedEvent;
import com.satej.fraud.domain.exception.DuplicateTransactionException;
import com.satej.fraud.domain.model.Transaction;

@Service
public class SubmitTransactionService implements SubmitTransactionUseCase {

	private final TransactionRepository transactionRepository;
	private final TransactionSubmittedEventPublisher transactionSubmittedEventPublisher;
	private final TransactionVelocityPort transactionVelocityPort;
	private final FraudProperties fraudProperties;

	public SubmitTransactionService(
			TransactionRepository transactionRepository,
			TransactionSubmittedEventPublisher transactionSubmittedEventPublisher,
			TransactionVelocityPort transactionVelocityPort,
			FraudProperties fraudProperties) {
		this.transactionRepository = transactionRepository;
		this.transactionSubmittedEventPublisher = transactionSubmittedEventPublisher;
		this.transactionVelocityPort = transactionVelocityPort;
		this.fraudProperties = fraudProperties;
	}

	@Override
	@Transactional
	public Transaction submit(SubmitTransactionCommand command) {
		if (transactionRepository.existsByExternalTransactionId(command.externalTransactionId())) {
			throw new DuplicateTransactionException(command.externalTransactionId());
		}

		Transaction transaction = Transaction.createNew(
				command.externalTransactionId(),
				command.userId(),
				command.amount(),
				command.currency(),
				command.merchantId(),
				command.ipAddress(),
				command.deviceId());

		Transaction saved = transactionRepository.save(transaction);
		recordVelocityAfterCommit(saved);
		publishAfterCommit(TransactionSubmittedEvent.from(saved));
		return saved;
	}

	private void recordVelocityAfterCommit(Transaction transaction) {
		if (TransactionSynchronizationManager.isSynchronizationActive()) {
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
				@Override
				public void afterCommit() {
					recordVelocity(transaction);
				}
			});
			return;
		}
		recordVelocity(transaction);
	}

	private void recordVelocity(Transaction transaction) {
		transactionVelocityPort.recordTransaction(
				transaction.getUserId(),
				transaction.getId(),
				transaction.getCreatedAt(),
				fraudProperties.getVelocityWindowSeconds());
	}

	private void publishAfterCommit(TransactionSubmittedEvent event) {
		if (TransactionSynchronizationManager.isSynchronizationActive()) {
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
				@Override
				public void afterCommit() {
					transactionSubmittedEventPublisher.publish(event);
				}
			});
			return;
		}
		transactionSubmittedEventPublisher.publish(event);
	}
}

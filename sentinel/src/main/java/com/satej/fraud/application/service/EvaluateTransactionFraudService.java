package com.satej.fraud.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.satej.fraud.application.port.in.EvaluateTransactionFraudUseCase;
import com.satej.fraud.application.port.out.FraudEvaluatedEventPublisher;
import com.satej.fraud.application.port.out.FraudEvaluationRepository;
import com.satej.fraud.application.port.out.FraudReferenceDataPort;
import com.satej.fraud.application.port.out.TransactionRepository;
import com.satej.fraud.application.port.out.TransactionVelocityPort;
import com.satej.fraud.domain.exception.TransactionAlreadyEvaluatedException;
import com.satej.fraud.domain.exception.TransactionNotFoundException;
import com.satej.fraud.domain.fraud.engine.FraudRuleEngine;
import com.satej.fraud.domain.event.FraudEvaluatedEvent;
import com.satej.fraud.domain.fraud.model.FraudEvaluation;
import com.satej.fraud.domain.fraud.model.FraudEvaluationContext;
import com.satej.fraud.domain.model.Transaction;
import com.satej.fraud.config.FraudProperties;

@Service
public class EvaluateTransactionFraudService implements EvaluateTransactionFraudUseCase {

	private final TransactionRepository transactionRepository;
	private final FraudEvaluationRepository fraudEvaluationRepository;
	private final TransactionVelocityPort transactionVelocityPort;
	private final FraudReferenceDataPort fraudReferenceDataPort;
	private final FraudRuleEngine fraudRuleEngine;
	private final FraudProperties fraudProperties;
	private final FraudEvaluatedEventPublisher fraudEvaluatedEventPublisher;

	public EvaluateTransactionFraudService(
			TransactionRepository transactionRepository,
			FraudEvaluationRepository fraudEvaluationRepository,
			TransactionVelocityPort transactionVelocityPort,
			FraudReferenceDataPort fraudReferenceDataPort,
			FraudRuleEngine fraudRuleEngine,
			FraudProperties fraudProperties,
			FraudEvaluatedEventPublisher fraudEvaluatedEventPublisher) {
		this.transactionRepository = transactionRepository;
		this.fraudEvaluationRepository = fraudEvaluationRepository;
		this.transactionVelocityPort = transactionVelocityPort;
		this.fraudReferenceDataPort = fraudReferenceDataPort;
		this.fraudRuleEngine = fraudRuleEngine;
		this.fraudProperties = fraudProperties;
		this.fraudEvaluatedEventPublisher = fraudEvaluatedEventPublisher;
	}

	@Override
	@Transactional
	public FraudEvaluation evaluate(UUID transactionId) {
		Transaction transaction = transactionRepository
				.findById(transactionId)
				.orElseThrow(() -> new TransactionNotFoundException(transactionId));

		if (!transaction.isEligibleForFraudEvaluation()) {
			throw new TransactionAlreadyEvaluatedException(transactionId, transaction.getStatus());
		}

		FraudEvaluationContext context = buildContext(transaction);
		FraudEvaluation evaluation = fraudRuleEngine.evaluate(context);

		Transaction updatedTransaction = transaction.withStatus(evaluation.getDecision());
		transactionRepository.update(updatedTransaction);
		FraudEvaluation savedEvaluation = fraudEvaluationRepository.save(evaluation);
		publishAfterCommit(FraudEvaluatedEvent.from(savedEvaluation));
		return savedEvaluation;
	}

	private void publishAfterCommit(FraudEvaluatedEvent event) {
		if (TransactionSynchronizationManager.isSynchronizationActive()) {
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
				@Override
				public void afterCommit() {
					fraudEvaluatedEventPublisher.publish(event);
				}
			});
			return;
		}
		fraudEvaluatedEventPublisher.publish(event);
	}

	private FraudEvaluationContext buildContext(Transaction transaction) {
		int windowSeconds = fraudProperties.getVelocityWindowSeconds();
		long recentCount = transactionVelocityPort.countInRollingWindow(transaction.getUserId(), windowSeconds);

		boolean merchantBlacklisted = fraudReferenceDataPort.isMerchantBlacklisted(transaction.getMerchantId());
		boolean ipSuspicious = fraudReferenceDataPort.isIpSuspicious(transaction.getIpAddress());

		return new FraudEvaluationContext(
				transaction,
				(int) recentCount,
				merchantBlacklisted,
				ipSuspicious,
				fraudProperties.getLargeAmountThreshold(),
				fraudProperties.getRapidTransactionCountThreshold(),
				windowSeconds);
	}
}

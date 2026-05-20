package com.satej.fraud.infrastructure.messaging.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.satej.fraud.application.port.in.EvaluateTransactionFraudUseCase;
import com.satej.fraud.domain.event.TransactionSubmittedEvent;
import com.satej.fraud.domain.exception.TransactionAlreadyEvaluatedException;
import com.satej.fraud.domain.exception.TransactionNotFoundException;
import com.satej.fraud.domain.fraud.model.FraudEvaluation;

@Component
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true")
public class TransactionSubmittedEventConsumer {

	private static final Logger log = LoggerFactory.getLogger(TransactionSubmittedEventConsumer.class);

	private final EvaluateTransactionFraudUseCase evaluateTransactionFraudUseCase;

	public TransactionSubmittedEventConsumer(EvaluateTransactionFraudUseCase evaluateTransactionFraudUseCase) {
		this.evaluateTransactionFraudUseCase = evaluateTransactionFraudUseCase;
	}

	@KafkaListener(
			topics = "${app.kafka.transaction-submitted-topic}",
			groupId = "${spring.kafka.consumer.group-id}",
			containerFactory = "transactionSubmittedKafkaListenerContainerFactory")
	public void onTransactionSubmitted(
			@Payload TransactionSubmittedEvent event,
			@Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
			@Header(KafkaHeaders.OFFSET) long offset) {
		log.info(
				"Consuming TransactionSubmittedEvent from topic={} offset={} transactionId={}",
				topic,
				offset,
				event.transactionId());

		try {
			FraudEvaluation evaluation = evaluateTransactionFraudUseCase.evaluate(event.transactionId());
			log.info(
					"Async fraud evaluation completed for transactionId={} score={} decision={}",
					event.transactionId(),
					evaluation.getFraudScore(),
					evaluation.getDecision());
		} catch (TransactionNotFoundException ex) {
			log.error("Transaction not found during async fraud evaluation: {}", event.transactionId(), ex);
		} catch (TransactionAlreadyEvaluatedException ex) {
			log.warn("Transaction already evaluated (idempotent skip): {}", event.transactionId());
		}
	}
}

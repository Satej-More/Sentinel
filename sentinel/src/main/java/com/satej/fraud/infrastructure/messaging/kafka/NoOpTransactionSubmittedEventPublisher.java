package com.satej.fraud.infrastructure.messaging.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.satej.fraud.application.port.out.TransactionSubmittedEventPublisher;
import com.satej.fraud.domain.event.TransactionSubmittedEvent;

@Component
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "false", matchIfMissing = true)
public class NoOpTransactionSubmittedEventPublisher implements TransactionSubmittedEventPublisher {

	private static final Logger log = LoggerFactory.getLogger(NoOpTransactionSubmittedEventPublisher.class);

	@Override
	public void publish(TransactionSubmittedEvent event) {
		log.debug("Kafka disabled — skipping TransactionSubmittedEvent for transaction {}", event.transactionId());
	}
}

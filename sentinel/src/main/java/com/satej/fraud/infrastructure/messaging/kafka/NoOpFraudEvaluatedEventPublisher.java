package com.satej.fraud.infrastructure.messaging.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.satej.fraud.application.port.out.FraudEvaluatedEventPublisher;
import com.satej.fraud.domain.event.FraudEvaluatedEvent;

@Component
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "false", matchIfMissing = true)
public class NoOpFraudEvaluatedEventPublisher implements FraudEvaluatedEventPublisher {

	private static final Logger log = LoggerFactory.getLogger(NoOpFraudEvaluatedEventPublisher.class);

	@Override
	public void publish(FraudEvaluatedEvent event) {
		log.debug("Kafka disabled — skipping FraudEvaluatedEvent for transaction {}", event.transactionId());
	}
}

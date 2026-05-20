package com.satej.fraud.infrastructure.messaging.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import com.satej.fraud.application.port.out.TransactionSubmittedEventPublisher;
import com.satej.fraud.config.KafkaTopicProperties;
import com.satej.fraud.domain.event.TransactionSubmittedEvent;

@Component
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true")
public class KafkaTransactionSubmittedEventPublisher implements TransactionSubmittedEventPublisher {

	private static final Logger log = LoggerFactory.getLogger(KafkaTransactionSubmittedEventPublisher.class);

	private final KafkaTemplate<String, Object> kafkaTemplate;
	private final KafkaTopicProperties kafkaTopicProperties;

	public KafkaTransactionSubmittedEventPublisher(
			KafkaTemplate<String, Object> kafkaTemplate,
			KafkaTopicProperties kafkaTopicProperties) {
		this.kafkaTemplate = kafkaTemplate;
		this.kafkaTopicProperties = kafkaTopicProperties;
	}

	@Override
	public void publish(TransactionSubmittedEvent event) {
		String topic = kafkaTopicProperties.getTransactionSubmittedTopic();
		var message = MessageBuilder.withPayload(event)
				.setHeader(KafkaHeaders.TOPIC, topic)
				.setHeader(KafkaHeaders.KEY, event.transactionId().toString())
				.setHeader(KafkaEventHeaders.EVENT_TYPE, KafkaEventHeaders.TRANSACTION_SUBMITTED)
				.build();

		kafkaTemplate.send(message).whenComplete((result, ex) -> {
			if (ex != null) {
				log.error("Failed to publish TransactionSubmittedEvent for transaction {}", event.transactionId(), ex);
			} else {
				log.info(
						"Published TransactionSubmittedEvent to topic={} transactionId={} offset={}",
						topic,
						event.transactionId(),
						result.getRecordMetadata().offset());
			}
		});
	}
}

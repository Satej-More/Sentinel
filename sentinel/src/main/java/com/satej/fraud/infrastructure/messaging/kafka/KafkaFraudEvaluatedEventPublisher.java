package com.satej.fraud.infrastructure.messaging.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import com.satej.fraud.application.port.out.FraudEvaluatedEventPublisher;
import com.satej.fraud.config.KafkaTopicProperties;
import com.satej.fraud.domain.event.FraudEvaluatedEvent;

@Component
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true")
public class KafkaFraudEvaluatedEventPublisher implements FraudEvaluatedEventPublisher {

	private static final Logger log = LoggerFactory.getLogger(KafkaFraudEvaluatedEventPublisher.class);

	private final KafkaTemplate<String, Object> kafkaTemplate;
	private final KafkaTopicProperties kafkaTopicProperties;

	public KafkaFraudEvaluatedEventPublisher(
			KafkaTemplate<String, Object> kafkaTemplate,
			KafkaTopicProperties kafkaTopicProperties) {
		this.kafkaTemplate = kafkaTemplate;
		this.kafkaTopicProperties = kafkaTopicProperties;
	}

	@Override
	public void publish(FraudEvaluatedEvent event) {
		String topic = kafkaTopicProperties.getFraudEvaluatedTopic();
		var message = MessageBuilder.withPayload(event)
				.setHeader(KafkaHeaders.TOPIC, topic)
				.setHeader(KafkaHeaders.KEY, event.transactionId().toString())
				.setHeader(KafkaEventHeaders.EVENT_TYPE, KafkaEventHeaders.FRAUD_EVALUATED)
				.build();

		kafkaTemplate.send(message).whenComplete((result, ex) -> {
			if (ex != null) {
				log.error("Failed to publish FraudEvaluatedEvent for transaction {}", event.transactionId(), ex);
			} else {
				log.info(
						"Published FraudEvaluatedEvent to topic={} transactionId={} decision={} offset={}",
						topic,
						event.transactionId(),
						event.decision(),
						result.getRecordMetadata().offset());
			}
		});
	}
}

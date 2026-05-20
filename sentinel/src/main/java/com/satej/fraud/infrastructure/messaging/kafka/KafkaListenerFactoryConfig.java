package com.satej.fraud.infrastructure.messaging.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import com.satej.fraud.domain.event.TransactionSubmittedEvent;

@Configuration
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true")
public class KafkaListenerFactoryConfig {

	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;

	@Value("${spring.kafka.consumer.group-id}")
	private String consumerGroupId;

	@Bean
	ConsumerFactory<String, TransactionSubmittedEvent> transactionSubmittedConsumerFactory() {
		Map<String, Object> config = new HashMap<>();
		config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		config.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
		config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
		config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);
		config.put(JacksonJsonDeserializer.VALUE_DEFAULT_TYPE, TransactionSubmittedEvent.class);
		config.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, "com.satej.fraud.domain.event");
		config.put(JacksonJsonDeserializer.USE_TYPE_INFO_HEADERS, false);
		return new DefaultKafkaConsumerFactory<>(config);
	}

	@Bean
	ConcurrentKafkaListenerContainerFactory<String, TransactionSubmittedEvent>
			transactionSubmittedKafkaListenerContainerFactory(
					ConsumerFactory<String, TransactionSubmittedEvent> transactionSubmittedConsumerFactory,
					DefaultErrorHandler kafkaErrorHandler) {
		ConcurrentKafkaListenerContainerFactory<String, TransactionSubmittedEvent> factory =
				new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(transactionSubmittedConsumerFactory);
		factory.setCommonErrorHandler(kafkaErrorHandler);
		return factory;
	}
}

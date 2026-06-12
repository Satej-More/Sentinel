package com.satej.fraud.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import com.satej.fraud.domain.exception.TransactionAlreadyEvaluatedException;
import com.satej.fraud.domain.exception.TransactionNotFoundException;

@Configuration
@EnableKafka
@EnableConfigurationProperties(KafkaTopicProperties.class)
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true")
public class KafkaConfig {

	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;

	@Value("${spring.kafka.consumer.group-id}")
	private String consumerGroupId;

	@Value("${KAFKA_SASL_USERNAME:}")
	private String saslUsername;

	@Value("${KAFKA_SASL_PASSWORD:}")
	private String saslPassword;

	/**
	 * Applies SASL/SSL properties required by Upstash Kafka when credentials are present.
	 * When running locally with Docker Kafka, these fields are empty and no SASL config is applied.
	 */
	private void applySaslConfig(Map<String, Object> config) {
		if (saslUsername != null && !saslUsername.isEmpty()) {
			config.put("security.protocol", "SASL_SSL");
			config.put(SaslConfigs.SASL_MECHANISM, "SCRAM-SHA-256");
			config.put(SaslConfigs.SASL_JAAS_CONFIG,
					"org.apache.kafka.common.security.scram.ScramLoginModule required username=\""
							+ saslUsername + "\" password=\"" + saslPassword + "\";");
		}
	}

	@Bean
	KafkaAdmin kafkaAdmin() {
		Map<String, Object> config = new HashMap<>();
		config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		applySaslConfig(config);
		return new KafkaAdmin(config);
	}

	@Bean
	NewTopic transactionSubmittedTopic(KafkaTopicProperties properties) {
		return TopicBuilder.name(properties.getTransactionSubmittedTopic())
				.partitions(3)
				.replicas(1)
				.build();
	}

	@Bean
	NewTopic fraudEvaluatedTopic(KafkaTopicProperties properties) {
		return TopicBuilder.name(properties.getFraudEvaluatedTopic())
				.partitions(3)
				.replicas(1)
				.build();
	}

	@Bean
	ProducerFactory<String, Object> producerFactory() {
		Map<String, Object> config = new HashMap<>();
		config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
		config.put(ProducerConfig.ACKS_CONFIG, "all");
		config.put(ProducerConfig.RETRIES_CONFIG, 3);
		config.put(JacksonJsonSerializer.ADD_TYPE_INFO_HEADERS, false);
		applySaslConfig(config);
		return new DefaultKafkaProducerFactory<>(config);
	}

	@Bean
	KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
		return new KafkaTemplate<>(producerFactory);
	}

	@Bean
	ConsumerFactory<String, Object> consumerFactory() {
		Map<String, Object> config = new HashMap<>();
		config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		config.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
		config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
		config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);
		config.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, "com.satej.fraud.domain.event,com.satej.fraud.domain.fraud.model,com.satej.fraud.domain.model");
		config.put(JacksonJsonDeserializer.USE_TYPE_INFO_HEADERS, false);
		applySaslConfig(config);
		return new DefaultKafkaConsumerFactory<>(config);
	}

	@Bean
	DefaultErrorHandler kafkaErrorHandler() {
		DefaultErrorHandler errorHandler = new DefaultErrorHandler(new FixedBackOff(1000L, 3L));
		errorHandler.addNotRetryableExceptions(
				TransactionNotFoundException.class, TransactionAlreadyEvaluatedException.class);
		return errorHandler;
	}
}


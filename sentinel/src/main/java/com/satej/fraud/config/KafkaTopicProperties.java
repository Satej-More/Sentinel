package com.satej.fraud.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.kafka")
public class KafkaTopicProperties {

	private boolean enabled = false;
	private String transactionSubmittedTopic = "transaction-submitted";
	private String fraudEvaluatedTopic = "fraud-evaluated";

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getTransactionSubmittedTopic() {
		return transactionSubmittedTopic;
	}

	public void setTransactionSubmittedTopic(String transactionSubmittedTopic) {
		this.transactionSubmittedTopic = transactionSubmittedTopic;
	}

	public String getFraudEvaluatedTopic() {
		return fraudEvaluatedTopic;
	}

	public void setFraudEvaluatedTopic(String fraudEvaluatedTopic) {
		this.fraudEvaluatedTopic = fraudEvaluatedTopic;
	}
}

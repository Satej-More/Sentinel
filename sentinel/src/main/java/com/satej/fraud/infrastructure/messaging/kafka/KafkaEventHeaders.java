package com.satej.fraud.infrastructure.messaging.kafka;

public final class KafkaEventHeaders {

	public static final String EVENT_TYPE = "event-type";
	public static final String TRANSACTION_SUBMITTED = "TransactionSubmitted";
	public static final String FRAUD_EVALUATED = "FraudEvaluated";

	private KafkaEventHeaders() {
	}
}

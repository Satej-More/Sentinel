package com.satej.fraud.application.port.out;

import com.satej.fraud.domain.event.FraudEvaluatedEvent;

public interface FraudEvaluatedEventPublisher {

	void publish(FraudEvaluatedEvent event);
}

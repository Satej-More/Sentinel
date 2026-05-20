package com.satej.fraud.application.port.out;

import com.satej.fraud.domain.event.TransactionSubmittedEvent;

public interface TransactionSubmittedEventPublisher {

	void publish(TransactionSubmittedEvent event);
}

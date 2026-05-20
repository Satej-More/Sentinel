package com.satej.fraud.application.port.out;

import java.time.Instant;
import java.util.UUID;

/**
 * Outbound port for real-time transaction velocity.
 * Redis implementation uses sliding windows; JPA fallback uses database counts.
 */
public interface TransactionVelocityPort {

	void recordTransaction(String userId, UUID transactionId, Instant occurredAt, int windowSeconds);

	long countInRollingWindow(String userId, int windowSeconds);
}

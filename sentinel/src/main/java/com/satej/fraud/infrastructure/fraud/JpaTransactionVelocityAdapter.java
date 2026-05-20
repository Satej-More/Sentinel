package com.satej.fraud.infrastructure.fraud;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.satej.fraud.application.port.out.TransactionVelocityPort;
import com.satej.fraud.infrastructure.persistence.adapter.TransactionJpaRepository;

@Component
@ConditionalOnProperty(name = "app.redis.enabled", havingValue = "false", matchIfMissing = true)
public class JpaTransactionVelocityAdapter implements TransactionVelocityPort {

	private static final Logger log = LoggerFactory.getLogger(JpaTransactionVelocityAdapter.class);

	private final TransactionJpaRepository transactionJpaRepository;

	public JpaTransactionVelocityAdapter(TransactionJpaRepository transactionJpaRepository) {
		this.transactionJpaRepository = transactionJpaRepository;
	}

	@Override
	public void recordTransaction(String userId, UUID transactionId, Instant occurredAt, int windowSeconds) {
		log.debug("Redis disabled — velocity record skipped for user={} (DB count used on evaluation)", userId);
	}

	@Override
	public long countInRollingWindow(String userId, int windowSeconds) {
		Instant windowStart = Instant.now().minus(windowSeconds, ChronoUnit.SECONDS);
		return transactionJpaRepository.countByUserIdAndCreatedAtAfter(userId, windowStart);
	}
}

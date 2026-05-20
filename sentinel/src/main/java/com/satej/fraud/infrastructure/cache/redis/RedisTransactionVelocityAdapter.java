package com.satej.fraud.infrastructure.cache.redis;

import java.time.Instant;
import java.util.UUID;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.satej.fraud.application.port.out.TransactionVelocityPort;

@Component
@ConditionalOnProperty(name = "app.redis.enabled", havingValue = "true")
public class RedisTransactionVelocityAdapter implements TransactionVelocityPort {

	private final RedisVelocityTracker velocityTracker;

	public RedisTransactionVelocityAdapter(StringRedisTemplate redisTemplate) {
		this.velocityTracker = new RedisVelocityTracker(redisTemplate);
	}

	@Override
	public void recordTransaction(String userId, UUID transactionId, Instant occurredAt, int windowSeconds) {
		velocityTracker.record(userId, transactionId.toString(), occurredAt, windowSeconds);
	}

	@Override
	public long countInRollingWindow(String userId, int windowSeconds) {
		return velocityTracker.count(userId, windowSeconds);
	}
}

package com.satej.fraud.infrastructure.cache.redis;

import java.time.Duration;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Sliding-window velocity tracker using Redis sorted sets (score = epoch millis, TTL on key).
 */
public class RedisVelocityTracker {

	private static final Logger log = LoggerFactory.getLogger(RedisVelocityTracker.class);
	private static final String KEY_PREFIX = "fraud:velocity:user:";

	private final StringRedisTemplate redisTemplate;

	public RedisVelocityTracker(StringRedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public void record(String userId, String transactionId, Instant occurredAt, int windowSeconds) {
		String key = key(userId);
		long score = occurredAt.toEpochMilli();
		redisTemplate.opsForZSet().add(key, transactionId, score);
		trimExpired(key, score, windowSeconds);
		redisTemplate.expire(key, Duration.ofSeconds(windowSeconds + 5L));
		log.debug("Recorded velocity for user={} transaction={} windowSeconds={}", userId, transactionId, windowSeconds);
	}

	public long count(String userId, int windowSeconds) {
		String key = key(userId);
		long now = Instant.now().toEpochMilli();
		trimExpired(key, now, windowSeconds);
		Long count = redisTemplate.opsForZSet().zCard(key);
		long result = count == null ? 0L : count;
		log.debug("Velocity count for user={} in last {}s = {}", userId, windowSeconds, result);
		return result;
	}

	private void trimExpired(String key, long referenceScore, int windowSeconds) {
		long minScore = referenceScore - (windowSeconds * 1000L);
		redisTemplate.opsForZSet().removeRangeByScore(key, 0, minScore - 1);
	}

	private String key(String userId) {
		return KEY_PREFIX + userId;
	}
}

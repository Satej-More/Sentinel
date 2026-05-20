package com.satej.fraud.infrastructure.cache.redis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

@ExtendWith(MockitoExtension.class)
class RedisVelocityTrackerTest {

	@Mock
	private StringRedisTemplate redisTemplate;

	@Mock
	private ZSetOperations<String, String> zSetOperations;

	private RedisVelocityTracker tracker;

	@BeforeEach
	void setUp() {
		when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
		tracker = new RedisVelocityTracker(redisTemplate);
	}

	@Test
	void record_addsToSortedSetAndSetsTtl() {
		Instant at = Instant.parse("2026-05-18T10:00:00Z");

		tracker.record("user-1", "txn-1", at, 30);

		verify(zSetOperations).add("fraud:velocity:user:user-1", "txn-1", at.toEpochMilli());
		verify(zSetOperations).removeRangeByScore(eq("fraud:velocity:user:user-1"), eq(0.0), anyDouble());
		verify(redisTemplate).expire(eq("fraud:velocity:user:user-1"), eq(Duration.ofSeconds(35)));
	}

	@Test
	void count_returnsSortedSetCardinalityAfterTrim() {
		when(zSetOperations.zCard("fraud:velocity:user:user-2")).thenReturn(6L);

		long count = tracker.count("user-2", 30);

		assertEquals(6L, count);
		verify(zSetOperations).removeRangeByScore(eq("fraud:velocity:user:user-2"), eq(0.0), anyDouble());
	}
}

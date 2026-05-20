package com.satej.fraud.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
@EnableConfigurationProperties(RedisProperties.class)
@ConditionalOnProperty(name = "app.redis.enabled", havingValue = "true")
public class RedisConfig {

	@Bean
	RedisConnectionFactory redisConnectionFactory(
			@org.springframework.beans.factory.annotation.Value("${spring.data.redis.host:localhost}") String host,
			@org.springframework.beans.factory.annotation.Value("${spring.data.redis.port:6379}") int port) {
		return new LettuceConnectionFactory(host, port);
	}

	@Bean
	StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
		return new StringRedisTemplate(redisConnectionFactory);
	}
}

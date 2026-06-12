package com.satej.fraud.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
@EnableConfigurationProperties(RedisProperties.class)
@ConditionalOnProperty(name = "app.redis.enabled", havingValue = "true")
public class RedisConfig {

	@Value("${spring.data.redis.host:localhost}")
	private String host;

	@Value("${spring.data.redis.port:6379}")
	private int port;

	@Value("${REDIS_PASSWORD:}")
	private String password;

	@Value("${REDIS_TLS_ENABLED:false}")
	private boolean tlsEnabled;

	@Bean
	RedisConnectionFactory redisConnectionFactory() {
		RedisStandaloneConfiguration serverConfig = new RedisStandaloneConfiguration(host, port);
		if (password != null && !password.isEmpty()) {
			serverConfig.setPassword(password);
		}

		if (tlsEnabled) {
			LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
					.useSsl()
					.build();
			return new LettuceConnectionFactory(serverConfig, clientConfig);
		}

		return new LettuceConnectionFactory(serverConfig);
	}

	@Bean
	StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
		return new StringRedisTemplate(redisConnectionFactory);
	}
}


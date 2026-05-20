package com.satej.fraud.config;

import java.util.List;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.satej.fraud.domain.fraud.engine.FraudDecisionPolicy;
import com.satej.fraud.domain.fraud.engine.FraudRuleEngine;
import com.satej.fraud.domain.fraud.rule.BlacklistedMerchantFraudRule;
import com.satej.fraud.domain.fraud.rule.FraudRule;
import com.satej.fraud.domain.fraud.rule.LargeAmountFraudRule;
import com.satej.fraud.domain.fraud.rule.RapidTransactionFraudRule;
import com.satej.fraud.domain.fraud.rule.SuspiciousIpFraudRule;

@Configuration
@EnableConfigurationProperties(FraudProperties.class)
public class FraudEngineConfig {

	@Bean
	FraudDecisionPolicy fraudDecisionPolicy(FraudProperties properties) {
		return new FraudDecisionPolicy(properties.getApproveMaxScore(), properties.getReviewMaxScore());
	}

	@Bean
	List<FraudRule> fraudRules(FraudProperties properties) {
		return List.of(
				new LargeAmountFraudRule(properties.getLargeAmountRuleScore()),
				new RapidTransactionFraudRule(properties.getRapidTransactionRuleScore()),
				new SuspiciousIpFraudRule(properties.getSuspiciousIpRuleScore()),
				new BlacklistedMerchantFraudRule(properties.getBlacklistedMerchantRuleScore()));
	}

	@Bean
	FraudRuleEngine fraudRuleEngine(List<FraudRule> fraudRules, FraudDecisionPolicy fraudDecisionPolicy) {
		return new FraudRuleEngine(fraudRules, fraudDecisionPolicy);
	}
}

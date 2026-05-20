package com.satej.fraud.domain.fraud.rule;

import java.util.List;
import java.util.Optional;

import com.satej.fraud.domain.fraud.model.FraudEvaluationContext;
import com.satej.fraud.domain.fraud.model.FraudReason;
import com.satej.fraud.domain.fraud.model.FraudRuleHit;

public class RapidTransactionFraudRule implements FraudRule {

	public static final String CODE = "RAPID_TRANSACTIONS";

	private final int scoreContribution;

	public RapidTransactionFraudRule(int scoreContribution) {
		this.scoreContribution = scoreContribution;
	}

	@Override
	public String getCode() {
		return CODE;
	}

	@Override
	public Optional<FraudRuleHit> evaluate(FraudEvaluationContext context) {
		if (context.recentUserTransactionCount() <= context.velocityThreshold()) {
			return Optional.empty();
		}

		String message = "User %s submitted %d transactions within %d seconds (limit: more than %d)"
				.formatted(
						context.transaction().getUserId(),
						context.recentUserTransactionCount(),
						context.velocityWindowSeconds(),
						context.velocityThreshold());
		return Optional.of(new FraudRuleHit(
				CODE,
				scoreContribution,
				List.of(new FraudReason(CODE, message))));
	}
}

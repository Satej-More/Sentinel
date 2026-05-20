package com.satej.fraud.domain.fraud.rule;

import java.util.List;
import java.util.Optional;

import com.satej.fraud.domain.fraud.model.FraudEvaluationContext;
import com.satej.fraud.domain.fraud.model.FraudReason;
import com.satej.fraud.domain.fraud.model.FraudRuleHit;

public class BlacklistedMerchantFraudRule implements FraudRule {

	public static final String CODE = "BLACKLISTED_MERCHANT";

	private final int scoreContribution;

	public BlacklistedMerchantFraudRule(int scoreContribution) {
		this.scoreContribution = scoreContribution;
	}

	@Override
	public String getCode() {
		return CODE;
	}

	@Override
	public Optional<FraudRuleHit> evaluate(FraudEvaluationContext context) {
		if (!context.merchantBlacklisted()) {
			return Optional.empty();
		}

		String message = "Merchant %s is blacklisted".formatted(context.transaction().getMerchantId());
		return Optional.of(new FraudRuleHit(
				CODE,
				scoreContribution,
				List.of(new FraudReason(CODE, message))));
	}
}

package com.satej.fraud.domain.fraud.rule;

import java.util.List;
import java.util.Optional;

import com.satej.fraud.domain.fraud.model.FraudEvaluationContext;
import com.satej.fraud.domain.fraud.model.FraudReason;
import com.satej.fraud.domain.fraud.model.FraudRuleHit;

public class LargeAmountFraudRule implements FraudRule {

	public static final String CODE = "LARGE_AMOUNT";

	private final int scoreContribution;

	public LargeAmountFraudRule(int scoreContribution) {
		this.scoreContribution = scoreContribution;
	}

	@Override
	public String getCode() {
		return CODE;
	}

	@Override
	public Optional<FraudRuleHit> evaluate(FraudEvaluationContext context) {
		if (context.transaction().getAmount().compareTo(context.largeAmountThreshold()) <= 0) {
			return Optional.empty();
		}

		String message = "Transaction amount %s exceeds threshold %s"
				.formatted(context.transaction().getAmount(), context.largeAmountThreshold());
		return Optional.of(new FraudRuleHit(
				CODE,
				scoreContribution,
				List.of(new FraudReason(CODE, message))));
	}
}

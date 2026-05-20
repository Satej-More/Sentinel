package com.satej.fraud.domain.fraud.rule;

import java.util.List;
import java.util.Optional;

import com.satej.fraud.domain.fraud.model.FraudEvaluationContext;
import com.satej.fraud.domain.fraud.model.FraudReason;
import com.satej.fraud.domain.fraud.model.FraudRuleHit;

public class SuspiciousIpFraudRule implements FraudRule {

	public static final String CODE = "SUSPICIOUS_IP";

	private final int scoreContribution;

	public SuspiciousIpFraudRule(int scoreContribution) {
		this.scoreContribution = scoreContribution;
	}

	@Override
	public String getCode() {
		return CODE;
	}

	@Override
	public Optional<FraudRuleHit> evaluate(FraudEvaluationContext context) {
		if (!context.ipSuspicious()) {
			return Optional.empty();
		}

		String ip = context.transaction().getIpAddress();
		String message = "Transaction originated from suspicious IP: " + ip;
		return Optional.of(new FraudRuleHit(
				CODE,
				scoreContribution,
				List.of(new FraudReason(CODE, message))));
	}
}

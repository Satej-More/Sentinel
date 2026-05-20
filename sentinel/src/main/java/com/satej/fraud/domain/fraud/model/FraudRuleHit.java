package com.satej.fraud.domain.fraud.model;

import java.util.List;

public record FraudRuleHit(String ruleCode, int scoreContribution, List<FraudReason> reasons) {

	public FraudRuleHit {
		reasons = List.copyOf(reasons);
	}
}

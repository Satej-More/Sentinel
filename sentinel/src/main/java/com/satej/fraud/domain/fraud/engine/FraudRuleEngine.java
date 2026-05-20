package com.satej.fraud.domain.fraud.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.satej.fraud.domain.fraud.model.FraudEvaluation;
import com.satej.fraud.domain.fraud.model.FraudEvaluationContext;
import com.satej.fraud.domain.fraud.model.FraudReason;
import com.satej.fraud.domain.fraud.model.FraudRuleHit;
import com.satej.fraud.domain.fraud.rule.FraudRule;
import com.satej.fraud.domain.model.TransactionStatus;

/**
 * Pure domain engine: runs all rules, aggregates score (0–100), and applies decision policy.
 * ML scoring can be added later as another {@link FraudRule} implementation.
 */
public class FraudRuleEngine {

	private final List<FraudRule> rules;
	private final FraudDecisionPolicy decisionPolicy;

	public FraudRuleEngine(List<FraudRule> rules, FraudDecisionPolicy decisionPolicy) {
		this.rules = List.copyOf(rules);
		this.decisionPolicy = decisionPolicy;
	}

	public FraudEvaluation evaluate(FraudEvaluationContext context) {
		List<FraudReason> reasons = new ArrayList<>();
		int totalScore = 0;

		for (FraudRule rule : rules) {
			Optional<FraudRuleHit> hit = rule.evaluate(context);
			if (hit.isPresent()) {
				totalScore += hit.get().scoreContribution();
				reasons.addAll(hit.get().reasons());
			}
		}

		int normalizedScore = Math.min(totalScore, 100);
		TransactionStatus decision = decisionPolicy.determineDecision(normalizedScore);

		return FraudEvaluation.create(context.transaction().getId(), normalizedScore, decision, reasons);
	}
}

package com.satej.fraud.domain.fraud.rule;

import java.util.Optional;

import com.satej.fraud.domain.fraud.model.FraudEvaluationContext;
import com.satej.fraud.domain.fraud.model.FraudRuleHit;

/**
 * Pluggable fraud rule. New rules (e.g. ML-based) implement this interface without changing the engine.
 */
public interface FraudRule {

	String getCode();

	Optional<FraudRuleHit> evaluate(FraudEvaluationContext context);
}

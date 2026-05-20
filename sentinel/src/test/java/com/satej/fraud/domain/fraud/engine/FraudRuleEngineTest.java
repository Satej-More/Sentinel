package com.satej.fraud.domain.fraud.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.satej.fraud.domain.fraud.model.FraudEvaluation;
import com.satej.fraud.domain.fraud.model.FraudEvaluationContext;
import com.satej.fraud.domain.fraud.rule.BlacklistedMerchantFraudRule;
import com.satej.fraud.domain.fraud.rule.LargeAmountFraudRule;
import com.satej.fraud.domain.fraud.rule.RapidTransactionFraudRule;
import com.satej.fraud.domain.fraud.rule.SuspiciousIpFraudRule;
import com.satej.fraud.domain.model.Transaction;
import com.satej.fraud.domain.model.TransactionStatus;

class FraudRuleEngineTest {

	private FraudRuleEngine fraudRuleEngine;

	@BeforeEach
	void setUp() {
		FraudDecisionPolicy policy = new FraudDecisionPolicy(29, 69);
		fraudRuleEngine = new FraudRuleEngine(
				List.of(
						new LargeAmountFraudRule(40),
						new RapidTransactionFraudRule(35),
						new SuspiciousIpFraudRule(30),
						new BlacklistedMerchantFraudRule(50)),
				policy);
	}

	@Test
	void evaluate_cleanTransaction_returnsApprovedWithZeroScore() {
		Transaction transaction = Transaction.createNew(
				"txn-clean", "user-1", new BigDecimal("50.00"), "USD", "merchant-ok", "8.8.8.8", null);
		FraudEvaluationContext context = new FraudEvaluationContext(
				transaction, 3, false, false, new BigDecimal("1000.00"), 5, 30);

		FraudEvaluation evaluation = fraudRuleEngine.evaluate(context);

		assertEquals(0, evaluation.getFraudScore());
		assertEquals(TransactionStatus.APPROVED, evaluation.getDecision());
		assertTrue(evaluation.getReasons().isEmpty());
	}

	@Test
	void evaluate_rapidVelocity_returnsUnderReview() {
		Transaction transaction = Transaction.createNew(
				"txn-rapid", "user-rapid", new BigDecimal("10.00"), "USD", "merchant-ok", null, null);
		FraudEvaluationContext context = new FraudEvaluationContext(
				transaction, 6, false, false, new BigDecimal("1000.00"), 5, 30);

		FraudEvaluation evaluation = fraudRuleEngine.evaluate(context);

		assertEquals(35, evaluation.getFraudScore());
		assertEquals(TransactionStatus.UNDER_REVIEW, evaluation.getDecision());
		assertEquals(RapidTransactionFraudRule.CODE, evaluation.getReasons().get(0).ruleCode());
	}

	@Test
	void evaluate_blacklistedMerchant_returnsUnderReviewScore() {
		Transaction transaction = Transaction.createNew(
				"txn-bad-merchant", "user-1", new BigDecimal("50.00"), "USD", "merchant-blocked", null, null);
		FraudEvaluationContext context = new FraudEvaluationContext(
				transaction, 1, true, false, new BigDecimal("1000.00"), 5, 30);

		FraudEvaluation evaluation = fraudRuleEngine.evaluate(context);

		assertEquals(50, evaluation.getFraudScore());
		assertEquals(TransactionStatus.UNDER_REVIEW, evaluation.getDecision());
		assertEquals(1, evaluation.getReasons().size());
	}

	@Test
	void evaluate_multipleHits_capsScoreAt100() {
		Transaction transaction = Transaction.createNew(
				"txn-multi", "user-1", new BigDecimal("50000.00"), "USD", "merchant-blocked", "10.0.0.1", null);
		FraudEvaluationContext context = new FraudEvaluationContext(
				transaction, 10, true, true, new BigDecimal("1000.00"), 3, 30);

		FraudEvaluation evaluation = fraudRuleEngine.evaluate(context);

		assertEquals(100, evaluation.getFraudScore());
		assertEquals(TransactionStatus.REJECTED, evaluation.getDecision());
		assertTrue(evaluation.getReasons().size() >= 3);
	}
}

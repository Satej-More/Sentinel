package com.satej.fraud.domain.fraud.engine;

import com.satej.fraud.domain.model.TransactionStatus;

public class FraudDecisionPolicy {

	private final int approveMaxScore;
	private final int reviewMaxScore;

	public FraudDecisionPolicy(int approveMaxScore, int reviewMaxScore) {
		if (approveMaxScore >= reviewMaxScore) {
			throw new IllegalArgumentException("approveMaxScore must be less than reviewMaxScore");
		}
		this.approveMaxScore = approveMaxScore;
		this.reviewMaxScore = reviewMaxScore;
	}

	public TransactionStatus determineDecision(int fraudScore) {
		if (fraudScore <= approveMaxScore) {
			return TransactionStatus.APPROVED;
		}
		if (fraudScore <= reviewMaxScore) {
			return TransactionStatus.UNDER_REVIEW;
		}
		return TransactionStatus.REJECTED;
	}
}

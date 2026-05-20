package com.satej.fraud.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class FraudReasonEmbeddable {

	@Column(name = "rule_code", nullable = false, length = 64)
	private String ruleCode;

	@Column(nullable = false, length = 512)
	private String message;

	protected FraudReasonEmbeddable() {
	}

	public FraudReasonEmbeddable(String ruleCode, String message) {
		this.ruleCode = ruleCode;
		this.message = message;
	}

	public String getRuleCode() {
		return ruleCode;
	}

	public String getMessage() {
		return message;
	}
}

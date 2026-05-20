package com.satej.fraud.config;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.fraud")
public class FraudProperties {

	private BigDecimal largeAmountThreshold = new BigDecimal("10000.00");
	private int rapidTransactionCountThreshold = 5;
	private int velocityWindowSeconds = 30;
	private int rapidTransactionWindowMinutes = 10;
	private int approveMaxScore = 29;
	private int reviewMaxScore = 69;
	private int largeAmountRuleScore = 40;
	private int rapidTransactionRuleScore = 35;
	private int suspiciousIpRuleScore = 30;
	private int blacklistedMerchantRuleScore = 50;
	private List<String> blacklistedMerchants = new ArrayList<>();
	private List<String> suspiciousIps = new ArrayList<>();

	public BigDecimal getLargeAmountThreshold() {
		return largeAmountThreshold;
	}

	public void setLargeAmountThreshold(BigDecimal largeAmountThreshold) {
		this.largeAmountThreshold = largeAmountThreshold;
	}

	public int getRapidTransactionCountThreshold() {
		return rapidTransactionCountThreshold;
	}

	public void setRapidTransactionCountThreshold(int rapidTransactionCountThreshold) {
		this.rapidTransactionCountThreshold = rapidTransactionCountThreshold;
	}

	public int getVelocityWindowSeconds() {
		return velocityWindowSeconds;
	}

	public void setVelocityWindowSeconds(int velocityWindowSeconds) {
		this.velocityWindowSeconds = velocityWindowSeconds;
	}

	public int getRapidTransactionWindowMinutes() {
		return rapidTransactionWindowMinutes;
	}

	public void setRapidTransactionWindowMinutes(int rapidTransactionWindowMinutes) {
		this.rapidTransactionWindowMinutes = rapidTransactionWindowMinutes;
	}

	public int getApproveMaxScore() {
		return approveMaxScore;
	}

	public void setApproveMaxScore(int approveMaxScore) {
		this.approveMaxScore = approveMaxScore;
	}

	public int getReviewMaxScore() {
		return reviewMaxScore;
	}

	public void setReviewMaxScore(int reviewMaxScore) {
		this.reviewMaxScore = reviewMaxScore;
	}

	public int getLargeAmountRuleScore() {
		return largeAmountRuleScore;
	}

	public void setLargeAmountRuleScore(int largeAmountRuleScore) {
		this.largeAmountRuleScore = largeAmountRuleScore;
	}

	public int getRapidTransactionRuleScore() {
		return rapidTransactionRuleScore;
	}

	public void setRapidTransactionRuleScore(int rapidTransactionRuleScore) {
		this.rapidTransactionRuleScore = rapidTransactionRuleScore;
	}

	public int getSuspiciousIpRuleScore() {
		return suspiciousIpRuleScore;
	}

	public void setSuspiciousIpRuleScore(int suspiciousIpRuleScore) {
		this.suspiciousIpRuleScore = suspiciousIpRuleScore;
	}

	public int getBlacklistedMerchantRuleScore() {
		return blacklistedMerchantRuleScore;
	}

	public void setBlacklistedMerchantRuleScore(int blacklistedMerchantRuleScore) {
		this.blacklistedMerchantRuleScore = blacklistedMerchantRuleScore;
	}

	public List<String> getBlacklistedMerchants() {
		return blacklistedMerchants;
	}

	public void setBlacklistedMerchants(List<String> blacklistedMerchants) {
		this.blacklistedMerchants = blacklistedMerchants;
	}

	public List<String> getSuspiciousIps() {
		return suspiciousIps;
	}

	public void setSuspiciousIps(List<String> suspiciousIps) {
		this.suspiciousIps = suspiciousIps;
	}
}

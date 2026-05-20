package com.satej.fraud.application.port.out;

/**
 * Outbound port for reference lists (blacklists). Can be backed by DB, config, or external service.
 */
public interface FraudReferenceDataPort {

	boolean isMerchantBlacklisted(String merchantId);

	boolean isIpSuspicious(String ipAddress);
}

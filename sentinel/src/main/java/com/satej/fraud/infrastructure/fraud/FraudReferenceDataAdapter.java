package com.satej.fraud.infrastructure.fraud;

import org.springframework.stereotype.Component;

import com.satej.fraud.application.port.out.FraudReferenceDataPort;
import com.satej.fraud.config.FraudProperties;

@Component
public class FraudReferenceDataAdapter implements FraudReferenceDataPort {

	private final FraudProperties fraudProperties;

	public FraudReferenceDataAdapter(FraudProperties fraudProperties) {
		this.fraudProperties = fraudProperties;
	}

	@Override
	public boolean isMerchantBlacklisted(String merchantId) {
		if (merchantId == null || merchantId.isBlank()) {
			return false;
		}
		return fraudProperties.getBlacklistedMerchants().stream()
				.anyMatch(blocked -> blocked.equalsIgnoreCase(merchantId.trim()));
	}

	@Override
	public boolean isIpSuspicious(String ipAddress) {
		if (ipAddress == null || ipAddress.isBlank()) {
			return false;
		}
		return fraudProperties.getSuspiciousIps().stream()
				.anyMatch(suspicious -> suspicious.equalsIgnoreCase(ipAddress.trim()));
	}
}

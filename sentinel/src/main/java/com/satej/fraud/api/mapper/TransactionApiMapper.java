package com.satej.fraud.api.mapper;

import org.springframework.stereotype.Component;

import com.satej.fraud.api.dto.request.SubmitTransactionRequest;
import com.satej.fraud.api.dto.response.TransactionResponse;
import com.satej.fraud.application.port.in.SubmitTransactionCommand;
import com.satej.fraud.domain.model.Transaction;

@Component
public class TransactionApiMapper {

	public SubmitTransactionCommand toCommand(SubmitTransactionRequest request) {
		return new SubmitTransactionCommand(
				request.externalTransactionId(),
				request.userId(),
				request.amount(),
				request.currency(),
				request.merchantId(),
				request.ipAddress(),
				request.deviceId());
	}

	public TransactionResponse toResponse(Transaction transaction) {
		return new TransactionResponse(
				transaction.getId(),
				transaction.getExternalTransactionId(),
				transaction.getUserId(),
				transaction.getAmount(),
				transaction.getCurrency(),
				transaction.getMerchantId(),
				transaction.getIpAddress(),
				transaction.getDeviceId(),
				transaction.getStatus(),
				transaction.getCreatedAt());
	}
}

package com.satej.fraud.infrastructure.persistence.adapter;

import org.springframework.stereotype.Component;

import com.satej.fraud.domain.model.Transaction;
import com.satej.fraud.infrastructure.persistence.entity.TransactionJpaEntity;

@Component
public class TransactionPersistenceMapper {

	public TransactionJpaEntity toEntity(Transaction transaction) {
		TransactionJpaEntity entity = new TransactionJpaEntity();
		entity.setId(transaction.getId());
		entity.setExternalTransactionId(transaction.getExternalTransactionId());
		entity.setUserId(transaction.getUserId());
		entity.setAmount(transaction.getAmount());
		entity.setCurrency(transaction.getCurrency());
		entity.setMerchantId(transaction.getMerchantId());
		entity.setIpAddress(transaction.getIpAddress());
		entity.setDeviceId(transaction.getDeviceId());
		entity.setStatus(transaction.getStatus());
		entity.setCreatedAt(transaction.getCreatedAt());
		return entity;
	}

	public Transaction toDomain(TransactionJpaEntity entity) {
		return Transaction.restore(
				entity.getId(),
				entity.getExternalTransactionId(),
				entity.getUserId(),
				entity.getAmount(),
				entity.getCurrency(),
				entity.getMerchantId(),
				entity.getIpAddress(),
				entity.getDeviceId(),
				entity.getStatus(),
				entity.getCreatedAt());
	}
}

package com.satej.fraud.application.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.satej.fraud.api.dto.response.FraudReasonResponse;
import com.satej.fraud.api.dto.response.TransactionDetailsResponse;
import com.satej.fraud.application.port.in.ListTransactionsUseCase;
import com.satej.fraud.application.port.out.FraudEvaluationRepository;
import com.satej.fraud.application.port.out.TransactionRepository;
import com.satej.fraud.domain.fraud.model.FraudEvaluation;
import com.satej.fraud.domain.model.Transaction;

@Service
public class ListTransactionsService implements ListTransactionsUseCase {

	private final TransactionRepository transactionRepository;
	private final FraudEvaluationRepository fraudEvaluationRepository;

	public ListTransactionsService(
			TransactionRepository transactionRepository,
			FraudEvaluationRepository fraudEvaluationRepository) {
		this.transactionRepository = transactionRepository;
		this.fraudEvaluationRepository = fraudEvaluationRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<TransactionDetailsResponse> listAll() {
		List<Transaction> transactions = transactionRepository.findAll();
		return transactions.stream().map(this::mapToDetails).collect(Collectors.toList());
	}

	private TransactionDetailsResponse mapToDetails(Transaction transaction) {
		Optional<FraudEvaluation> evaluationOpt = fraudEvaluationRepository.findByTransactionId(transaction.getId());

		Integer fraudScore = null;
		List<FraudReasonResponse> reasons = Collections.emptyList();

		if (evaluationOpt.isPresent()) {
			FraudEvaluation evaluation = evaluationOpt.get();
			fraudScore = evaluation.getFraudScore();
			reasons = evaluation.getReasons().stream()
					.map(r -> new FraudReasonResponse(r.ruleCode(), r.message()))
					.collect(Collectors.toList());
		}

		return new TransactionDetailsResponse(
				transaction.getId(),
				transaction.getExternalTransactionId(),
				transaction.getUserId(),
				transaction.getAmount(),
				transaction.getCurrency(),
				transaction.getMerchantId(),
				transaction.getIpAddress(),
				transaction.getDeviceId(),
				transaction.getStatus(),
				transaction.getCreatedAt(),
				fraudScore,
				reasons);
	}
}

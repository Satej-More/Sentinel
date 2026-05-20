package com.satej.fraud.api.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.satej.fraud.api.dto.request.SubmitTransactionRequest;
import com.satej.fraud.api.dto.response.FraudEvaluationResponse;
import com.satej.fraud.api.dto.response.TransactionResponse;
import com.satej.fraud.api.mapper.FraudApiMapper;
import com.satej.fraud.api.mapper.TransactionApiMapper;
import com.satej.fraud.application.port.in.EvaluateTransactionFraudUseCase;
import com.satej.fraud.application.port.in.GetTransactionByExternalIdUseCase;
import com.satej.fraud.application.port.in.GetTransactionByIdUseCase;
import com.satej.fraud.application.port.in.SubmitTransactionCommand;
import com.satej.fraud.application.port.in.SubmitTransactionUseCase;
import com.satej.fraud.domain.fraud.model.FraudEvaluation;
import com.satej.fraud.domain.model.Transaction;
import com.satej.fraud.application.port.in.ListTransactionsUseCase;
import com.satej.fraud.api.dto.response.TransactionDetailsResponse;
import com.satej.fraud.infrastructure.security.CustomUserDetails;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

	private final SubmitTransactionUseCase submitTransactionUseCase;
	private final GetTransactionByIdUseCase getTransactionByIdUseCase;
	private final GetTransactionByExternalIdUseCase getTransactionByExternalIdUseCase;
	private final EvaluateTransactionFraudUseCase evaluateTransactionFraudUseCase;
	private final ListTransactionsUseCase listTransactionsUseCase;
	private final TransactionApiMapper transactionApiMapper;
	private final FraudApiMapper fraudApiMapper;

	public TransactionController(
			SubmitTransactionUseCase submitTransactionUseCase,
			GetTransactionByIdUseCase getTransactionByIdUseCase,
			GetTransactionByExternalIdUseCase getTransactionByExternalIdUseCase,
			EvaluateTransactionFraudUseCase evaluateTransactionFraudUseCase,
			ListTransactionsUseCase listTransactionsUseCase,
			TransactionApiMapper transactionApiMapper,
			FraudApiMapper fraudApiMapper) {
		this.submitTransactionUseCase = submitTransactionUseCase;
		this.getTransactionByIdUseCase = getTransactionByIdUseCase;
		this.getTransactionByExternalIdUseCase = getTransactionByExternalIdUseCase;
		this.evaluateTransactionFraudUseCase = evaluateTransactionFraudUseCase;
		this.listTransactionsUseCase = listTransactionsUseCase;
		this.transactionApiMapper = transactionApiMapper;
		this.fraudApiMapper = fraudApiMapper;
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('USER', 'ADMIN', 'FRAUD_ANALYST')")
	public ResponseEntity<java.util.List<TransactionDetailsResponse>> getAllTransactions() {
		return ResponseEntity.ok(listTransactionsUseCase.listAll());
	}

	@PostMapping
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public ResponseEntity<TransactionResponse> submitTransaction(
			@Valid @RequestBody SubmitTransactionRequest request,
			@AuthenticationPrincipal CustomUserDetails userDetails) {
		SubmitTransactionCommand mappedCommand = transactionApiMapper.toCommand(request);
		
		SubmitTransactionCommand commandWithAuthUser = new SubmitTransactionCommand(
				mappedCommand.externalTransactionId(),
				userDetails.getUser().getId(),
				mappedCommand.amount(),
				mappedCommand.currency(),
				mappedCommand.merchantId(),
				mappedCommand.ipAddress(),
				mappedCommand.deviceId()
		);

		Transaction transaction = submitTransactionUseCase.submit(commandWithAuthUser);
		return ResponseEntity.status(HttpStatus.CREATED).body(transactionApiMapper.toResponse(transaction));
	}

	@PostMapping("/{id}/evaluate")
	@PreAuthorize("hasAnyRole('ADMIN', 'FRAUD_ANALYST')")
	public ResponseEntity<FraudEvaluationResponse> evaluateTransaction(@PathVariable UUID id) {
		FraudEvaluation evaluation = evaluateTransactionFraudUseCase.evaluate(id);
		return ResponseEntity.ok(fraudApiMapper.toResponse(evaluation));
	}

	@GetMapping("/external/{externalTransactionId}")
	@PreAuthorize("hasAnyRole('USER', 'ADMIN', 'FRAUD_ANALYST')")
	public ResponseEntity<TransactionResponse> getTransactionByExternalId(
			@PathVariable String externalTransactionId) {
		Transaction transaction = getTransactionByExternalIdUseCase.getByExternalTransactionId(externalTransactionId);
		return ResponseEntity.ok(transactionApiMapper.toResponse(transaction));
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('USER', 'ADMIN', 'FRAUD_ANALYST')")
	public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable UUID id) {
		Transaction transaction = getTransactionByIdUseCase.getById(id);
		return ResponseEntity.ok(transactionApiMapper.toResponse(transaction));
	}
}

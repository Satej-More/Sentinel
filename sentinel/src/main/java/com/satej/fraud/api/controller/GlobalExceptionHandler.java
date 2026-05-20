package com.satej.fraud.api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.satej.fraud.api.dto.response.ApiErrorResponse;
import com.satej.fraud.api.dto.response.FieldErrorDetail;
import com.satej.fraud.domain.exception.DuplicateTransactionException;
import com.satej.fraud.domain.exception.TransactionAlreadyEvaluatedException;
import com.satej.fraud.domain.exception.TransactionNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleValidation(
			MethodArgumentNotValidException ex,
			HttpServletRequest request) {
		List<FieldErrorDetail> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
				.map(error -> new FieldErrorDetail(error.getField(), error.getDefaultMessage()))
				.toList();

		ApiErrorResponse body = ApiErrorResponse.of(
				HttpStatus.BAD_REQUEST.value(),
				"Validation Failed",
				"Request body contains invalid fields",
				request.getRequestURI(),
				fieldErrors);

		return ResponseEntity.badRequest().body(body);
	}

	@ExceptionHandler(TransactionNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleNotFound(
			TransactionNotFoundException ex,
			HttpServletRequest request) {
		ApiErrorResponse body = ApiErrorResponse.of(
				HttpStatus.NOT_FOUND.value(),
				"Not Found",
				ex.getMessage(),
				request.getRequestURI(),
				List.of());

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ApiErrorResponse> handleTypeMismatch(
			MethodArgumentTypeMismatchException ex,
			HttpServletRequest request) {
		String message = "Invalid value for parameter '%s'".formatted(ex.getName());
		ApiErrorResponse body = ApiErrorResponse.of(
				HttpStatus.BAD_REQUEST.value(),
				"Bad Request",
				message,
				request.getRequestURI(),
				List.of());

		return ResponseEntity.badRequest().body(body);
	}

	@ExceptionHandler(TransactionAlreadyEvaluatedException.class)
	public ResponseEntity<ApiErrorResponse> handleAlreadyEvaluated(
			TransactionAlreadyEvaluatedException ex,
			HttpServletRequest request) {
		ApiErrorResponse body = ApiErrorResponse.of(
				HttpStatus.CONFLICT.value(),
				"Conflict",
				ex.getMessage(),
				request.getRequestURI(),
				List.of());

		return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
	}

	@ExceptionHandler(DuplicateTransactionException.class)
	public ResponseEntity<ApiErrorResponse> handleDuplicate(
			DuplicateTransactionException ex,
			HttpServletRequest request) {
		ApiErrorResponse body = ApiErrorResponse.of(
				HttpStatus.CONFLICT.value(),
				"Conflict",
				ex.getMessage(),
				request.getRequestURI(),
				List.of());

		return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiErrorResponse> handleIllegalArgument(
			IllegalArgumentException ex,
			HttpServletRequest request) {
		ApiErrorResponse body = ApiErrorResponse.of(
				HttpStatus.BAD_REQUEST.value(),
				"Bad Request",
				ex.getMessage(),
				request.getRequestURI(),
				List.of());

		return ResponseEntity.badRequest().body(body);
	}
}

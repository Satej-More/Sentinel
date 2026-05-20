package com.satej.fraud.application.port.in;

import java.util.UUID;

import com.satej.fraud.domain.model.Transaction;

public interface GetTransactionByIdUseCase {

	Transaction getById(UUID id);
}

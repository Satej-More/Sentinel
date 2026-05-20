package com.satej.fraud.application.port.in;

import com.satej.fraud.domain.model.Transaction;

public interface GetTransactionByExternalIdUseCase {

	Transaction getByExternalTransactionId(String externalTransactionId);
}

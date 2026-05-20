package com.satej.fraud.application.port.in;

import java.util.List;
import com.satej.fraud.api.dto.response.TransactionDetailsResponse;

public interface ListTransactionsUseCase {
	List<TransactionDetailsResponse> listAll();
}

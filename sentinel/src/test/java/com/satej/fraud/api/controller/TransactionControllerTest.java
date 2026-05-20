package com.satej.fraud.api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.satej.fraud.domain.user.Role;
import com.satej.fraud.domain.user.User;
import com.satej.fraud.infrastructure.security.CustomUserDetails;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TransactionControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void submitTransaction_returnsCreated() throws Exception {
		CustomUserDetails customUserDetails = new CustomUserDetails(User.builder().id("user-42").username("user-42").role(Role.USER).password("pass").build());
		String body = """
				{
				  "externalTransactionId": "txn-001",
				  "userId": "user-42",
				  "amount": 150.75,
				  "currency": "usd",
				  "merchantId": "merchant-9",
				  "ipAddress": "203.0.113.10",
				  "deviceId": "device-abc"
				}
				""";

		mockMvc.perform(post("/transactions").with(user(customUserDetails)).contentType(MediaType.APPLICATION_JSON).content(body))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.externalTransactionId").value("txn-001"))
				.andExpect(jsonPath("$.status").value("RECEIVED"))
				.andExpect(jsonPath("$.currency").value("USD"));
	}

	@Test
	void submitTransaction_duplicateExternalId_returnsConflict() throws Exception {
		CustomUserDetails customUserDetails = new CustomUserDetails(User.builder().id("user-1").username("user-1").role(Role.USER).password("pass").build());
		String body = """
				{
				  "externalTransactionId": "txn-dup",
				  "userId": "user-1",
				  "amount": 10.00,
				  "currency": "EUR",
				  "merchantId": "merchant-1"
				}
				""";

		mockMvc.perform(post("/transactions").with(user(customUserDetails)).contentType(MediaType.APPLICATION_JSON).content(body))
				.andExpect(status().isCreated());

		mockMvc.perform(post("/transactions").with(user(customUserDetails)).contentType(MediaType.APPLICATION_JSON).content(body))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.error").value("Conflict"));
	}

	@Test
	void submitTransaction_invalidBody_returnsBadRequest() throws Exception {
		CustomUserDetails customUserDetails = new CustomUserDetails(User.builder().id("user-1").username("user-1").role(Role.USER).password("pass").build());
		String body = """
				{
				  "externalTransactionId": "",
				  "userId": "user-1",
				  "amount": -5,
				  "currency": "US",
				  "merchantId": "merchant-1"
				}
				""";

		mockMvc.perform(post("/transactions").with(user(customUserDetails)).contentType(MediaType.APPLICATION_JSON).content(body))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.fieldErrors").isArray());
	}

	@Test
	void getTransactionById_returnsOk() throws Exception {
		CustomUserDetails customUserDetails = new CustomUserDetails(User.builder().id("user-1").username("user-1").role(Role.USER).password("pass").build());
		String createBody = """
				{
				  "externalTransactionId": "txn-get-by-id",
				  "userId": "user-1",
				  "amount": 99.99,
				  "currency": "INR",
				  "merchantId": "merchant-1"
				}
				""";

		String response = mockMvc
				.perform(post("/transactions").with(user(customUserDetails)).contentType(MediaType.APPLICATION_JSON).content(createBody))
				.andExpect(status().isCreated())
				.andReturn()
				.getResponse()
				.getContentAsString();

		String id = response.replaceAll("(?s).*\"id\"\\s*:\\s*\"([^\"]+)\".*", "$1");

		mockMvc.perform(get("/transactions/{id}", id).with(user(customUserDetails)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.externalTransactionId").value("txn-get-by-id"))
				.andExpect(jsonPath("$.currency").value("INR"));
	}

	@Test
	void getTransactionById_notFound_returns404() throws Exception {
		CustomUserDetails customUserDetails = new CustomUserDetails(User.builder().id("user-1").username("user-1").role(Role.USER).password("pass").build());
		mockMvc.perform(get("/transactions/{id}", "00000000-0000-0000-0000-000000000099").with(user(customUserDetails)))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.error").value("Not Found"));
	}

	@Test
	void getTransactionById_invalidUuid_returnsBadRequest() throws Exception {
		CustomUserDetails customUserDetails = new CustomUserDetails(User.builder().id("user-1").username("user-1").role(Role.USER).password("pass").build());
		mockMvc.perform(get("/transactions/{id}", "not-a-uuid").with(user(customUserDetails)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Invalid value for parameter 'id'"));
	}

	@Test
	void getTransactionByExternalId_returnsOk() throws Exception {
		CustomUserDetails customUserDetails = new CustomUserDetails(User.builder().id("user-2").username("user-2").role(Role.USER).password("pass").build());
		String createBody = """
				{
				  "externalTransactionId": "txn-get-external",
				  "userId": "user-2",
				  "amount": 25.00,
				  "currency": "GBP",
				  "merchantId": "merchant-2"
				}
				""";

		mockMvc.perform(post("/transactions").with(user(customUserDetails)).contentType(MediaType.APPLICATION_JSON).content(createBody))
				.andExpect(status().isCreated());

		mockMvc.perform(get("/transactions/external/{externalTransactionId}", "txn-get-external").with(user(customUserDetails)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.externalTransactionId").value("txn-get-external"))
				.andExpect(jsonPath("$.status").value("RECEIVED"));
	}

	@Test
	void getTransactionByExternalId_notFound_returns404() throws Exception {
		CustomUserDetails customUserDetails = new CustomUserDetails(User.builder().id("user-1").username("user-1").role(Role.USER).password("pass").build());
		mockMvc.perform(get("/transactions/external/{externalTransactionId}", "missing-txn").with(user(customUserDetails)))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Transaction not found with external id: missing-txn"));
	}

	@Test
	void evaluateTransaction_cleanTransaction_returnsApproved() throws Exception {
		CustomUserDetails adminUser = new CustomUserDetails(User.builder().id("admin-1").username("admin").role(Role.ADMIN).password("pass").build());
		String id = createTransaction(
				"txn-eval-clean",
				"user-clean",
				"25.00",
				"USD",
				"merchant-ok",
				"8.8.8.8");

		mockMvc.perform(post("/transactions/{id}/evaluate", id).with(user(adminUser)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.fraudScore").value(0))
				.andExpect(jsonPath("$.decision").value("APPROVED"))
				.andExpect(jsonPath("$.transactionStatus").value("APPROVED"))
				.andExpect(jsonPath("$.reasons").isEmpty());

		mockMvc.perform(get("/transactions/{id}", id).with(user(adminUser)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("APPROVED"));
	}

	@Test
	void evaluateTransaction_blacklistedMerchant_returnsRejected() throws Exception {
		CustomUserDetails adminUser = new CustomUserDetails(User.builder().id("admin-1").username("admin").role(Role.ADMIN).password("pass").build());
		String id = createTransaction(
				"txn-eval-blocked",
				"user-blocked",
				"100.00",
				"USD",
				"merchant-blocked",
				"8.8.8.8");

		mockMvc.perform(post("/transactions/{id}/evaluate", id).with(user(adminUser)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.fraudScore").value(50))
				.andExpect(jsonPath("$.decision").value("REJECTED"))
				.andExpect(jsonPath("$.reasons[0].ruleCode").value("BLACKLISTED_MERCHANT"));
	}

	@Test
	void evaluateTransaction_alreadyEvaluated_returnsConflict() throws Exception {
		CustomUserDetails adminUser = new CustomUserDetails(User.builder().id("admin-1").username("admin").role(Role.ADMIN).password("pass").build());
		String id = createTransaction(
				"txn-eval-dup",
				"user-dup",
				"15.00",
				"USD",
				"merchant-ok",
				null);

		mockMvc.perform(post("/transactions/{id}/evaluate", id).with(user(adminUser))).andExpect(status().isOk());

		mockMvc.perform(post("/transactions/{id}/evaluate", id).with(user(adminUser)))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.error").value("Conflict"));
	}

	@Test
	void evaluateTransaction_notFound_returns404() throws Exception {
		CustomUserDetails adminUser = new CustomUserDetails(User.builder().id("admin-1").username("admin").role(Role.ADMIN).password("pass").build());
		mockMvc.perform(post("/transactions/{id}/evaluate", "00000000-0000-0000-0000-000000000099").with(user(adminUser)))
				.andExpect(status().isNotFound());
	}

	private String createTransaction(
			String externalTransactionId,
			String userId,
			String amount,
			String currency,
			String merchantId,
			String ipAddress)
			throws Exception {
		String ipField = ipAddress == null ? "" : ",\n  \"ipAddress\": \"%s\"".formatted(ipAddress);
		String body = """
				{
				  "externalTransactionId": "%s",
				  "userId": "%s",
				  "amount": %s,
				  "currency": "%s",
				  "merchantId": "%s"%s
				}
				"""
				.formatted(externalTransactionId, userId, amount, currency, merchantId, ipField);

		CustomUserDetails customUserDetails = new CustomUserDetails(User.builder().id(userId).username(userId).role(Role.USER).password("pass").build());
		String response = mockMvc
				.perform(post("/transactions").with(user(customUserDetails)).contentType(MediaType.APPLICATION_JSON).content(body))
				.andExpect(status().isCreated())
				.andReturn()
				.getResponse()
				.getContentAsString();

		return response.replaceAll("(?s).*\"id\"\\s*:\\s*\"([^\"]+)\".*", "$1");
	}
}

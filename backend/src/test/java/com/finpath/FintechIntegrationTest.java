package com.finpath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finpath.modules.fintech.dto.*;
import com.finpath.security.dto.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class FintechIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String authToken;

    @BeforeEach
    void setupToken() throws Exception {
        LoginRequest loginRequest = new LoginRequest("sarah.connor@finpath.ai", "SecurePass123!");
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String token = objectMapper.readTree(result.getResponse().getContentAsString())
                .path("data").path("accessToken").asText();
        if (token == null || token.isEmpty()) {
            token = objectMapper.readTree(result.getResponse().getContentAsString())
                    .path("data").path("token").asText();
        }
        authToken = token;
    }

    @Test
    void testGetAccounts() throws Exception {
        mockMvc.perform(get("/api/v1/fintech/accounts")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data[0].institutionName", notNullValue()));
    }

    @Test
    void testGetTransactions() throws Exception {
        mockMvc.perform(get("/api/v1/fintech/transactions")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data.content[0].merchantName", notNullValue()));
    }

    @Test
    void testGetBudgets() throws Exception {
        mockMvc.perform(get("/api/v1/fintech/budgets")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void testGetCashflowForecast() throws Exception {
        mockMvc.perform(get("/api/v1/fintech/cashflow/forecast")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.projectedNetSavings", notNullValue()));
    }

    @Test
    void testCreateTransaction() throws Exception {
        MvcResult accountsResult = mockMvc.perform(get("/api/v1/fintech/accounts")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andReturn();

        String accountId = objectMapper.readTree(accountsResult.getResponse().getContentAsString())
                .path("data").get(0).path("id").asText();

        CreateTransactionRequest request = new CreateTransactionRequest(
                accountId,
                BigDecimal.valueOf(-45.50),
                LocalDate.now(),
                "Target Store #224",
                "SHOPPING",
                false
        );

        mockMvc.perform(post("/api/v1/fintech/transactions")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.merchantName", is("Target Store #224")))
                .andExpect(jsonPath("$.data.category", is("SHOPPING")));
    }

    @Test
    void testAutoCategorizeMerchant() throws Exception {
        AutoCategorizeRequest request = new AutoCategorizeRequest("Trader Joe's Grocery Market #54", BigDecimal.valueOf(-78.40));

        mockMvc.perform(post("/api/v1/fintech/transactions/auto-categorize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.category", is("GROCERIES")))
                .andExpect(jsonPath("$.data.cleanMerchantName", containsString("Trader Joe's")));
    }

    @Test
    void testUpdateTransactionCategory() throws Exception {
        MvcResult accountsResult = mockMvc.perform(get("/api/v1/fintech/accounts")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andReturn();

        String accountId = objectMapper.readTree(accountsResult.getResponse().getContentAsString())
                .path("data").get(0).path("id").asText();

        CreateTransactionRequest createReq = new CreateTransactionRequest(
                accountId,
                BigDecimal.valueOf(-24.00),
                LocalDate.now(),
                "Local Bookstore",
                "UNCATEGORIZED",
                false
        );

        MvcResult createRes = mockMvc.perform(post("/api/v1/fintech/transactions")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andReturn();

        String txId = objectMapper.readTree(createRes.getResponse().getContentAsString())
                .path("data").path("id").asText();

        UpdateTransactionCategoryRequest updateReq = new UpdateTransactionCategoryRequest("ENTERTAINMENT");

        mockMvc.perform(patch("/api/v1/fintech/transactions/" + txId + "/category")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.category", is("ENTERTAINMENT")));
    }

    @Test
    void testFinancialInsights() throws Exception {
        mockMvc.perform(get("/api/v1/fintech/insights")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.monthlySummary", notNullValue()))
                .andExpect(jsonPath("$.data.monthlySummary.savingsRate", notNullValue()))
                .andExpect(jsonPath("$.data.insights", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.data.historicalMonthlyTrends", hasSize(greaterThan(0))));
    }

    @Test
    void testSavingsGoalsWorkflow() throws Exception {
        CreateSavingsGoalRequest goalReq = new CreateSavingsGoalRequest(
                "Home Renovation Reserve",
                "HOUSING",
                BigDecimal.valueOf(12000),
                BigDecimal.valueOf(3000),
                LocalDate.now().plusMonths(12)
        );

        // 1. Create Goal
        MvcResult createRes = mockMvc.perform(post("/api/v1/fintech/savings-goals")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(goalReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.title", is("Home Renovation Reserve")))
                .andExpect(jsonPath("$.data.monthlyRequiredSavings", notNullValue()))
                .andReturn();

        String goalId = objectMapper.readTree(createRes.getResponse().getContentAsString())
                .path("data").path("id").asText();

        // 2. Get Goals
        mockMvc.perform(get("/api/v1/fintech/savings-goals")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))));

        // 3. Update Saved Amount
        mockMvc.perform(patch("/api/v1/fintech/savings-goals/" + goalId)
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("currentSavedAmount", BigDecimal.valueOf(5000)))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.currentSavedAmount", is(5000)));

        // 4. Delete Goal
        mockMvc.perform(delete("/api/v1/fintech/savings-goals/" + goalId)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk());
    }

    @Test
    void testCsvTransactionImport() throws Exception {
        String csv = "Date,Merchant,Amount,Category\n" +
                "2026-11-01,\"Whole Foods Market #1029\",-112.50,GROCERIES\n" +
                "2026-11-02,\"Shell Oil Station\",-45.00,TRANSPORTATION\n" +
                "2026-11-03,\"Netflix Subscription\",-19.99,ENTERTAINMENT\n";

        CsvImportRequest request = new CsvImportRequest(csv, null);

        mockMvc.perform(post("/api/v1/fintech/transactions/import-csv")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.importedCount", is(3)));
    }

    @Test
    void testFinancialHealthScore() throws Exception {
        mockMvc.perform(get("/api/v1/fintech/health-score")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.overallScore", greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.data.overallScore", lessThanOrEqualTo(100)))
                .andExpect(jsonPath("$.data.healthTier", notNullValue()))
                .andExpect(jsonPath("$.data.scoringFactors", hasSize(5)))
                .andExpect(jsonPath("$.data.improvementAreas", notNullValue()))
                .andExpect(jsonPath("$.data.disclaimer", containsString("DOES NOT constitute a credit score")));
    }

    @Test
    void testSmartExpenseDetection() throws Exception {
        mockMvc.perform(get("/api/v1/fintech/smart-expenses")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.subscriptions", notNullValue()))
                .andExpect(jsonPath("$.data.duplicateSuspects", notNullValue()))
                .andExpect(jsonPath("$.data.unusualExpenses", notNullValue()))
                .andExpect(jsonPath("$.data.categoryIncreases", notNullValue()));
    }

    @Test
    void testConfirmDuplicateTransaction() throws Exception {
        DuplicateConfirmationRequest req = new DuplicateConfirmationRequest(
                java.util.UUID.randomUUID(),
                true
        );

        mockMvc.perform(post("/api/v1/fintech/smart-expenses/confirm-duplicate")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", containsString("confirmed as duplicate")));
    }

    @Test
    void testWhatIfSimulation() throws Exception {
        WhatIfSimulationRequestDto req = new WhatIfSimulationRequestDto(
                BigDecimal.valueOf(5000),
                Map.of("DINING", BigDecimal.valueOf(20)),
                BigDecimal.valueOf(200000),
                36,
                BigDecimal.valueOf(10.5),
                BigDecimal.valueOf(2500),
                "EMERGENCY_FUND",
                BigDecimal.valueOf(500000)
        );

        mockMvc.perform(post("/api/v1/fintech/what-if/simulate")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.baselineIncome", notNullValue()))
                .andExpect(jsonPath("$.data.simulatedMonthlySurplus", notNullValue()))
                .andExpect(jsonPath("$.data.feasibilityVerdict", notNullValue()))
                .andExpect(jsonPath("$.data.impactObservations", notNullValue()));
    }
}

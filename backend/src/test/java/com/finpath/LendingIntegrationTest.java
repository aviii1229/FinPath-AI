package com.finpath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finpath.modules.lending.dto.*;
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
import java.util.Arrays;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LendingIntegrationTest {

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
    void testGetLoanProducts() throws Exception {
        mockMvc.perform(get("/api/v1/lending/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data[0].productName", notNullValue()))
                .andExpect(jsonPath("$.data[0].lenderName", notNullValue()))
                .andExpect(jsonPath("$.data[0].simulated", is(true)));
    }

    @Test
    void testPreQualifyCalculation() throws Exception {
        PreQualifyRequest request = new PreQualifyRequest(
                BigDecimal.valueOf(35000),
                48,
                "AUTO"
        );

        mockMvc.perform(post("/api/v1/lending/eligibility/pre-qualify")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.approved", notNullValue()))
                .andExpect(jsonPath("$.data.estimatedMonthlyPayment", notNullValue()))
                .andExpect(jsonPath("$.data.dtiPercentage", notNullValue()));
    }

    @Test
    void testGetMyLoanApplications() throws Exception {
        mockMvc.perform(get("/api/v1/lending/applications")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data[0].applicationNumber", notNullValue()));
    }

    @Test
    void testCreateLoanApplication() throws Exception {
        MvcResult productsResult = mockMvc.perform(get("/api/v1/lending/products"))
                .andExpect(status().isOk())
                .andReturn();

        String productId = objectMapper.readTree(productsResult.getResponse().getContentAsString())
                .path("data").get(0).path("id").asText();

        CreateLoanApplicationRequest request = new CreateLoanApplicationRequest(
                productId,
                BigDecimal.valueOf(15000),
                36,
                "PERSONAL_CONSOLIDATION"
        );
        request.setEmploymentType("SALARIED");
        request.setMonthlyIncome(BigDecimal.valueOf(6500.00));
        request.setExistingEmis(BigDecimal.valueOf(600.00));

        mockMvc.perform(post("/api/v1/lending/applications")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.applicationNumber", notNullValue()))
                .andExpect(jsonPath("$.data.requestedAmount", notNullValue()))
                .andExpect(jsonPath("$.data.status", is("SUBMITTED")));
    }

    @Test
    void testLoanDiscoveryAndIllustrativeEligibility() throws Exception {
        LoanDiscoveryRequest request = new LoanDiscoveryRequest(
                BigDecimal.valueOf(25000),
                BigDecimal.valueOf(7000),
                "SALARIED",
                BigDecimal.valueOf(500),
                36,
                "PERSONAL",
                740
        );

        mockMvc.perform(post("/api/v1/lending/discovery/eligibility")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.illustrativelyEligible", is(true)))
                .andExpect(jsonPath("$.data.calculatedFoirPercentage", notNullValue()))
                .andExpect(jsonPath("$.data.maxEligibleAmount", notNullValue()))
                .andExpect(jsonPath("$.data.disclaimer", containsString("ILLUSTRATIVE ESTIMATE ONLY")))
                .andExpect(jsonPath("$.data.eligibilityFactors", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.data.improvementSuggestions", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.data.matchingProducts", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.data.matchingProducts[0].simulated", is(true)));
    }

    @Test
    void testAuthoritativeJavaEmiCalculator() throws Exception {
        EmiCalculationRequest request = new EmiCalculationRequest(
                BigDecimal.valueOf(30000),
                BigDecimal.valueOf(6.50),
                48
        );

        mockMvc.perform(post("/api/v1/lending/calculator/emi")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.monthlyEmi", notNullValue()))
                .andExpect(jsonPath("$.data.totalInterest", notNullValue()))
                .andExpect(jsonPath("$.data.totalRepayment", notNullValue()))
                .andExpect(jsonPath("$.data.amortizationSchedule", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.data.amortizationSchedule[0].installmentNumber", is(1)))
                .andExpect(jsonPath("$.data.amortizationSchedule[0].principalPaid", notNullValue()));
    }

    @Test
    void testLoanComparisonMatrix() throws Exception {
        LoanComparisonRequest request = new LoanComparisonRequest(
                null,
                BigDecimal.valueOf(25000),
                36
        );

        mockMvc.perform(post("/api/v1/lending/compare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.comparisonMatrix", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.data.comparisonMatrix[0].lenderName", notNullValue()))
                .andExpect(jsonPath("$.data.comparisonMatrix[0].processingFeeAmount", notNullValue()))
                .andExpect(jsonPath("$.data.comparisonMatrix[0].totalBorrowingCost", notNullValue()))
                .andExpect(jsonPath("$.data.lowestCostLender", notNullValue()));
    }

    @Test
    void testLoanAiAssistantQuery() throws Exception {
        LoanAssistantQueryRequest request = new LoanAssistantQueryRequest(
                "Explain the difference between APR and Flat Interest Rate",
                "PERSONAL",
                "SALARIED",
                BigDecimal.valueOf(20000)
        );

        mockMvc.perform(post("/api/v1/lending/assistant/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.topic", containsString("APR")))
                .andExpect(jsonPath("$.data.plainEnglishDefinition", notNullValue()))
                .andExpect(jsonPath("$.data.practicalImpacts", hasSize(greaterThan(0))));
    }

    @Test
    void testLoanChecklistGeneration() throws Exception {
        LoanChecklistRequest request = new LoanChecklistRequest("PERSONAL", "SALARIED", BigDecimal.valueOf(30000));

        mockMvc.perform(post("/api/v1/lending/assistant/checklist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.mandatoryDocuments", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.data.underwritingCriteria", hasSize(greaterThan(0))));
    }

    @Test
    void testApplicationLifecycleAndSimulatedDecision() throws Exception {
        // 1. Get a product
        MvcResult productsResult = mockMvc.perform(get("/api/v1/lending/products"))
                .andExpect(status().isOk())
                .andReturn();

        String productId = objectMapper.readTree(productsResult.getResponse().getContentAsString())
                .path("data").get(0).path("id").asText();

        // 2. Create application in DRAFT
        CreateLoanApplicationRequest createReq = new CreateLoanApplicationRequest(
                productId,
                BigDecimal.valueOf(18000),
                36,
                "HOME_IMPROVEMENT"
        );
        createReq.setStatus("DRAFT");
        createReq.setEmploymentType("SALARIED");
        createReq.setMonthlyIncome(BigDecimal.valueOf(8000.00));
        createReq.setExistingEmis(BigDecimal.valueOf(400.00));

        MvcResult createRes = mockMvc.perform(post("/api/v1/lending/applications")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status", is("DRAFT")))
                .andReturn();

        String appId = objectMapper.readTree(createRes.getResponse().getContentAsString())
                .path("data").path("id").asText();

        // 3. Update status to SUBMITTED
        UpdateApplicationStatusRequest updateReq = new UpdateApplicationStatusRequest(
                "SUBMITTED", "User submitted documents for underwriting", null
        );

        mockMvc.perform(patch("/api/v1/lending/applications/" + appId + "/status")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", is("SUBMITTED")));

        // 4. Trigger simulated decision
        mockMvc.perform(post("/api/v1/lending/applications/" + appId + "/simulate-decision")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.decision", notNullValue()))
                .andExpect(jsonPath("$.data.simulated", is(true)));
    }
}

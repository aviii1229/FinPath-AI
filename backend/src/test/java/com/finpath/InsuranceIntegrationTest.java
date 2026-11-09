package com.finpath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finpath.modules.insurance.dto.QuoteCalculationRequest;
import com.finpath.modules.insurance.dto.SubmitClaimRequest;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class InsuranceIntegrationTest {

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
    void testGetInsuranceProducts() throws Exception {
        mockMvc.perform(get("/api/v1/insurance/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data[0].carrierName", notNullValue()));
    }

    @Test
    void testCalculateQuote() throws Exception {
        MvcResult productsResult = mockMvc.perform(get("/api/v1/insurance/products"))
                .andExpect(status().isOk())
                .andReturn();

        String productId = objectMapper.readTree(productsResult.getResponse().getContentAsString())
                .path("data").get(0).path("id").asText();

        QuoteCalculationRequest request = new QuoteCalculationRequest(
                productId,
                BigDecimal.valueOf(500000),
                false,
                false
        );

        mockMvc.perform(post("/api/v1/insurance/quotes/calculate")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.monthlyPremium", notNullValue()))
                .andExpect(jsonPath("$.data.riskTier", notNullValue()));
    }

    @Test
    void testGetMyPolicies() throws Exception {
        mockMvc.perform(get("/api/v1/insurance/policies")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data[0].policyNumber", notNullValue()));
    }

    @Test
    void testGetMyClaims() throws Exception {
        mockMvc.perform(get("/api/v1/insurance/claims")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void testSubmitClaim() throws Exception {
        // Fetch existing policy id
        MvcResult policiesResult = mockMvc.perform(get("/api/v1/insurance/policies")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andReturn();

        String policyId = objectMapper.readTree(policiesResult.getResponse().getContentAsString())
                .path("data").get(0).path("id").asText();

        SubmitClaimRequest request = new SubmitClaimRequest(
                policyId,
                BigDecimal.valueOf(450.00),
                LocalDate.now().minusDays(1),
                "Windshield crack replacement from road debris."
        );

        mockMvc.perform(post("/api/v1/insurance/claims")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.claimNumber", notNullValue()))
                .andExpect(jsonPath("$.data.claimAmount", notNullValue()))
                .andExpect(jsonPath("$.data.fraudRiskScore", notNullValue()));
    }

    @Test
    void testInsuranceDiscoveryRecommendation() throws Exception {
        com.finpath.modules.insurance.dto.InsuranceDiscoveryRequest request =
                new com.finpath.modules.insurance.dto.InsuranceDiscoveryRequest(
                        34,
                        BigDecimal.valueOf(80000),
                        BigDecimal.valueOf(350),
                        2,
                        java.util.List.of("HEALTH", "TERM_LIFE"),
                        "BALANCED",
                        false,
                        false
                );

        mockMvc.perform(post("/api/v1/insurance/discovery/recommend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.recommendations", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data.recommendations[0].product.productName", notNullValue()))
                .andExpect(jsonPath("$.data.recommendations[0].simulated", is(true)))
                .andExpect(jsonPath("$.data.aiAdviceSummary", notNullValue()));
    }

    @Test
    void testPolicyComparison() throws Exception {
        MvcResult productsResult = mockMvc.perform(get("/api/v1/insurance/products"))
                .andExpect(status().isOk())
                .andReturn();

        String body = productsResult.getResponse().getContentAsString();
        java.util.UUID id1 = java.util.UUID.fromString(objectMapper.readTree(body).path("data").get(0).path("id").asText());
        java.util.UUID id2 = java.util.UUID.fromString(objectMapper.readTree(body).path("data").get(1).path("id").asText());

        com.finpath.modules.insurance.dto.InsuranceComparisonRequest request =
                new com.finpath.modules.insurance.dto.InsuranceComparisonRequest(java.util.List.of(id1, id2));

        mockMvc.perform(post("/api/v1/insurance/compare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.products", hasSize(2)))
                .andExpect(jsonPath("$.data.comparisonMatrix", hasSize(greaterThanOrEqualTo(5))))
                .andExpect(jsonPath("$.data.aiRecommendation", notNullValue()));
    }

    @Test
    void testClaimChecklistGeneration() throws Exception {
        com.finpath.modules.insurance.dto.ClaimChecklistRequest request =
                new com.finpath.modules.insurance.dto.ClaimChecklistRequest("HEALTH", "HOSPITALIZATION", BigDecimal.valueOf(3500));

        mockMvc.perform(post("/api/v1/insurance/claims/checklist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.requiredDocuments", hasSize(greaterThanOrEqualTo(3))))
                .andExpect(jsonPath("$.data.commonPitfalls", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data.submissionDeadlines", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void testPolicyRagQuestionAnswering() throws Exception {
        com.finpath.modules.insurance.dto.PolicyRagQueryRequest request =
                new com.finpath.modules.insurance.dto.PolicyRagQueryRequest(null, "HEALTH", "What is the emergency room copay and deductible?");

        mockMvc.perform(post("/api/v1/insurance/assistant/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.answer", notNullValue()))
                .andExpect(jsonPath("$.data.grounded", is(true)))
                .andExpect(jsonPath("$.data.citations", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data.citations[0].documentName", notNullValue()))
                .andExpect(jsonPath("$.data.citations[0].sectionTitle", notNullValue()));
    }

    @Test
    void testClaimTrackingWorkflow() throws Exception {
        MvcResult claimsResult = mockMvc.perform(get("/api/v1/insurance/claims")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andReturn();

        String claimId = objectMapper.readTree(claimsResult.getResponse().getContentAsString())
                .path("data").get(0).path("id").asText();

        mockMvc.perform(get("/api/v1/insurance/claims/" + claimId + "/tracking")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.claimNumber", notNullValue()))
                .andExpect(jsonPath("$.data.stages", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$.data.aiFraudScore", notNullValue()));
    }
}

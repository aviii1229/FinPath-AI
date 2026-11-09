package com.finpath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finpath.modules.ai.dto.ChatQueryRequest;
import com.finpath.security.dto.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AiChatIntegrationTest {

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
    void testGetChatSessions() throws Exception {
        mockMvc.perform(get("/api/v1/ai/chat/sessions")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", notNullValue()));
    }

    @Test
    void testScenarioA_FamilyHealthInsuranceJourney() throws Exception {
        ChatQueryRequest queryRequest = new ChatQueryRequest("I need health insurance for my family.");

        mockMvc.perform(post("/api/v1/ai/chat/orchestrate")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(queryRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.intent", is("INSURANCE_FAMILY_COVERAGE")))
                .andExpect(jsonPath("$.data.journeyStage.currentStep", is(2)))
                .andExpect(jsonPath("$.data.cards", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$.data.suggestedActions", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$.data.toolExecutions", hasSize(greaterThanOrEqualTo(2))));
    }

    @Test
    void testScenarioB_LoanEmiAndComparisonJourney() throws Exception {
        ChatQueryRequest queryRequest = new ChatQueryRequest("I need a loan of ₹3 lakh");

        mockMvc.perform(post("/api/v1/ai/chat/query")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(queryRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.intent", is("LENDING_LOAN_DISCOVERY")))
                .andExpect(jsonPath("$.data.journeyStage.currentStep", is(2)))
                .andExpect(jsonPath("$.data.cards", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$.data.cards[0].cardType", is("EMI_BREAKDOWN")))
                .andExpect(jsonPath("$.data.suggestedActions", hasSize(greaterThanOrEqualTo(2))));
    }

    @Test
    void testScenarioC_IncomeAndBudgetOptimizationJourney() throws Exception {
        ChatQueryRequest queryRequest = new ChatQueryRequest("I earn ₹40,000 but cannot save money");

        mockMvc.perform(post("/api/v1/ai/chat/orchestrate")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(queryRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.intent", is("FINTECH_BUDGET_OPTIMIZATION")))
                .andExpect(jsonPath("$.data.journeyStage.currentStep", is(3)))
                .andExpect(jsonPath("$.data.cards", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$.data.cards[0].cardType", is("BUDGET_ANALYSIS")));
    }

    @Test
    void testScenarioD_ReduceMonthlyFinancialBurdenCrossDomain() throws Exception {
        ChatQueryRequest queryRequest = new ChatQueryRequest("I want to reduce my monthly financial burden");

        mockMvc.perform(post("/api/v1/ai/chat/query")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(queryRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.intent", is("CROSS_DOMAIN_DEBT_REDUCTION")))
                .andExpect(jsonPath("$.data.journeyStage.currentStep", is(3)))
                .andExpect(jsonPath("$.data.cards", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$.data.cards[0].cardType", is("DEBT_BURDEN_REDUCTION")));
    }

    @Test
    void testCreateChatSessionAndSendMessage() throws Exception {
        // 1. Create a new chat session
        MvcResult sessionResult = mockMvc.perform(post("/api/v1/ai/chat/sessions")
                        .header("Authorization", "Bearer " + authToken)
                        .param("title", "Loan & Savings Analysis"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.id", notNullValue()))
                .andReturn();

        String sessionId = objectMapper.readTree(sessionResult.getResponse().getContentAsString())
                .path("data").path("id").asText();

        // 2. Send an inquiry querying cash flow & lending
        ChatQueryRequest queryRequest = new ChatQueryRequest(
                sessionId,
                "What is my monthly cash flow and how much auto loan can I afford?",
                true
        );

        mockMvc.perform(post("/api/v1/ai/chat/query")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(queryRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.reply", notNullValue()))
                .andExpect(jsonPath("$.data.toolExecutions", notNullValue()))
                .andExpect(jsonPath("$.data.ragCitations", notNullValue()));

        // 3. Fetch history for this session
        mockMvc.perform(get("/api/v1/ai/chat/sessions/" + sessionId + "/messages")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(2))));
    }

    @Test
    void testPhase3_MultiModule_InsuranceAndBudgeting() throws Exception {
        ChatQueryRequest request = new ChatQueryRequest("I want to buy health insurance but also need to manage my monthly expenses");

        mockMvc.perform(post("/api/v1/ai/chat/orchestrate")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.intent", is("MULTI_MODULE_INSURANCE_AND_BUDGET")))
                .andExpect(jsonPath("$.data.confidenceScore", greaterThanOrEqualTo(0.9)))
                .andExpect(jsonPath("$.data.clarificationNeeded", is(false)))
                .andExpect(jsonPath("$.data.explainability", notNullValue()))
                .andExpect(jsonPath("$.data.explainability.recommendationSummary", notNullValue()))
                .andExpect(jsonPath("$.data.explainability.dataSourcesUsed", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$.data.cards", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$.data.toolInvocations", hasSize(greaterThanOrEqualTo(3))));
    }

    @Test
    void testPhase3_FinancialEducation_Foir() throws Exception {
        ChatQueryRequest request = new ChatQueryRequest("What is FOIR?");

        mockMvc.perform(post("/api/v1/ai/chat/query")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.intent", is("FINANCIAL_EDUCATION")))
                .andExpect(jsonPath("$.data.confidenceScore", is(1.0)))
                .andExpect(jsonPath("$.data.clarificationNeeded", is(false)))
                .andExpect(jsonPath("$.data.content", containsString("Fixed Obligation to Income Ratio")))
                .andExpect(jsonPath("$.data.explainability", notNullValue()));
    }

    @Test
    void testPhase3_AmbiguousQuery_ClarificationNeeded() throws Exception {
        ChatQueryRequest request = new ChatQueryRequest("help me");

        mockMvc.perform(post("/api/v1/ai/chat/query")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.intent", is("AMBIGUOUS_CLARIFICATION_NEEDED")))
                .andExpect(jsonPath("$.data.clarificationNeeded", is(true)))
                .andExpect(jsonPath("$.data.confidenceScore", lessThanOrEqualTo(0.5)))
                .andExpect(jsonPath("$.data.clarificationOptions", hasSize(greaterThanOrEqualTo(3))))
                .andExpect(jsonPath("$.data.cards", hasSize(greaterThanOrEqualTo(3))));
    }

    @Test
    void testPhase3_SessionDeletionAndClearAll() throws Exception {
        // 1. Create a session
        MvcResult sessionRes = mockMvc.perform(post("/api/v1/ai/chat/sessions")
                        .header("Authorization", "Bearer " + authToken)
                        .param("title", "Temporary Session"))
                .andExpect(status().isCreated())
                .andReturn();

        String sessionId = objectMapper.readTree(sessionRes.getResponse().getContentAsString())
                .path("data").path("id").asText();

        // 2. Delete the created session
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/v1/ai/chat/sessions/" + sessionId)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        // 3. Clear all sessions
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/v1/ai/chat/sessions")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        // 4. Verify sessions list is now empty
        mockMvc.perform(get("/api/v1/ai/chat/sessions")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(0)));
    }

    @Test
    void testPhase3_PiiMaskingInChatHistory() throws Exception {
        // Create session
        MvcResult sessionRes = mockMvc.perform(post("/api/v1/ai/chat/sessions")
                        .header("Authorization", "Bearer " + authToken)
                        .param("title", "PII Test Session"))
                .andExpect(status().isCreated())
                .andReturn();

        String sessionId = objectMapper.readTree(sessionRes.getResponse().getContentAsString())
                .path("data").path("id").asText();

        // Send message with Aadhaar and PAN
        ChatQueryRequest request = new ChatQueryRequest(
                sessionId,
                "My Aadhaar is 1234 5678 9012 and my PAN is ABCDE1234F",
                true
        );

        mockMvc.perform(post("/api/v1/ai/chat/query")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Fetch session messages and verify PII was masked
        mockMvc.perform(get("/api/v1/ai/chat/sessions/" + sessionId + "/messages")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[1].content", containsString("XXXX-XXXX-9012")))
                .andExpect(jsonPath("$.data[1].content", containsString("ABCDEXXXXF")));
    }

    @Test
    void testPhase4_OrchestrateFinancialHealthScore() throws Exception {
        ChatQueryRequest request = new ChatQueryRequest(
                "What is my financial health score and how can I improve it?"
        );

        mockMvc.perform(post("/api/v1/ai/chat/query")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.intent", is("FINANCIAL_HEALTH_SCORE")))
                .andExpect(jsonPath("$.data.content", containsString("Financial Health Assessment")))
                .andExpect(jsonPath("$.data.content", containsString("Notice:")))
                .andExpect(jsonPath("$.data.toolInvocations[0].tool", is("getFinancialHealthScore")));
    }

    @Test
    void testPhase4_OrchestrateWhatIfSimulation() throws Exception {
        ChatQueryRequest request = new ChatQueryRequest(
                "What if I save ₹5,000 extra per month and cut dining out?"
        );

        mockMvc.perform(post("/api/v1/ai/chat/query")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.intent", is("WHAT_IF_SIMULATION")))
                .andExpect(jsonPath("$.data.content", containsString("What-If Cashflow Scenario Simulation")))
                .andExpect(jsonPath("$.data.toolInvocations[0].tool", is("simulateWhatIf")));
    }

    @Test
    void testPhase4_OrchestrateSmartExpenseAudit() throws Exception {
        ChatQueryRequest request = new ChatQueryRequest(
                "Review my subscriptions and any duplicate transactions"
        );

        mockMvc.perform(post("/api/v1/ai/chat/query")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.intent", is("SMART_EXPENSE_AUDIT")))
                .andExpect(jsonPath("$.data.content", containsString("Smart Expense & Anomaly Audit")))
                .andExpect(jsonPath("$.data.toolInvocations[0].tool", is("getSmartExpenses")));
    }
}

package com.finpath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finpath.modules.journey.dto.*;
import com.finpath.security.dto.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class JourneyIntegrationTest {

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
    void testGetTemplates() throws Exception {
        mockMvc.perform(get("/api/v1/journeys/templates")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(3))))
                .andExpect(jsonPath("$.data[?(@.journeyType == 'INSURANCE_APPLICATION_JOURNEY')].domain", hasItem("INSURANCE")))
                .andExpect(jsonPath("$.data[?(@.journeyType == 'LENDING_APPLICATION_JOURNEY')].domain", hasItem("LENDING")))
                .andExpect(jsonPath("$.data[?(@.journeyType == 'FINTECH_WEALTH_JOURNEY')].domain", hasItem("FINTECH")));
    }

    @Test
    void testStartInsuranceJourneyAndStepLifecycle() throws Exception {
        StartJourneyRequest startReq = new StartJourneyRequest(
                "INSURANCE_APPLICATION_JOURNEY",
                Map.of("category", "HEALTH", "targetBudgetInr", 25000)
        );

        MvcResult startResult = mockMvc.perform(post("/api/v1/journeys/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(startReq))
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.status", is("IN_PROGRESS")))
                .andExpect(jsonPath("$.data.simulated", is(true)))
                .andExpect(jsonPath("$.data.currentStepKey", is("INSURANCE_DISCOVERY")))
                .andExpect(jsonPath("$.data.totalSteps", is(6)))
                .andReturn();

        String journeyId = objectMapper.readTree(startResult.getResponse().getContentAsString())
                .path("data").path("id").asText();

        // 1. Edit step data (Draft update)
        UpdateStepDataRequest editReq = new UpdateStepDataRequest(
                "INSURANCE_DISCOVERY",
                Map.of("category", "HEALTH_FAMILY_FLOATER", "requestedCoverInr", 1500000)
        );
        mockMvc.perform(put("/api/v1/journeys/" + journeyId + "/step")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editReq))
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.journeyData.INSURANCE_DISCOVERY.category", is("HEALTH_FAMILY_FLOATER")));

        // 2. Complete Step 1
        CompleteStepRequest compReq1 = new CompleteStepRequest(
                "INSURANCE_DISCOVERY",
                Map.of("coverageType", "FAMILY_FLOATER", "chosenTier", "PLATINUM")
        );
        mockMvc.perform(post("/api/v1/journeys/" + journeyId + "/step/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(compReq1))
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.currentStepKey", is("REQUIREMENT_COLLECTION")))
                .andExpect(jsonPath("$.data.currentStepIndex", is(1)))
                .andExpect(jsonPath("$.data.progressPercentage", is(17)));

        // 3. Pause Journey
        mockMvc.perform(post("/api/v1/journeys/" + journeyId + "/pause")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", is("PAUSED")));

        // 4. While paused, attempting to complete a step must be REJECTED by state machine
        CompleteStepRequest compReq2 = new CompleteStepRequest("REQUIREMENT_COLLECTION", Map.of("proposerAge", 32));
        mockMvc.perform(post("/api/v1/journeys/" + journeyId + "/step/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(compReq2))
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", containsString("Resume journey first")));

        // 5. Resume Journey
        mockMvc.perform(post("/api/v1/journeys/" + journeyId + "/resume")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", is("IN_PROGRESS")));

        // 6. Complete Step 2 now that journey is resumed
        mockMvc.perform(post("/api/v1/journeys/" + journeyId + "/step/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(compReq2))
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.currentStepKey", is("POLICY_COMPARISON")))
                .andExpect(jsonPath("$.data.currentStepIndex", is(2)));

        // 7. Cancel Journey
        CancelJourneyRequest cancelReq = new CancelJourneyRequest("User switched to offline advisor");
        mockMvc.perform(post("/api/v1/journeys/" + journeyId + "/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cancelReq))
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", is("CANCELLED")))
                .andExpect(jsonPath("$.data.cancellationReason", is("User switched to offline advisor")));

        // 8. Attempting to resume a CANCELLED journey must be REJECTED (terminal state)
        mockMvc.perform(post("/api/v1/journeys/" + journeyId + "/resume")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", containsString("Invalid journey state transition")));
    }

    @Test
    void testOptimisticLockingConflictDetection() throws Exception {
        StartJourneyRequest startReq = new StartJourneyRequest("FINTECH_WEALTH_JOURNEY");
        MvcResult startResult = mockMvc.perform(post("/api/v1/journeys/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(startReq))
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isCreated())
                .andReturn();

        String journeyId = objectMapper.readTree(startResult.getResponse().getContentAsString())
                .path("data").path("id").asText();
        long currentVersion = objectMapper.readTree(startResult.getResponse().getContentAsString())
                .path("data").path("version").asLong();

        // Pass an outdated / mismatched version (e.g. currentVersion + 5)
        UpdateStepDataRequest staleReq = new UpdateStepDataRequest(
                "EXPENSE_ANALYSIS",
                Map.of("discretionarySpendInr", 15000),
                currentVersion + 5
        );

        mockMvc.perform(put("/api/v1/journeys/" + journeyId + "/step")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(staleReq))
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", containsString("Concurrent modification detected")));
    }

    @Test
    void testJourneyStatsAndRecommendations() throws Exception {
        mockMvc.perform(get("/api/v1/journeys/stats")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.totalJourneys", notNullValue()))
                .andExpect(jsonPath("$.data.averageProgressPercentage", notNullValue()));

        mockMvc.perform(get("/api/v1/journeys/recommendations")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", not(empty())))
                .andExpect(jsonPath("$.data[0].rationale", notNullValue()));
    }

    @Test
    void testAiOrchestratorJourneyTrackingIntent() throws Exception {
        String jsonPayload = """
                {
                    "message": "What is the status of my active journeys and pending steps?"
                }
                """;

        mockMvc.perform(post("/api/v1/ai/chat/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.intent", is("JOURNEY_MANAGEMENT")))
                .andExpect(jsonPath("$.data.toolInvocations[?(@.tool == 'getActiveJourneys')]", not(empty())))
                .andExpect(jsonPath("$.data.content", containsString("Financial Journey Orchestration")))
                .andExpect(jsonPath("$.data.content", containsString("Human Approval Notice")));
    }
}

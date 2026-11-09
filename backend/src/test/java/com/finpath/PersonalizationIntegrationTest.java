package com.finpath;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finpath.modules.personalization.dto.OnboardingRequestDto;
import com.finpath.security.dto.LoginRequest;
import com.finpath.security.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PersonalizationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String authToken;

    @BeforeEach
    void setUp() throws Exception {
        LoginRequest loginRequest = new LoginRequest("sarah.connor@finpath.ai", "SecurePass123!");

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(responseBody);
        authToken = root.path("data").path("accessToken").asText();
    }

    @Test
    void testGetPersonalizationForSeedUser() throws Exception {
        mockMvc.perform(get("/api/v1/personalization")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.onboardingCompleted", is(true)))
                .andExpect(jsonPath("$.data.contextualWelcomeTitle", containsString("Sarah")))
                .andExpect(jsonPath("$.data.relevantModules", not(empty())))
                .andExpect(jsonPath("$.data.suggestedJourneys", not(empty())))
                .andExpect(jsonPath("$.data.dashboardCustomization.debtToIncomeRatio", notNullValue()))
                .andExpect(jsonPath("$.data.dashboardCustomization.savingsRatePercentage", notNullValue()));
    }

    @Test
    void testFullOnboardingWorkflowForNewUser() throws Exception {
        // 1. Register a new user
        String email = "priya.sharma." + UUID.randomUUID() + "@finpath.ai";
        RegisterRequest registerReq = new RegisterRequest(
                "Priya",
                "Sharma",
                email,
                "SecurePass123!",
                "+919876543210"
        );

        MvcResult regResult = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isCreated())
                .andReturn();

        String regToken = objectMapper.readTree(regResult.getResponse().getContentAsString())
                .path("data").path("accessToken").asText();

        // 2. Check initial onboarding status (should not be completed yet)
        mockMvc.perform(get("/api/v1/personalization")
                        .header("Authorization", "Bearer " + regToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.onboardingCompleted", is(false)));

        // 3. Save Step 1 & 2 progress
        OnboardingRequestDto stepDto = new OnboardingRequestDto();
        stepDto.setStep(2);
        stepDto.setFirstName("Priya");
        stepDto.setLastName("Sharma");
        stepDto.setAge(26);
        stepDto.setCity("Pune");
        stepDto.setEmploymentCategory("SALARIED");
        stepDto.setOccupation("Data Analyst");
        stepDto.setMonthlyIncome(BigDecimal.valueOf(65000.00));
        stepDto.setMonthlyExpenses(BigDecimal.valueOf(25000.00));
        stepDto.setExistingObligations(BigDecimal.valueOf(5000.00));
        stepDto.setApproximateSavings(BigDecimal.valueOf(180000.00));

        mockMvc.perform(post("/api/v1/personalization/onboarding/step")
                        .header("Authorization", "Bearer " + regToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stepDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.currentStep", is(2)))
                .andExpect(jsonPath("$.data.onboardingCompleted", is(false)));

        // 4. Complete Onboarding with Goals & Preferences
        OnboardingRequestDto completeDto = new OnboardingRequestDto();
        completeDto.setFirstName("Priya");
        completeDto.setLastName("Sharma");
        completeDto.setAge(26);
        completeDto.setCity("Pune");
        completeDto.setEmploymentCategory("SALARIED");
        completeDto.setOccupation("Data Analyst");
        completeDto.setMonthlyIncome(BigDecimal.valueOf(65000.00));
        completeDto.setMonthlyExpenses(BigDecimal.valueOf(25000.00));
        completeDto.setExistingObligations(BigDecimal.valueOf(5000.00));
        completeDto.setApproximateSavings(BigDecimal.valueOf(180000.00));
        completeDto.setFinancialGoals(Set.of("HEALTH_INSURANCE", "EDUCATION_LOAN", "MONTHLY_BUDGETING"));
        completeDto.setPreferredAssistance("AI_CONVERSATIONAL");
        completeDto.setFinancialKnowledge("BEGINNER");
        completeDto.setCommunicationStyle("EDUCATIONAL");
        completeDto.setShortTermGoal("Pay off certification loan");
        completeDto.setLongTermGoal("Buy an electric vehicle");

        mockMvc.perform(post("/api/v1/personalization/onboarding/complete")
                        .header("Authorization", "Bearer " + regToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(completeDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.onboardingCompleted", is(true)))
                .andExpect(jsonPath("$.data.contextualWelcomeTitle", containsString("Priya")))
                .andExpect(jsonPath("$.data.dashboardCustomization.calculatedMonthlySurplus", is(35000.0)))
                .andExpect(jsonPath("$.data.dashboardCustomization.monthsOfEmergencyRunway", is(7)));
    }

    @Test
    void testUpdatePersonalizationProfile() throws Exception {
        OnboardingRequestDto updateDto = new OnboardingRequestDto();
        updateDto.setFirstName("Sarah");
        updateDto.setLastName("Connor");
        updateDto.setMonthlyIncome(BigDecimal.valueOf(95000.00));
        updateDto.setMonthlyExpenses(BigDecimal.valueOf(38000.00));
        updateDto.setExistingObligations(BigDecimal.valueOf(10000.00));
        updateDto.setApproximateSavings(BigDecimal.valueOf(500000.00));
        updateDto.setFinancialGoals(Set.of("HOME_LOAN", "LIFE_INSURANCE"));
        updateDto.setCommunicationStyle("SIMPLE_CONCISE");

        mockMvc.perform(put("/api/v1/personalization/profile")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.onboardingCompleted", is(true)))
                .andExpect(jsonPath("$.data.dashboardCustomization.calculatedMonthlySurplus", is(47000.0)))
                .andExpect(jsonPath("$.data.dashboardCustomization.primaryFocusArea", containsString("Home Loan")));
    }
}


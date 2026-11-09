package com.finpath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finpath.modules.notification.channel.NotificationChannelDispatcher;
import com.finpath.modules.notification.dto.NotificationPreferenceDto;
import com.finpath.modules.notification.entity.NotificationEntity;
import com.finpath.modules.notification.entity.NotificationPreferenceEntity;
import com.finpath.security.dto.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class NotificationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NotificationChannelDispatcher channelDispatcher;

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
    void testGetNotificationsAndSummary() throws Exception {
        mockMvc.perform(get("/api/v1/notifications")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", notNullValue()));

        mockMvc.perform(get("/api/v1/notifications/summary")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.unreadCount", greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.data.totalCount", greaterThanOrEqualTo(0)));
    }

    @Test
    void testProactiveEvaluationAndDeduplication() throws Exception {
        // Run first proactive evaluation
        MvcResult firstEval = mockMvc.perform(post("/api/v1/notifications/evaluate")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.evaluatedRulesCount", is(6)))
                .andReturn();

        int generatedFirst = objectMapper.readTree(firstEval.getResponse().getContentAsString())
                .path("data").path("generatedCount").asInt();

        // Run second evaluation immediately to test deduplication prevention
        MvcResult secondEval = mockMvc.perform(post("/api/v1/notifications/evaluate")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.evaluatedRulesCount", is(6)))
                .andReturn();

        int generatedSecond = objectMapper.readTree(secondEval.getResponse().getContentAsString())
                .path("data").path("generatedCount").asInt();
        int duplicatesSkipped = objectMapper.readTree(secondEval.getResponse().getContentAsString())
                .path("data").path("skippedDuplicatesCount").asInt();

        // The second run must not duplicate alerts generated in the first run
        if (generatedFirst > 0) {
            assertTrue(duplicatesSkipped >= generatedFirst, "Duplicates should be recognized and skipped");
        }
    }

    @Test
    void testMarkAsReadAndMarkAllAsRead() throws Exception {
        // Evaluate to ensure at least one notification exists
        mockMvc.perform(post("/api/v1/notifications/evaluate")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk());

        MvcResult feedResult = mockMvc.perform(get("/api/v1/notifications")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andReturn();

        var nodes = objectMapper.readTree(feedResult.getResponse().getContentAsString()).path("data");
        if (nodes.isArray() && nodes.size() > 0) {
            String firstId = nodes.get(0).path("id").asText();

            mockMvc.perform(put("/api/v1/notifications/" + firstId + "/read")
                            .header("Authorization", "Bearer " + authToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success", is(true)))
                    .andExpect(jsonPath("$.data.read", is(true)))
                    .andExpect(jsonPath("$.data.readAt", notNullValue()));
        }

        // Test Mark All As Read
        mockMvc.perform(put("/api/v1/notifications/read-all")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        // Unread count should now be 0
        mockMvc.perform(get("/api/v1/notifications/summary")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.unreadCount", is(0)));
    }

    @Test
    void testNotificationPreferencesLifecycle() throws Exception {
        // 1. Fetch current preferences
        MvcResult getResult = mockMvc.perform(get("/api/v1/notifications/preferences")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.timeZone", is("Asia/Kolkata")))
                .andReturn();

        NotificationPreferenceDto current = objectMapper.readValue(
                objectMapper.readTree(getResult.getResponse().getContentAsString()).path("data").toString(),
                NotificationPreferenceDto.class
        );

        // 2. Modify preferences (toggle budget alerts & quiet hours)
        current.setBudgetThresholdAlerts(false);
        current.setQuietHoursEnabled(true);
        current.setQuietHoursStart("23:00");
        current.setQuietHoursEnd("07:00");

        mockMvc.perform(put("/api/v1/notifications/preferences")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(current)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.budgetThresholdAlerts", is(false)))
                .andExpect(jsonPath("$.data.quietHoursEnabled", is(true)))
                .andExpect(jsonPath("$.data.quietHoursStart", is("23:00")))
                .andExpect(jsonPath("$.data.quietHoursEnd", is("07:00")));

        // Revert to clean state
        current.setBudgetThresholdAlerts(true);
        current.setQuietHoursEnabled(false);
        mockMvc.perform(put("/api/v1/notifications/preferences")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(current)))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteAndClearReadNotifications() throws Exception {
        // Mark all as read first
        mockMvc.perform(put("/api/v1/notifications/read-all")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk());

        // Clear all read
        mockMvc.perform(delete("/api/v1/notifications/clear-read")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        // Unread and total count check
        mockMvc.perform(get("/api/v1/notifications/summary")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.unreadCount", is(0)));
    }

    @Test
    void testQuietHoursCalculationLogic() {
        NotificationPreferenceEntity prefs = new NotificationPreferenceEntity();
        prefs.setQuietHoursEnabled(true);
        prefs.setQuietHoursStart("22:00");
        prefs.setQuietHoursEnd("08:00");
        prefs.setTimeZone("Asia/Kolkata");

        ZoneId kolkata = ZoneId.of("Asia/Kolkata");

        // 23:30 at night (within quiet hours)
        ZonedDateTime nightTime = ZonedDateTime.now(kolkata)
                .with(LocalTime.of(23, 30));
        assertTrue(channelDispatcher.isQuietHours(prefs, nightTime.toInstant()));

        // 03:00 early morning (within overnight quiet hours)
        ZonedDateTime earlyMorning = ZonedDateTime.now(kolkata)
                .with(LocalTime.of(3, 0));
        assertTrue(channelDispatcher.isQuietHours(prefs, earlyMorning.toInstant()));

        // 14:00 afternoon (outside quiet hours)
        ZonedDateTime afternoon = ZonedDateTime.now(kolkata)
                .with(LocalTime.of(14, 0));
        assertFalse(channelDispatcher.isQuietHours(prefs, afternoon.toInstant()));
    }

    @Test
    void testPrivacySafeEmailSubjectEnforcement() {
        // External channels must never expose sensitive amounts or full account numbers in subject lines
        String emiSubject = channelDispatcher.getPrivacySafeSubject("LOAN_EMI");
        assertTrue(emiSubject.contains("Upcoming Payment Due"));
        assertFalse(emiSubject.contains("₹"));

        String renewalSubject = channelDispatcher.getPrivacySafeSubject("INSURANCE_RENEWAL");
        assertTrue(renewalSubject.contains("Insurance Policy Notice"));
        assertFalse(renewalSubject.contains("₹"));

        String budgetSubject = channelDispatcher.getPrivacySafeSubject("BUDGET_ALERT");
        assertTrue(budgetSubject.contains("Monthly Spending Update"));
        assertFalse(budgetSubject.contains("₹"));
    }

    @Test
    void testNonExistentNotificationNotFound() throws Exception {
        UUID randomId = UUID.randomUUID();
        mockMvc.perform(put("/api/v1/notifications/" + randomId + "/read")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNotFound());
    }
}


package com.finpath.modules.journey.controller;

import com.finpath.core.dto.ApiResponse;
import com.finpath.modules.journey.dto.*;
import com.finpath.modules.journey.service.JourneyService;
import com.finpath.security.model.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/journeys")
@Tag(name = "Journey Management & Automation", description = "Intelligent Financial Journey State Machine, Progress Tracking & Step Automation")
public class JourneyController {

    private final JourneyService journeyService;

    public JourneyController(JourneyService journeyService) {
        this.journeyService = journeyService;
    }

    @PostMapping("/start")
    @Operation(summary = "Start a new reusable financial journey workflow")
    public ResponseEntity<ApiResponse<UserJourneyDto>> startJourney(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody StartJourneyRequest request) {
        UserJourneyDto dto = journeyService.startJourney(user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Journey initialized successfully", dto));
    }

    @GetMapping
    @Operation(summary = "Get all active, paused, or completed journeys for user with optional filters")
    public ResponseEntity<ApiResponse<List<UserJourneyDto>>> getUserJourneys(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "domain", required = false) String domain) {
        List<UserJourneyDto> list = journeyService.getUserJourneys(user.getId(), status, domain);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get detailed state, steps, and submitted parameters of a specific journey")
    public ResponseEntity<ApiResponse<UserJourneyDto>> getJourney(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID id) {
        UserJourneyDto dto = journeyService.getJourney(user.getId(), id);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @PostMapping("/{id}/pause")
    @Operation(summary = "Pause an in-progress journey workflow")
    public ResponseEntity<ApiResponse<UserJourneyDto>> pauseJourney(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID id,
            @RequestParam(value = "expectedVersion", required = false) Long expectedVersion) {
        UserJourneyDto dto = journeyService.pauseJourney(user.getId(), id, expectedVersion);
        return ResponseEntity.ok(ApiResponse.success("Journey paused", dto));
    }

    @PostMapping("/{id}/resume")
    @Operation(summary = "Resume a paused financial journey workflow")
    public ResponseEntity<ApiResponse<UserJourneyDto>> resumeJourney(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID id,
            @RequestParam(value = "expectedVersion", required = false) Long expectedVersion) {
        UserJourneyDto dto = journeyService.resumeJourney(user.getId(), id, expectedVersion);
        return ResponseEntity.ok(ApiResponse.success("Journey resumed", dto));
    }

    @PutMapping("/{id}/step")
    @Operation(summary = "Edit and update submitted information for a specific step")
    public ResponseEntity<ApiResponse<UserJourneyDto>> updateStepData(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateStepDataRequest request) {
        UserJourneyDto dto = journeyService.updateStepData(user.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Step information updated", dto));
    }

    @PostMapping("/{id}/step/complete")
    @Operation(summary = "Complete an individual step and advance journey workflow")
    public ResponseEntity<ApiResponse<UserJourneyDto>> completeStep(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID id,
            @Valid @RequestBody CompleteStepRequest request) {
        UserJourneyDto dto = journeyService.completeStep(user.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Step marked complete", dto));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel a financial journey with an optional cancellation reason")
    public ResponseEntity<ApiResponse<UserJourneyDto>> cancelJourney(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID id,
            @RequestBody(required = false) CancelJourneyRequest request) {
        UserJourneyDto dto = journeyService.cancelJourney(user.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Journey cancelled", dto));
    }

    @GetMapping("/templates")
    @Operation(summary = "Get backend-controlled journey workflow definitions")
    public ResponseEntity<ApiResponse<List<JourneyTemplateDto>>> getTemplates() {
        List<JourneyTemplateDto> templates = journeyService.getTemplates();
        return ResponseEntity.ok(ApiResponse.success(templates));
    }

    @GetMapping("/stats")
    @Operation(summary = "Get user journey dashboard statistics and progress aggregates")
    public ResponseEntity<ApiResponse<JourneyStatsDto>> getJourneyStats(
            @AuthenticationPrincipal UserPrincipal user) {
        JourneyStatsDto stats = journeyService.getJourneyStats(user.getId());
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/recommendations")
    @Operation(summary = "Get AI-recommended financial journeys matching user needs")
    public ResponseEntity<ApiResponse<List<JourneyRecommendationDto>>> getRecommendations(
            @AuthenticationPrincipal UserPrincipal user) {
        List<JourneyRecommendationDto> recs = journeyService.getRecommendations(user.getId());
        return ResponseEntity.ok(ApiResponse.success(recs));
    }
}


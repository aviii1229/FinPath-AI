package com.finpath.modules.personalization.controller;

import com.finpath.core.dto.ApiResponse;
import com.finpath.modules.personalization.dto.OnboardingRequestDto;
import com.finpath.modules.personalization.dto.PersonalizationResponseDto;
import com.finpath.modules.personalization.service.PersonalizationService;
import com.finpath.security.model.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/personalization")
@Tag(name = "Personalization & Onboarding", description = "Smart onboarding wizard and personalized dashboard endpoints")
public class PersonalizationController {

    private final PersonalizationService personalizationService;

    public PersonalizationController(PersonalizationService personalizationService) {
        this.personalizationService = personalizationService;
    }

    @GetMapping
    @Operation(summary = "Get personalized dashboard configuration and recommended journeys")
    public ResponseEntity<ApiResponse<PersonalizationResponseDto>> getPersonalization(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        PersonalizationResponseDto dto = personalizationService.getPersonalization(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @PostMapping("/onboarding/step")
    @Operation(summary = "Save intermediate step progress in the multi-step onboarding wizard")
    public ResponseEntity<ApiResponse<PersonalizationResponseDto>> saveOnboardingStep(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody OnboardingRequestDto req) {
        PersonalizationResponseDto dto = personalizationService.saveOnboardingStep(userPrincipal.getId(), req);
        return ResponseEntity.ok(ApiResponse.success("Onboarding step saved successfully", dto));
    }

    @PostMapping("/onboarding/complete")
    @Operation(summary = "Complete smart onboarding and generate customized financial recommendations")
    public ResponseEntity<ApiResponse<PersonalizationResponseDto>> completeOnboarding(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody OnboardingRequestDto req) {
        PersonalizationResponseDto dto = personalizationService.completeOnboarding(userPrincipal.getId(), req);
        return ResponseEntity.ok(ApiResponse.success("Onboarding completed successfully", dto));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update financial profile, goals, and communication preferences at any time")
    public ResponseEntity<ApiResponse<PersonalizationResponseDto>> updateProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody OnboardingRequestDto req) {
        PersonalizationResponseDto dto = personalizationService.updateProfile(userPrincipal.getId(), req);
        return ResponseEntity.ok(ApiResponse.success("Financial profile and goals updated successfully", dto));
    }
}


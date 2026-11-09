package com.finpath.security.controller;

import com.finpath.core.dto.ApiResponse;
import com.finpath.security.dto.DataUsageExplanationDto;
import com.finpath.security.dto.PrivacyConsentDto;
import com.finpath.security.dto.UserDataExportDto;
import com.finpath.security.model.UserPrincipal;
import com.finpath.security.service.PrivacyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/privacy")
@Tag(name = "Privacy & Data Governance", description = "DPDP Act 2023 Compliance, Consent Management, Data Export & Erasure")
public class PrivacyController {

    private final PrivacyService privacyService;

    public PrivacyController(PrivacyService privacyService) {
        this.privacyService = privacyService;
    }

    @GetMapping("/consents")
    @Operation(summary = "Get user privacy and consent preferences")
    public ResponseEntity<ApiResponse<PrivacyConsentDto>> getConsents(
            @AuthenticationPrincipal UserPrincipal user) {
        PrivacyConsentDto consents = privacyService.getConsents(user.getId());
        return ResponseEntity.ok(ApiResponse.success(consents));
    }

    @PutMapping("/consents")
    @Operation(summary = "Update user privacy and consent preferences")
    public ResponseEntity<ApiResponse<PrivacyConsentDto>> updateConsents(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody PrivacyConsentDto dto,
            HttpServletRequest request) {
        PrivacyConsentDto updated = privacyService.updateConsents(
                user.getId(), dto, request.getRemoteAddr(), request.getHeader("User-Agent"));
        return ResponseEntity.ok(ApiResponse.success("Privacy preferences updated successfully", updated));
    }

    @GetMapping("/data-usage")
    @Operation(summary = "Transparent explanation of data processing, purposes, and user rights under DPDP")
    public ResponseEntity<ApiResponse<DataUsageExplanationDto>> getDataUsageExplanation() {
        DataUsageExplanationDto explanation = privacyService.getDataUsageExplanation();
        return ResponseEntity.ok(ApiResponse.success(explanation));
    }

    @GetMapping("/export")
    @Operation(summary = "Download complete machine-readable user data export (Right to Data Portability)")
    public ResponseEntity<ApiResponse<UserDataExportDto>> exportData(
            @AuthenticationPrincipal UserPrincipal user) {
        UserDataExportDto export = privacyService.exportUserData(user.getId());
        return ResponseEntity.ok(ApiResponse.success("Data export generated successfully", export));
    }

    @PostMapping("/delete-account")
    @Operation(summary = "Request permanent account deletion and data scrubbing (Right to Erasure)")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(
            @AuthenticationPrincipal UserPrincipal user,
            HttpServletRequest request) {
        privacyService.requestAccountDeletion(
                user.getId(), request.getHeader("Authorization"), request.getRemoteAddr(), request.getHeader("User-Agent"));
        return ResponseEntity.ok(ApiResponse.success("Account marked for deletion and personal data scrubbed", null));
    }
}

package com.finpath.modules.lending.controller;

import com.finpath.core.dto.ApiResponse;
import com.finpath.modules.lending.dto.*;
import com.finpath.modules.lending.service.LendingService;
import com.finpath.modules.lending.service.LoanAiAssistantService;
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
@RequestMapping("/api/v1/lending")
@Tag(name = "Lending & Credit", description = "Loan Products, Pre-qualification, Origination & Amortization")
public class LendingController {

    private final LendingService lendingService;
    private final LoanAiAssistantService loanAiAssistantService;

    public LendingController(LendingService lendingService, LoanAiAssistantService loanAiAssistantService) {
        this.lendingService = lendingService;
        this.loanAiAssistantService = loanAiAssistantService;
    }

    @GetMapping("/products")
    @Operation(summary = "List all active loan programs and terms")
    public ResponseEntity<ApiResponse<List<LoanProductDto>>> getProducts() {
        List<LoanProductDto> products = lendingService.getActiveProducts();
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @GetMapping("/applications")
    @Operation(summary = "Get user loan applications and status")
    public ResponseEntity<ApiResponse<List<LoanApplicationDto>>> getUserApplications(
            @AuthenticationPrincipal UserPrincipal user) {
        List<LoanApplicationDto> applications = lendingService.getUserApplications(user.getId());
        return ResponseEntity.ok(ApiResponse.success(applications));
    }

    @GetMapping("/applications/{id}")
    @Operation(summary = "Get detailed loan application by ID")
    public ResponseEntity<ApiResponse<LoanApplicationDto>> getApplicationById(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal user) {
        LoanApplicationDto app = lendingService.getApplicationById(user.getId(), id);
        return ResponseEntity.ok(ApiResponse.success(app));
    }

    @PostMapping("/eligibility/pre-qualify")
    @Operation(summary = "Soft credit check & dynamic Debt-to-Income evaluation")
    public ResponseEntity<ApiResponse<PreQualifyResponse>> preQualify(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody PreQualifyRequest request) {
        PreQualifyResponse response = lendingService.preQualify(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/discovery/eligibility")
    @Operation(summary = "Calculate illustrative eligibility with factors and suggestions")
    public ResponseEntity<ApiResponse<LoanEligibilityResponse>> discoverEligibility(
            @Valid @RequestBody LoanDiscoveryRequest request) {
        LoanEligibilityResponse response = lendingService.discoverEligibility(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/calculator/emi")
    @Operation(summary = "Authoritative Java EMI calculation and amortization schedule")
    public ResponseEntity<ApiResponse<EmiCalculationResponse>> calculateEmi(
            @Valid @RequestBody EmiCalculationRequest request) {
        EmiCalculationResponse response = lendingService.calculateEmiDetails(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/compare")
    @Operation(summary = "Compare simulated lenders side-by-side with total borrowing cost")
    public ResponseEntity<ApiResponse<LoanComparisonResponse>> compareLoans(
            @RequestBody LoanComparisonRequest request) {
        LoanComparisonResponse response = lendingService.compareLoans(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/applications")
    @Operation(summary = "Submit or draft a loan application")
    public ResponseEntity<ApiResponse<LoanApplicationDto>> createApplication(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody CreateLoanApplicationRequest request) {
        LoanApplicationDto application = lendingService.createApplication(user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Loan application submitted", application));
    }

    @PatchMapping("/applications/{id}/status")
    @Operation(summary = "Update loan application status lifecycle")
    public ResponseEntity<ApiResponse<LoanApplicationDto>> updateApplicationStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateApplicationStatusRequest request) {
        LoanApplicationDto updated = lendingService.updateApplicationStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("Application status updated", updated));
    }

    @PostMapping("/applications/{id}/simulate-decision")
    @Operation(summary = "Trigger simulated underwriting decision engine")
    public ResponseEntity<ApiResponse<SimulateDecisionResponse>> simulateUnderwritingDecision(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal user) {
        SimulateDecisionResponse response = lendingService.simulateUnderwritingDecision(user.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Simulated underwriting decision complete", response));
    }

    @PostMapping("/assistant/query")
    @Operation(summary = "Ask AI loan assistant questions on terminology and guidelines")
    public ResponseEntity<ApiResponse<LoanAssistantQueryResponse>> askAssistant(
            @Valid @RequestBody LoanAssistantQueryRequest request) {
        LoanAssistantQueryResponse response = loanAiAssistantService.answerLendingQuery(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/assistant/checklist")
    @Operation(summary = "Generate tailored document checklist for loan underwriting")
    public ResponseEntity<ApiResponse<LoanChecklistResponse>> getChecklist(
            @RequestBody LoanChecklistRequest request) {
        LoanChecklistResponse response = loanAiAssistantService.generateChecklist(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/applications/{id}/repayment-schedule")
    @Operation(summary = "Get month-by-month repayment amortization schedule")
    public ResponseEntity<ApiResponse<List<RepaymentScheduleItemDto>>> getRepaymentSchedule(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal user) {
        List<RepaymentScheduleItemDto> schedule = lendingService.getSchedule(user.getId(), id);
        return ResponseEntity.ok(ApiResponse.success(schedule));
    }
}

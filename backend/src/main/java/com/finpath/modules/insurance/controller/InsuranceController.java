package com.finpath.modules.insurance.controller;

import com.finpath.core.dto.ApiResponse;
import com.finpath.modules.insurance.dto.*;
import com.finpath.modules.insurance.service.DocumentExtractionService;
import com.finpath.modules.insurance.service.InsuranceService;
import com.finpath.modules.insurance.service.PolicyRagService;
import com.finpath.security.model.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/insurance")
@Tag(name = "Insurance AI", description = "Insurance Discovery, Policy Comparison, Document Extraction, RAG Policy Assistant, & Claims Workflow")
public class InsuranceController {

    private final InsuranceService insuranceService;
    private final DocumentExtractionService documentExtractionService;
    private final PolicyRagService policyRagService;

    public InsuranceController(InsuranceService insuranceService,
                               DocumentExtractionService documentExtractionService,
                               PolicyRagService policyRagService) {
        this.insuranceService = insuranceService;
        this.documentExtractionService = documentExtractionService;
        this.policyRagService = policyRagService;
    }

    @GetMapping("/products")
    @Operation(summary = "Get active insurance products by category")
    public ResponseEntity<ApiResponse<List<InsuranceProductDto>>> getProducts(
            @RequestParam(required = false) String category) {
        List<InsuranceProductDto> products = insuranceService.getActiveProducts(category);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @PostMapping("/discovery/recommend")
    @Operation(summary = "Insurance Discovery: Recommend optimal simulated products based on user needs and budget")
    public ResponseEntity<ApiResponse<InsuranceDiscoveryResponse>> discoverRecommendations(
            @Valid @RequestBody InsuranceDiscoveryRequest request) {
        InsuranceDiscoveryResponse response = insuranceService.discoverRecommendations(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/compare")
    @Operation(summary = "Policy Comparison: Side-by-side analytical matrix of coverage, exclusions, waiting periods & benefits")
    public ResponseEntity<ApiResponse<InsuranceComparisonDto>> comparePolicies(
            @Valid @RequestBody InsuranceComparisonRequest request) {
        InsuranceComparisonDto comparison = insuranceService.comparePolicies(request);
        return ResponseEntity.ok(ApiResponse.success(comparison));
    }

    @PostMapping("/claims/checklist")
    @Operation(summary = "Claim Assistance: Dynamic document checklist and common requirement guide")
    public ResponseEntity<ApiResponse<ClaimChecklistResponse>> getClaimChecklist(
            @Valid @RequestBody ClaimChecklistRequest request) {
        ClaimChecklistResponse checklist = insuranceService.getClaimChecklist(request);
        return ResponseEntity.ok(ApiResponse.success(checklist));
    }

    @GetMapping("/claims/{id}/tracking")
    @Operation(summary = "Track detailed claim progression with multi-stage verification stepper")
    public ResponseEntity<ApiResponse<ClaimTrackingDto>> getClaimTracking(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal user) {
        ClaimTrackingDto tracking = insuranceService.getClaimTracking(user.getId(), id);
        return ResponseEntity.ok(ApiResponse.success(tracking));
    }

    @PostMapping(value = "/documents/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "AI Policy Assistant: Upload PDF policy, extract terms using Apache Tika & index into RAG vector store")
    public ResponseEntity<ApiResponse<DocumentExtractionResponse>> uploadDocument(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "category", defaultValue = "INSURANCE_POLICY") String category) {
        UUID userId = user != null ? user.getId() : null;
        DocumentExtractionResponse response = documentExtractionService.extractAndStoreDocument(userId, file, category);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Document extracted and indexed successfully", response));
    }

    @PostMapping("/assistant/query")
    @Operation(summary = "AI Policy Assistant: Grounded RAG question answering with verified citations from uploaded policy")
    public ResponseEntity<ApiResponse<PolicyRagQueryResponse>> queryPolicyAssistant(
            @Valid @RequestBody PolicyRagQueryRequest request) {
        PolicyRagQueryResponse response = policyRagService.answerPolicyQuestion(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/policies")
    @Operation(summary = "Get current user's bound insurance policies")
    public ResponseEntity<ApiResponse<List<InsurancePolicyDto>>> getPolicies(
            @AuthenticationPrincipal UserPrincipal user) {
        List<InsurancePolicyDto> policies = insuranceService.getUserPolicies(user.getId());
        return ResponseEntity.ok(ApiResponse.success(policies));
    }

    @GetMapping("/claims")
    @Operation(summary = "Get current user's filed claims and adjudication status")
    public ResponseEntity<ApiResponse<List<InsuranceClaimDto>>> getClaims(
            @AuthenticationPrincipal UserPrincipal user) {
        List<InsuranceClaimDto> claims = insuranceService.getUserClaims(user.getId());
        return ResponseEntity.ok(ApiResponse.success(claims));
    }

    @PostMapping("/quotes/calculate")
    @Operation(summary = "Simulate and calculate customized insurance quote premium")
    public ResponseEntity<ApiResponse<QuoteCalculationResponse>> calculateQuote(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody QuoteCalculationRequest request) {
        QuoteCalculationResponse response = insuranceService.calculateQuote(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/claims")
    @Operation(summary = "File a digital claim with incident details")
    public ResponseEntity<ApiResponse<InsuranceClaimDto>> submitClaim(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody SubmitClaimRequest request) {
        InsuranceClaimDto claim = insuranceService.submitClaim(user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Claim submitted successfully", claim));
    }
}

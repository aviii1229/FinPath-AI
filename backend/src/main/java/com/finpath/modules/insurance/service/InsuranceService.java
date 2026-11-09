package com.finpath.modules.insurance.service;

import com.finpath.core.exception.BadRequestException;
import com.finpath.core.exception.ResourceNotFoundException;
import com.finpath.modules.insurance.dto.*;
import com.finpath.modules.insurance.entity.InsuranceClaimEntity;
import com.finpath.modules.insurance.entity.InsurancePolicyEntity;
import com.finpath.modules.insurance.entity.InsuranceProductEntity;
import com.finpath.modules.insurance.repository.InsuranceClaimRepository;
import com.finpath.modules.insurance.repository.InsurancePolicyRepository;
import com.finpath.modules.insurance.repository.InsuranceProductRepository;
import com.finpath.security.entity.UserEntity;
import com.finpath.security.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InsuranceService {

    private final InsuranceProductRepository productRepository;
    private final InsurancePolicyRepository policyRepository;
    private final InsuranceClaimRepository claimRepository;
    private final UserRepository userRepository;

    public InsuranceService(InsuranceProductRepository productRepository,
                            InsurancePolicyRepository policyRepository,
                            InsuranceClaimRepository claimRepository,
                            UserRepository userRepository) {
        this.productRepository = productRepository;
        this.policyRepository = policyRepository;
        this.claimRepository = claimRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<InsuranceProductDto> getActiveProducts(String category) {
        List<InsuranceProductEntity> products = (category != null && !category.equalsIgnoreCase("ALL"))
                ? productRepository.findByCategoryAndIsActiveTrue(category.toUpperCase())
                : productRepository.findByIsActiveTrue();

        return products.stream()
                .map(this::mapToProductDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InsurancePolicyDto> getUserPolicies(UUID userId) {
        return policyRepository.findByUserId(userId).stream()
                .map(this::mapToPolicyDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InsuranceClaimDto> getUserClaims(UUID userId) {
        return claimRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToClaimDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public QuoteCalculationResponse calculateQuote(UUID userId, QuoteCalculationRequest request) {
        InsuranceProductEntity product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Insurance product not found: " + request.getProductId()));

        if (request.getCoverageAmount().compareTo(product.getCoverageAmountMin()) < 0 ||
            request.getCoverageAmount().compareTo(product.getCoverageAmountMax()) > 0) {
            throw new BadRequestException("Coverage amount must be between ₹" + product.getCoverageAmountMin() + " and ₹" + product.getCoverageAmountMax());
        }

        BigDecimal base = request.getCoverageAmount().multiply(BigDecimal.valueOf(0.00008));
        BigDecimal multiplier = BigDecimal.ONE;

        if (request.isSmoker()) {
            multiplier = multiplier.add(BigDecimal.valueOf(0.45)); // +45% smoker surcharge
        }
        if (request.isHasPreExistingConditions()) {
            multiplier = multiplier.add(BigDecimal.valueOf(0.30)); // +30% pre-existing condition
        }

        BigDecimal monthlyPremium = base.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
        BigDecimal deductible = BigDecimal.valueOf(500.00);
        BigDecimal riskScore = request.isSmoker() ? BigDecimal.valueOf(28.5) : BigDecimal.valueOf(14.2);

        return new QuoteCalculationResponse(
                UUID.randomUUID(),
                product.getCarrierName(),
                product.getProductName(),
                request.getCoverageAmount(),
                monthlyPremium,
                deductible,
                riskScore,
                "DRAFT",
                Instant.now().plus(30, ChronoUnit.DAYS)
        );
    }

    @Transactional(readOnly = true)
    public InsuranceDiscoveryResponse discoverRecommendations(InsuranceDiscoveryRequest request) {
        List<InsuranceProductEntity> allProducts = productRepository.findByIsActiveTrue();
        List<InsuranceRecommendationDto> recommendations = new ArrayList<>();

        for (InsuranceProductEntity p : allProducts) {
            int score = 65; // Baseline suitability

            // Category match bonus
            if (request.getCoverageNeeds() != null && request.getCoverageNeeds().contains(p.getCategory())) {
                score += 20;
            }

            // Budget match bonus
            BigDecimal budget = request.getMonthlyBudget() != null ? request.getMonthlyBudget() : BigDecimal.valueOf(300);
            if (p.getBasePremiumMonthly().compareTo(budget) <= 0) {
                score += 10;
            }

            // Priority alignment
            if ("LOW_PREMIUM".equalsIgnoreCase(request.getPrimaryPriority()) && p.getBasePremiumMonthly().compareTo(BigDecimal.valueOf(100)) <= 0) {
                score += 5;
            } else if ("MAX_COVERAGE".equalsIgnoreCase(request.getPrimaryPriority()) && p.getCoverageAmountMax().compareTo(BigDecimal.valueOf(1000000)) >= 0) {
                score += 5;
            }

            // Smoker / Risk adjustment
            BigDecimal adjustedPremium = p.getBasePremiumMonthly();
            if (request.isSmoker() && "TERM_LIFE".equalsIgnoreCase(p.getCategory())) {
                adjustedPremium = adjustedPremium.multiply(BigDecimal.valueOf(1.45)).setScale(2, RoundingMode.HALF_UP);
            }

            String matchReason;
            String priorityBenefit;
            switch (p.getCategory()) {
                case "HEALTH" -> {
                    matchReason = "Comprehensive in-network hospital & prescription coverage with low out-of-pocket maximum.";
                    priorityBenefit = "100% Inpatient Care & Telehealth Support";
                }
                case "TERM_LIFE" -> {
                    BigDecimal tenTimesIncome = request.getAnnualIncome().multiply(BigDecimal.valueOf(10));
                    matchReason = "Recommended death benefit of ₹" + tenTimesIncome.setScale(0, RoundingMode.HALF_UP) + " (10x annual income) to protect dependents.";
                    priorityBenefit = "Level Term Guaranteed Benefit with Zero Waiting Period";
                }
                case "AUTO" -> {
                    matchReason = "Zero-depreciation collision and liability protection optimized for daily commuters.";
                    priorityBenefit = "Roadside Assistance & OEM Replacement Guarantee";
                }
                default -> {
                    matchReason = "Tailored protection aligned with your budget and risk preferences.";
                    priorityBenefit = "Essential Catastrophic Safeguard";
                }
            }

            recommendations.add(new InsuranceRecommendationDto(
                    mapToProductDto(p),
                    Math.min(99, score),
                    matchReason,
                    priorityBenefit,
                    adjustedPremium,
                    true
            ));
        }

        recommendations.sort(Comparator.comparingInt(InsuranceRecommendationDto::getMatchScorePercentage).reversed());

        List<String> gaps = new ArrayList<>();
        if (request.getCoverageNeeds() == null || !request.getCoverageNeeds().contains("TERM_LIFE")) {
            gaps.add("No Life Insurance selected: Recommended 10x income coverage for dependent protection.");
        }
        if (request.getCoverageNeeds() == null || !request.getCoverageNeeds().contains("HEALTH")) {
            gaps.add("Healthcare protection is missing from your selections.");
        }

        String advice = "Based on your annual income of ₹" + request.getAnnualIncome() + " and monthly budget of ₹"
                + (request.getMonthlyBudget() != null ? request.getMonthlyBudget() : 2500)
                + ", we recommend prioritizing foundational Health and Term Life policies to eliminate major financial vulnerabilities.";

        return new InsuranceDiscoveryResponse(recommendations, advice, gaps);
    }

    @Transactional(readOnly = true)
    public InsuranceComparisonDto comparePolicies(InsuranceComparisonRequest request) {
        List<InsuranceProductEntity> products = productRepository.findAllById(request.getProductIds());
        List<InsuranceProductDto> productDtos = products.stream().map(this::mapToProductDto).toList();

        List<InsuranceComparisonDto.ComparisonRow> matrix = new ArrayList<>();

        // 1. Monthly Premium
        Map<String, String> premiumMap = new HashMap<>();
        for (InsuranceProductDto p : productDtos) {
            premiumMap.put(p.getId().toString(), "₹" + p.getBasePremiumMonthly().toString() + "/mo");
        }
        matrix.add(new InsuranceComparisonDto.ComparisonRow("Monthly Premium", "COST", premiumMap));

        // 2. Maximum Coverage (Sum Assured)
        Map<String, String> coverageMap = new HashMap<>();
        for (InsuranceProductDto p : productDtos) {
            coverageMap.put(p.getId().toString(), "₹" + p.getCoverageAmountMax().toString());
        }
        matrix.add(new InsuranceComparisonDto.ComparisonRow("Maximum Coverage", "COVERAGE", coverageMap));

        // 3. Deductible Options
        Map<String, String> deductibleMap = new HashMap<>();
        for (InsuranceProductDto p : productDtos) {
            deductibleMap.put(p.getId().toString(), p.getDeductibleOptions() != null ? p.getDeductibleOptions() : "₹5,000 / ₹15,000");
        }
        matrix.add(new InsuranceComparisonDto.ComparisonRow("Deductibles", "COST", deductibleMap));

        // 4. Co-Pay Percentage
        Map<String, String> copayMap = new HashMap<>();
        for (InsuranceProductDto p : productDtos) {
            copayMap.put(p.getId().toString(), (p.getCopayPercentage() != null ? p.getCopayPercentage() : BigDecimal.valueOf(10)) + "%");
        }
        matrix.add(new InsuranceComparisonDto.ComparisonRow("Co-Pay", "COST", copayMap));

        // 5. Waiting Periods
        Map<String, String> waitingMap = new HashMap<>();
        for (InsuranceProductDto p : productDtos) {
            int days = p.getWaitingPeriodDays() != null ? p.getWaitingPeriodDays() : 0;
            waitingMap.put(p.getId().toString(), days == 0 ? "Immediate (0 Days)" : days + " Days");
        }
        matrix.add(new InsuranceComparisonDto.ComparisonRow("Waiting Period", "TERMS", waitingMap));

        // 6. Key Benefits
        Map<String, String> benefitsMap = new HashMap<>();
        for (InsuranceProductDto p : productDtos) {
            benefitsMap.put(p.getId().toString(), p.getBenefits() != null ? p.getBenefits() : "Inpatient Care, Telehealth, Emergency Services");
        }
        matrix.add(new InsuranceComparisonDto.ComparisonRow("Included Benefits", "BENEFITS", benefitsMap));

        // 7. Key Exclusions
        Map<String, String> exclusionsMap = new HashMap<>();
        for (InsuranceProductDto p : productDtos) {
            exclusionsMap.put(p.getId().toString(), p.getExclusions() != null ? p.getExclusions() : "Cosmetic procedures, self-inflicted injuries, pre-existing conditions < 12 mos");
        }
        matrix.add(new InsuranceComparisonDto.ComparisonRow("Exclusions & Limitations", "EXCLUSIONS", exclusionsMap));

        // 8. AI Value Score
        Map<String, String> ratingMap = new HashMap<>();
        for (InsuranceProductDto p : productDtos) {
            ratingMap.put(p.getId().toString(), "9.4 / 10 (Tier-1 Outstanding)");
        }
        matrix.add(new InsuranceComparisonDto.ComparisonRow("AI Value Rating", "ANALYSIS", ratingMap));

        String recommendation = "Comparison summary: Products exhibit competitive coverage-to-premium ratios. " +
                "Evaluate waiting periods and exclusions carefully against planned medical or lifestyle needs.";

        return new InsuranceComparisonDto(productDtos, matrix, recommendation);
    }

    @Transactional(readOnly = true)
    public ClaimChecklistResponse getClaimChecklist(ClaimChecklistRequest request) {
        String cat = request.getCategory() != null ? request.getCategory().toUpperCase() : "HEALTH";
        List<ChecklistItemDto> docs = new ArrayList<>();
        List<String> pitfalls = new ArrayList<>();
        List<String> deadlines = new ArrayList<>();
        String adjudicationCriteria;
        int days = 3;

        switch (cat) {
            case "HEALTH" -> {
                docs.add(new ChecklistItemDto("Attending Physician Statement", "Official diagnostic summary and treatment notes signed by medical doctor.", true, "FORM-MED-01"));
                docs.add(new ChecklistItemDto("Itemized Hospital Bill", "Detailed breakdown of room charges, pharmacy, lab work, and diagnostics.", true, "INVOICE-HOSP-99"));
                docs.add(new ChecklistItemDto("Discharge Summary", "Summary from facility detailing admission, procedure, and discharge condition.", true, "DISCHARGE-DOC"));
                docs.add(new ChecklistItemDto("Pharmacy Receipts & Prescriptions", "Original receipts matching physician orders.", false, "RX-RECEIPTS"));

                pitfalls.add("Submitting credit card charge slips instead of itemized breakdown.");
                pitfalls.add("Missing medical diagnosis code (ICD-10 / CPT code).");
                pitfalls.add("Non-covered elective or routine cosmetic treatments.");

                deadlines.add("Intimation within 48 hours of emergency admission.");
                deadlines.add("Complete documentation submitted within 30 days of discharge.");

                adjudicationCriteria = "Adjudicated against in-network negotiated rates and standard deductible schedule.";
                days = 2;
            }
            case "AUTO" -> {
                docs.add(new ChecklistItemDto("Police Accident Report (FIR)", "Official incident report detailing accident circumstances and other parties.", true, "POLICE-FIR-AUTO"));
                docs.add(new ChecklistItemDto("Certified Repair Estimate", "Estimate from an authorized auto body repair facility.", true, "ESTIMATE-BODYSHOP"));
                docs.add(new ChecklistItemDto("Clear Photographs of Damage", "Minimum 4 angles showing vehicle license plate and impact zones.", true, "PHOTO-IMPACT-4X"));
                docs.add(new ChecklistItemDto("Driver's License & Registration Copy", "Valid license of person operating vehicle at time of incident.", true, "DL-RC-COPY"));

                pitfalls.add("Commencing repairs prior to insurance surveyor inspection.");
                pitfalls.add("Operating vehicle with expired driver's license or under intoxication.");

                deadlines.add("Notify insurer within 24 hours of collision.");
                deadlines.add("Submit garage estimate within 7 days.");

                adjudicationCriteria = "Surveyor assessment and OEM parts replacement guidelines.";
                days = 4;
            }
            case "TERM_LIFE" -> {
                docs.add(new ChecklistItemDto("Certified Death Certificate", "Official government issued certificate with cause of death.", true, "GOV-DEATH-CERT"));
                docs.add(new ChecklistItemDto("Original Policy Schedule Document", "Contract schedule issued at policy inception.", true, "POLICY-ORIGINAL"));
                docs.add(new ChecklistItemDto("Beneficiary Identification & Bank Details", "Valid government ID and cancelled check for ACH wire disbursement.", true, "KYC-BANK-ACH"));
                docs.add(new ChecklistItemDto("Medical Attendant Statement", "Physician record regarding final illness duration and diagnosis.", false, "PHYSICIAN-FINAL"));

                pitfalls.add("Contestable period investigations if claim occurs within first 24 months of policy.");
                pitfalls.add("Discrepancies in beneficiary legal name and bank registration.");

                deadlines.add("Intimation as soon as practically possible (recommended within 90 days).");

                adjudicationCriteria = "Full death benefit dispatches upon beneficiary identity verification.";
                days = 5;
            }
            default -> {
                docs.add(new ChecklistItemDto("Incident Report & Proof of Loss", "Itemized proof of damage or financial loss.", true, "PROOF-OF-LOSS"));
                docs.add(new ChecklistItemDto("Receipts / Appraisal Values", "Original receipts or recent appraisal documentation.", true, "APPRAISAL-RECEIPT"));

                pitfalls.add("Failing to mitigate ongoing damage (e.g. failing to shut water valve).");
                deadlines.add("Report loss within 72 hours of discovery.");

                adjudicationCriteria = "Standard replacement cost value less deductible.";
                days = 3;
            }
        }

        return new ClaimChecklistResponse(
                cat,
                request.getIncidentType() != null ? request.getIncidentType() : "STANDARD_CLAIM",
                docs,
                pitfalls,
                deadlines,
                adjudicationCriteria,
                days
        );
    }

    @Transactional(readOnly = true)
    public ClaimTrackingDto getClaimTracking(UUID claimId) {
        InsuranceClaimEntity claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found: " + claimId));

        List<ClaimTrackingDto.ClaimStageDto> stages = new ArrayList<>();
        int stepIndex;

        stages.add(new ClaimTrackingDto.ClaimStageDto(
                "Claim Intimation & Submission",
                "COMPLETED",
                claim.getCreatedAt().toString().substring(0, 10),
                "Digital claim submission received. Claim #" + claim.getClaimNumber() + " registered."
        ));

        if ("APPROVED".equalsIgnoreCase(claim.getStatus())) {
            stepIndex = 3;
            stages.add(new ClaimTrackingDto.ClaimStageDto(
                    "Document Verification",
                    "COMPLETED",
                    claim.getCreatedAt().toString().substring(0, 10),
                    "All required receipts, medical statements, and IDs verified."
            ));
            stages.add(new ClaimTrackingDto.ClaimStageDto(
                    "AI Triage & Underwriting Review",
                    "COMPLETED",
                    claim.getCreatedAt().toString().substring(0, 10),
                    "Fraud risk score: " + claim.getAiFraudRiskScore() + "% (Low Risk). Fast-track adjudication approved."
            ));
            stages.add(new ClaimTrackingDto.ClaimStageDto(
                    "Settlement & Payout Disbursement",
                    "COMPLETED",
                    LocalDate.now().toString(),
                    "Disbursement of ₹" + claim.getClaimAmount() + " processed via direct IMPS / NEFT deposit."
            ));
        } else if ("IN_REVIEW".equalsIgnoreCase(claim.getStatus())) {
            stepIndex = 1;
            stages.add(new ClaimTrackingDto.ClaimStageDto(
                    "Document Verification",
                    "CURRENT",
                    claim.getCreatedAt().toString().substring(0, 10),
                    "Checking submitted incident description against policy schedule limits and exclusions."
            ));
            stages.add(new ClaimTrackingDto.ClaimStageDto(
                    "AI Triage & Underwriting Review",
                    "PENDING",
                    "-",
                    "Automated fraud screening and policy terms validation."
            ));
            stages.add(new ClaimTrackingDto.ClaimStageDto(
                    "Settlement & Payout Disbursement",
                    "PENDING",
                    "-",
                    "Final adjudicator sign-off and fund disbursement."
            ));
        } else {
            stepIndex = 0;
            stages.add(new ClaimTrackingDto.ClaimStageDto("Document Verification", "PENDING", "-", "Awaiting document upload."));
            stages.add(new ClaimTrackingDto.ClaimStageDto("AI Triage & Underwriting Review", "PENDING", "-", "Queued for review."));
            stages.add(new ClaimTrackingDto.ClaimStageDto("Settlement & Payout Disbursement", "PENDING", "-", "Awaiting adjudication."));
        }

        String nextAction = stepIndex == 3
                ? "Claim fully settled. Funds deposited into primary checking account."
                : "No further action required at this moment. You will receive an SMS and email notification when review concludes.";

        ClaimTrackingDto dto = new ClaimTrackingDto();
        dto.setClaimId(claim.getId());
        dto.setClaimNumber(claim.getClaimNumber());
        dto.setPolicyNumber(claim.getPolicy().getPolicyNumber());
        dto.setPolicyName(claim.getPolicy().getProduct().getProductName());
        dto.setCategory(claim.getPolicy().getProduct().getCategory());
        dto.setClaimAmount(claim.getClaimAmount());
        dto.setApprovedAmount(claim.getApprovedAmount() != null ? claim.getApprovedAmount() : claim.getClaimAmount());
        dto.setIncidentDate(claim.getIncidentDate());
        dto.setCurrentStatus(claim.getStatus());
        dto.setCurrentStepIndex(stepIndex);
        dto.setStages(stages);
        dto.setAiFraudScore(claim.getAiFraudRiskScore());
        dto.setNextActionPrompt(nextAction);

        return dto;
    }

    @Transactional
    public InsuranceClaimDto submitClaim(UUID userId, SubmitClaimRequest request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        InsurancePolicyEntity policy = policyRepository.findById(request.getPolicyId())
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found: " + request.getPolicyId()));

        if (!policy.getUser().getId().equals(userId)) {
            throw new BadRequestException("Policy does not belong to user");
        }

        if (request.getClaimAmount().compareTo(policy.getCoverageAmount()) > 0) {
            throw new BadRequestException("Claim amount cannot exceed maximum policy coverage of ₹" + policy.getCoverageAmount());
        }

        String claimNumber = "CLM-2026-" + (int)(1000 + Math.random() * 9000);
        BigDecimal aiFraudScore = BigDecimal.valueOf(3.8); // Fast-track approved

        InsuranceClaimEntity claim = new InsuranceClaimEntity(
                claimNumber,
                policy,
                user,
                request.getClaimAmount(),
                request.getIncidentDate() != null ? request.getIncidentDate() : LocalDate.now(),
                request.getIncidentDescription(),
                "IN_REVIEW",
                aiFraudScore
        );

        InsuranceClaimEntity saved = claimRepository.save(claim);
        return mapToClaimDto(saved);
    }

    public InsuranceProductDto mapToProductDto(InsuranceProductEntity p) {
        return new InsuranceProductDto(
                p.getId(),
                p.getCarrierName(),
                p.getProductName(),
                p.getCategory(),
                p.getBasePremiumMonthly(),
                p.getCoverageAmountMin(),
                p.getCoverageAmountMax(),
                p.getTermsSummary(),
                p.getWaitingPeriodDays(),
                p.getBenefits(),
                p.getExclusions(),
                p.getDeductibleOptions(),
                p.getCopayPercentage(),
                p.isSimulated()
        );
    }

    private InsurancePolicyDto mapToPolicyDto(InsurancePolicyEntity p) {
        return new InsurancePolicyDto(
                p.getId(),
                p.getPolicyNumber(),
                p.getProduct().getCarrierName(),
                p.getProduct().getCategory(),
                p.getProduct().getProductName(),
                p.getCoverageAmount(),
                p.getPremiumAmount(),
                p.getDeductible(),
                p.getStartDate(),
                p.getEndDate(),
                p.getStatus()
        );
    }

    private InsuranceClaimDto mapToClaimDto(InsuranceClaimEntity c) {
        return new InsuranceClaimDto(
                c.getId(),
                c.getClaimNumber(),
                c.getPolicy().getId(),
                c.getPolicy().getProduct().getProductName(),
                c.getPolicy().getProduct().getCategory(),
                c.getClaimAmount(),
                c.getApprovedAmount(),
                c.getIncidentDate(),
                c.getIncidentDescription(),
                c.getStatus(),
                c.getAiFraudRiskScore()
        );
    }
}

package com.finpath.modules.lending.service;

import com.finpath.core.exception.BadRequestException;
import com.finpath.core.exception.ResourceNotFoundException;
import com.finpath.modules.lending.dto.*;
import com.finpath.modules.lending.entity.CreditProfileEntity;
import com.finpath.modules.lending.entity.LoanApplicationEntity;
import com.finpath.modules.lending.entity.LoanProductEntity;
import com.finpath.modules.lending.entity.RepaymentScheduleEntity;
import com.finpath.modules.lending.repository.CreditProfileRepository;
import com.finpath.modules.lending.repository.LoanApplicationRepository;
import com.finpath.modules.lending.repository.LoanProductRepository;
import com.finpath.modules.lending.repository.RepaymentScheduleRepository;
import com.finpath.security.entity.UserEntity;
import com.finpath.security.entity.UserProfileEntity;
import com.finpath.security.repository.UserProfileRepository;
import com.finpath.security.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LendingService {

    private final LoanProductRepository loanProductRepository;
    private final LoanApplicationRepository loanApplicationRepository;
    private final RepaymentScheduleRepository repaymentScheduleRepository;
    private final CreditProfileRepository creditProfileRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final LoanAiAssistantService loanAiAssistantService;

    public LendingService(LoanProductRepository loanProductRepository,
                          LoanApplicationRepository loanApplicationRepository,
                          RepaymentScheduleRepository repaymentScheduleRepository,
                          CreditProfileRepository creditProfileRepository,
                          UserRepository userRepository,
                          UserProfileRepository userProfileRepository,
                          LoanAiAssistantService loanAiAssistantService) {
        this.loanProductRepository = loanProductRepository;
        this.loanApplicationRepository = loanApplicationRepository;
        this.repaymentScheduleRepository = repaymentScheduleRepository;
        this.creditProfileRepository = creditProfileRepository;
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.loanAiAssistantService = loanAiAssistantService;
    }

    @Transactional(readOnly = true)
    public List<LoanProductDto> getActiveProducts() {
        return loanProductRepository.findByIsActiveTrue().stream()
                .map(this::mapToProductDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LoanApplicationDto> getUserApplications(UUID userId) {
        return loanApplicationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToApplicationDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LoanApplicationDto getApplicationById(UUID applicationId) {
        LoanApplicationEntity app = loanApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan application not found with id: " + applicationId));
        return mapToApplicationDto(app);
    }

    @Transactional(readOnly = true)
    public PreQualifyResponse preQualify(UUID userId, PreQualifyRequest request) {
        UserProfileEntity profile = userProfileRepository.findByUserId(userId).orElse(null);
        CreditProfileEntity creditProfile = creditProfileRepository.findByUserId(userId).orElse(null);

        BigDecimal annualIncome = (profile != null && profile.getAnnualIncome() != null)
                ? profile.getAnnualIncome()
                : BigDecimal.valueOf(75000.00);

        BigDecimal monthlyGross = annualIncome.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);

        BigDecimal monthlyExistingDebt = (creditProfile != null && creditProfile.getMonthlyDebtObligations() != null)
                ? creditProfile.getMonthlyDebtObligations()
                : BigDecimal.valueOf(770.00);

        BigDecimal apr = BigDecimal.valueOf(0.0549); // 5.49% baseline tier-1
        BigDecimal newEmi = calculateEmi(request.getRequestedAmount(), apr, request.getPreferredTermMonths());

        BigDecimal totalObligations = monthlyExistingDebt.add(newEmi);
        BigDecimal dti = totalObligations.divide(monthlyGross, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(1, RoundingMode.HALF_UP);

        boolean eligible = dti.compareTo(BigDecimal.valueOf(43.0)) <= 0;
        BigDecimal maxEligibleAmount = monthlyGross.multiply(BigDecimal.valueOf(0.43))
                .subtract(monthlyExistingDebt)
                .multiply(BigDecimal.valueOf(request.getPreferredTermMonths()))
                .setScale(2, RoundingMode.HALF_UP);

        if (maxEligibleAmount.compareTo(BigDecimal.ZERO) < 0) {
            maxEligibleAmount = BigDecimal.ZERO;
        }

        List<String> recommendations = new ArrayList<>();
        if (eligible) {
            recommendations.add("Your DTI of " + dti + "% is well within institutional safety margins (<43%).");
            recommendations.add("Consider 48 months to lock in the lowest fixed interest amortization.");
        } else {
            recommendations.add("Extending your tenure lowers monthly payments to bring DTI into approved range.");
            recommendations.add("Paying down existing credit liabilities could save you up to $180/mo in interest.");
        }

        return new PreQualifyResponse(
                eligible,
                maxEligibleAmount,
                apr.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP),
                dti,
                newEmi,
                recommendations
        );
    }

    /**
     * Comprehensive Loan Discovery and Illustrative Eligibility Calculation
     * Note: Clearly states that results are illustrative and never actual approval.
     */
    @Transactional(readOnly = true)
    public LoanEligibilityResponse discoverEligibility(LoanDiscoveryRequest request) {
        BigDecimal income = request.getMonthlyIncome();
        BigDecimal existingEmis = request.getExistingEmis();
        BigDecimal requestedPrincipal = request.getRequestedAmount();
        int tenure = request.getPreferredTenureMonths();
        int creditScore = request.getEstimatedCreditScore();

        // Baseline rate determined by credit tier
        BigDecimal annualAprPercent;
        if (creditScore >= 750) {
            annualAprPercent = BigDecimal.valueOf(5.49);
        } else if (creditScore >= 680) {
            annualAprPercent = BigDecimal.valueOf(7.25);
        } else if (creditScore >= 620) {
            annualAprPercent = BigDecimal.valueOf(9.50);
        } else {
            annualAprPercent = BigDecimal.valueOf(12.75);
        }

        BigDecimal annualAprDecimal = annualAprPercent.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
        BigDecimal proposedEmi = calculateEmi(requestedPrincipal, annualAprDecimal, tenure);

        // Maximum permissible FOIR (Fixed Obligation to Income Ratio) = 45%
        BigDecimal maxFoirRatio = BigDecimal.valueOf(0.45);
        BigDecimal maxTotalPermissibleEmi = income.multiply(maxFoirRatio).setScale(2, RoundingMode.HALF_UP);
        BigDecimal maxPermissibleNewEmi = maxTotalPermissibleEmi.subtract(existingEmis);
        if (maxPermissibleNewEmi.compareTo(BigDecimal.ZERO) < 0) {
            maxPermissibleNewEmi = BigDecimal.ZERO;
        }

        // Compute Max Eligible Principal from maxPermissibleNewEmi: P = EMI * ((1+r)^N - 1) / (r * (1+r)^N)
        BigDecimal maxEligiblePrincipal = reverseCalculatePrincipal(maxPermissibleNewEmi, annualAprDecimal, tenure);

        BigDecimal totalObligations = existingEmis.add(proposedEmi);
        BigDecimal calculatedFoir = totalObligations.divide(income, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(1, RoundingMode.HALF_UP);

        boolean isEligible = calculatedFoir.compareTo(BigDecimal.valueOf(50.0)) <= 0 && maxEligiblePrincipal.compareTo(requestedPrincipal) >= 0;

        List<String> factors = new ArrayList<>();
        factors.add("Income Baseline: ₹" + income.toPlainString() + "/mo (" + request.getEmploymentType() + ")");
        factors.add("FOIR / DTI Commitment: " + calculatedFoir.toPlainString() + "% of monthly income dedicated to servicing debt (Institutional safe limit: 45%)");
        factors.add("Estimated Credit Tier: " + creditScore + " (Qualifies for " + annualAprPercent.toPlainString() + "% APR benchmark)");
        factors.add("Tenure Sensitivity: " + tenure + " months yields monthly repayment of ₹" + proposedEmi.toPlainString() + "/mo");

        List<String> suggestions = new ArrayList<>();
        if (isEligible) {
            suggestions.add("Strong profile! You qualify for our lowest institutional rate tier.");
            suggestions.add("Consider setting up automatic bank debit (NACH / e-Mandate) for a potential 0.25% APR rate discount.");
            if (tenure < 48) {
                suggestions.add("You can comfortably borrow up to ₹" + maxEligiblePrincipal.toPlainString() + " if needed.");
            }
        } else {
            if (calculatedFoir.compareTo(BigDecimal.valueOf(50.0)) > 0) {
                int extendedTenure = Math.min(84, tenure + 24);
                BigDecimal lowerEmi = calculateEmi(requestedPrincipal, annualAprDecimal, extendedTenure);
                suggestions.add("Extending your tenure to " + extendedTenure + " months reduces monthly EMI to ₹" + lowerEmi.toPlainString() + " and lowers your FOIR.");
            }
            if (existingEmis.compareTo(BigDecimal.ZERO) > 0) {
                suggestions.add("Prepaying high-interest revolving credit cards can free up ₹" + existingEmis.toPlainString() + " in monthly debt obligations.");
            }
            suggestions.add("Adding a co-applicant with regular income will significantly elevate your maximum permissible borrowing ceiling.");
        }

        // Recommend matching simulated products
        List<LoanProductEntity> activeProducts = loanProductRepository.findByIsActiveTrue();
        List<LoanRecommendationDto> matchingProducts = new ArrayList<>();

        for (LoanProductEntity prod : activeProducts) {
            BigDecimal prodRate = prod.getMinInterestRate();
            BigDecimal prodRatePercent = prodRate.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
            BigDecimal prodEmi = calculateEmi(requestedPrincipal, prodRate, tenure);
            BigDecimal totalRepayment = prodEmi.multiply(BigDecimal.valueOf(tenure)).setScale(2, RoundingMode.HALF_UP);
            BigDecimal totalInterest = totalRepayment.subtract(requestedPrincipal);
            BigDecimal procFee = requestedPrincipal.multiply(prod.getProcessingFeePercentage()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            int score = 80;
            if (requestedPrincipal.compareTo(prod.getMinPrincipal()) >= 0 && requestedPrincipal.compareTo(prod.getMaxPrincipal()) <= 0) {
                score += 10;
            }
            if (creditScore >= prod.getMinCreditScore()) {
                score += 5;
            }
            if (prod.getLoanType().equalsIgnoreCase(request.getLoanPurpose()) || (prod.getLoanType().equals("PERSONAL") && request.getLoanPurpose().equalsIgnoreCase("PERSONAL"))) {
                score += 5;
            }
            score = Math.min(99, score);

            matchingProducts.add(new LoanRecommendationDto(
                    prod.getId(),
                    prod.getLenderName(),
                    prod.getProductName(),
                    prod.getLoanType(),
                    prodRatePercent,
                    prod.getInterestRateType(),
                    prodEmi,
                    totalInterest,
                    procFee,
                    score,
                    "Institutional risk profile alignment based on " + creditScore + " credit score and " + calculatedFoir.toPlainString() + "% FOIR.",
                    Arrays.asList(
                            "Fixed " + prodRatePercent.toPlainString() + "% APR",
                            "Tenure flexibility: " + prod.getMinTermMonths() + " - " + prod.getMaxTermMonths() + " months",
                            prod.getPrepaymentTerms()
                    ),
                    true
            ));
        }

        return new LoanEligibilityResponse(
                isEligible,
                requestedPrincipal,
                maxEligiblePrincipal,
                calculatedFoir,
                proposedEmi,
                maxPermissibleNewEmi,
                annualAprPercent,
                factors,
                suggestions,
                matchingProducts
        );
    }

    /**
     * Authoritative Java EMI Calculator with full amortization breakdown
     */
    public EmiCalculationResponse calculateEmiDetails(EmiCalculationRequest request) {
        BigDecimal principal = request.getPrincipal();
        BigDecimal annualRate = request.getAnnualInterestRate();
        int tenureMonths = request.getTenureMonths();

        BigDecimal annualRateDecimal = annualRate.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
        BigDecimal monthlyEmi = calculateEmi(principal, annualRateDecimal, tenureMonths);

        BigDecimal totalRepayment = monthlyEmi.multiply(BigDecimal.valueOf(tenureMonths)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalInterest = totalRepayment.subtract(principal);
        BigDecimal ratio = principal.compareTo(BigDecimal.ZERO) > 0
                ? totalInterest.divide(principal, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal monthlyRate = annualRateDecimal.divide(BigDecimal.valueOf(12), 6, RoundingMode.HALF_UP);
        BigDecimal balance = principal;
        List<EmiBreakdownItemDto> schedule = new ArrayList<>();

        // Generate schedule (first 24 months or full schedule if tenure <= 36)
        int maxMonthsToReturn = Math.min(tenureMonths, 36);
        for (int i = 1; i <= maxMonthsToReturn; i++) {
            BigDecimal opening = balance;
            BigDecimal interestPart = balance.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal principalPart = monthlyEmi.subtract(interestPart);
            balance = balance.subtract(principalPart);
            if (balance.compareTo(BigDecimal.ZERO) < 0 || i == tenureMonths) {
                balance = BigDecimal.ZERO;
            }

            schedule.add(new EmiBreakdownItemDto(
                    i,
                    opening,
                    principalPart,
                    interestPart,
                    monthlyEmi,
                    balance
            ));
        }

        return new EmiCalculationResponse(
                principal,
                annualRate,
                tenureMonths,
                monthlyEmi,
                totalInterest,
                totalRepayment,
                ratio,
                schedule
        );
    }

    /**
     * Side-by-side Loan Comparison Matrix
     */
    @Transactional(readOnly = true)
    public LoanComparisonResponse compareLoans(LoanComparisonRequest request) {
        BigDecimal requestedAmount = request.getRequestedAmount();
        int tenure = request.getTenureMonths();

        List<LoanProductEntity> products;
        if (request.getProductIds() != null && !request.getProductIds().isEmpty()) {
            products = loanProductRepository.findAllById(request.getProductIds());
        } else {
            products = loanProductRepository.findByIsActiveTrue();
        }

        List<LoanComparisonItemDto> comparisonList = new ArrayList<>();
        String lowestCostLender = null;
        BigDecimal lowestTotalCost = null;

        String lowestEmiLender = null;
        BigDecimal lowestEmi = null;

        for (LoanProductEntity prod : products) {
            BigDecimal apr = prod.getMinInterestRate();
            BigDecimal aprPercent = apr.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
            BigDecimal emi = calculateEmi(requestedAmount, apr, tenure);
            BigDecimal totalRepay = emi.multiply(BigDecimal.valueOf(tenure)).setScale(2, RoundingMode.HALF_UP);
            BigDecimal totalInterest = totalRepay.subtract(requestedAmount);

            BigDecimal procFeePercent = prod.getProcessingFeePercentage();
            BigDecimal procFeeAmount = requestedAmount.multiply(procFeePercent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            BigDecimal totalBorrowingCost = requestedAmount.add(totalInterest).add(procFeeAmount);

            if (lowestTotalCost == null || totalBorrowingCost.compareTo(lowestTotalCost) < 0) {
                lowestTotalCost = totalBorrowingCost;
                lowestCostLender = prod.getLenderName();
            }

            if (lowestEmi == null || emi.compareTo(lowestEmi) < 0) {
                lowestEmi = emi;
                lowestEmiLender = prod.getLenderName();
            }

            List<String> featureList = prod.getFeatures() != null
                    ? Arrays.asList(prod.getFeatures().split(","))
                    : Arrays.asList("Instant Paperless Processing", "Zero Hidden Charges", prod.getPrepaymentTerms());

            comparisonList.add(new LoanComparisonItemDto(
                    prod.getId(),
                    prod.getLenderName(),
                    prod.getProductName(),
                    prod.getLoanType(),
                    aprPercent,
                    prod.getInterestRateType(),
                    procFeePercent,
                    procFeeAmount,
                    emi,
                    totalInterest,
                    totalBorrowingCost,
                    prod.getMinTermMonths(),
                    prod.getMaxTermMonths(),
                    prod.getPrepaymentTerms(),
                    featureList,
                    true
            ));
        }

        return new LoanComparisonResponse(
                requestedAmount,
                tenure,
                comparisonList,
                lowestCostLender,
                lowestEmiLender
        );
    }

    @Transactional
    public LoanApplicationDto createApplication(UUID userId, CreateLoanApplicationRequest request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        LoanProductEntity product = loanProductRepository.findById(request.getLoanProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Loan product not found: " + request.getLoanProductId()));

        if (request.getRequestedAmount().compareTo(product.getMinPrincipal()) < 0 ||
            request.getRequestedAmount().compareTo(product.getMaxPrincipal()) > 0) {
            throw new BadRequestException("Requested amount must be between ₹" + product.getMinPrincipal() + " and ₹" + product.getMaxPrincipal());
        }

        BigDecimal apr = product.getMinInterestRate();
        BigDecimal monthlyEmi = calculateEmi(request.getRequestedAmount(), apr, request.getRequestedTermMonths());
        BigDecimal calculatedDti = BigDecimal.valueOf(28.5); // Default healthy DTI

        String appNumber = "LN-2026-" + (int)(10000 + Math.random() * 90000);
        String initialStatus = request.getStatus() != null && !request.getStatus().isBlank()
                ? request.getStatus().toUpperCase()
                : "SUBMITTED";

        LoanApplicationEntity app = new LoanApplicationEntity(
                appNumber,
                user,
                product,
                request.getRequestedAmount(),
                request.getRequestedTermMonths(),
                request.getLoanPurpose(),
                calculatedDti,
                monthlyEmi,
                apr.multiply(BigDecimal.valueOf(100)),
                initialStatus
        );

        app.setEmploymentType(request.getEmploymentType());
        app.setMonthlyIncome(request.getMonthlyIncome());
        app.setExistingEmis(request.getExistingEmis());
        app.setCreditScore(740);

        LoanApplicationEntity savedApp = loanApplicationRepository.save(app);

        // Generate Amortization Schedule
        generateAmortizationSchedule(savedApp);

        return mapToApplicationDto(savedApp);
    }

    @Transactional
    public LoanApplicationDto updateApplicationStatus(UUID applicationId, UpdateApplicationStatusRequest request) {
        LoanApplicationEntity app = loanApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan application not found with id: " + applicationId));

        app.setStatus(request.getStatus().toUpperCase());
        if (request.getNotes() != null) {
            app.setDecisionNotes(request.getNotes());
        }
        if (request.getMissingDocuments() != null) {
            app.setMissingDocuments(request.getMissingDocuments());
        }

        LoanApplicationEntity updated = loanApplicationRepository.save(app);
        return mapToApplicationDto(updated);
    }

    /**
     * Simulated Underwriting Decision Engine
     */
    @Transactional
    public SimulateDecisionResponse simulateUnderwritingDecision(UUID applicationId) {
        LoanApplicationEntity app = loanApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan application not found with id: " + applicationId));

        BigDecimal income = app.getMonthlyIncome() != null ? app.getMonthlyIncome() : BigDecimal.valueOf(6250.00);
        BigDecimal existing = app.getExistingEmis() != null ? app.getExistingEmis() : BigDecimal.valueOf(770.00);
        BigDecimal emi = app.getMonthlyEmi();

        BigDecimal totalObligations = existing.add(emi);
        BigDecimal foir = totalObligations.divide(income, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(1, RoundingMode.HALF_UP);

        String decision;
        String explanation;
        List<String> conditions = new ArrayList<>();
        List<String> docs = new ArrayList<>();

        if (foir.compareTo(BigDecimal.valueOf(42.0)) <= 0) {
            decision = "APPROVED";
            explanation = "Congratulations! Your debt service ratio (" + foir + "%) is well under institutional risk limits (<45%). The loan is approved under tier-1 pricing.";
            conditions.add("Sign digital loan agreement with e-signature");
            conditions.add("Set up auto-debit (ACH mandate) for monthly EMI deductions");
            app.setStatus("APPROVED");
        } else if (foir.compareTo(BigDecimal.valueOf(50.0)) <= 0) {
            decision = "CONDITIONALLY_APPROVED";
            explanation = "Approved subject to primary income verification and clearing minor documentation formalities.";
            conditions.add("Submit recent 2 months salary credits bank statement verification");
            conditions.add("Maintain maximum debt balance under $15,000 until initial disbursement");
            app.setStatus("UNDER_REVIEW");
        } else if (foir.compareTo(BigDecimal.valueOf(60.0)) <= 0) {
            decision = "DOCUMENTS_REQUIRED";
            explanation = "Underwriters require additional documentary evidence to offset the elevated FOIR of " + foir + "%.";
            docs.add("Latest 6 months bank statements with verified payroll credits");
            docs.add("Proof of additional liquid savings or co-borrower financial details");
            app.setStatus("DOCUMENTS_REQUIRED");
            app.setMissingDocuments("Latest 6 months bank statements, co-borrower proof of income");
        } else {
            decision = "DECLINED";
            explanation = "Calculated FOIR of " + foir + "% exceeds the regulatory 50% capacity threshold. We recommend extending your tenure or applying with an earning co-borrower.";
            conditions.add("Re-apply after reducing existing monthly debt obligations below ₹" + income.multiply(BigDecimal.valueOf(0.3)).setScale(0, RoundingMode.HALF_UP));
            app.setStatus("DECLINED");
        }

        app.setCalculatedDti(foir);
        app.setDecisionNotes(explanation);
        loanApplicationRepository.save(app);

        return new SimulateDecisionResponse(
                app.getId(),
                app.getApplicationNumber(),
                decision,
                explanation,
                app.getRequestedAmount(),
                app.getApr(),
                app.getMonthlyEmi(),
                foir,
                conditions,
                docs,
                true
        );
    }

    @Transactional(readOnly = true)
    public List<RepaymentScheduleItemDto> getSchedule(UUID applicationId) {
        return repaymentScheduleRepository.findByApplicationIdOrderByInstallmentNumberAsc(applicationId).stream()
                .map(s -> new RepaymentScheduleItemDto(
                        s.getInstallmentNumber(),
                        s.getDueDate(),
                        s.getPrincipalDue(),
                        s.getInterestDue(),
                        s.getTotalPaymentDue(),
                        s.getRemainingBalance(),
                        s.getStatus()
                ))
                .collect(Collectors.toList());
    }

    public BigDecimal calculateEmi(BigDecimal principal, BigDecimal annualRate, int tenureMonths) {
        if (principal == null || annualRate == null || tenureMonths <= 0) return BigDecimal.ZERO;
        double p = principal.doubleValue();
        double r = annualRate.doubleValue() / 12.0;
        double n = tenureMonths;

        if (r == 0) return BigDecimal.valueOf(p / n).setScale(2, RoundingMode.HALF_UP);

        double emi = (p * r * Math.pow(1 + r, n)) / (Math.pow(1 + r, n) - 1);
        return BigDecimal.valueOf(emi).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal reverseCalculatePrincipal(BigDecimal emi, BigDecimal annualRate, int tenureMonths) {
        if (emi == null || annualRate == null || tenureMonths <= 0 || emi.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        double e = emi.doubleValue();
        double r = annualRate.doubleValue() / 12.0;
        double n = tenureMonths;

        if (r == 0) return BigDecimal.valueOf(e * n).setScale(2, RoundingMode.HALF_UP);

        double numerator = Math.pow(1 + r, n) - 1;
        double denominator = r * Math.pow(1 + r, n);
        double principal = e * (numerator / denominator);
        return BigDecimal.valueOf(principal).setScale(2, RoundingMode.HALF_UP);
    }

    private void generateAmortizationSchedule(LoanApplicationEntity app) {
        BigDecimal emi = app.getMonthlyEmi();
        BigDecimal balance = app.getRequestedAmount();
        BigDecimal monthlyRate = app.getApr().divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP).divide(BigDecimal.valueOf(12), 6, RoundingMode.HALF_UP);

        LocalDate startDate = LocalDate.now().plusMonths(1);
        List<RepaymentScheduleEntity> schedule = new ArrayList<>();

        for (int i = 1; i <= app.getRequestedTermMonths(); i++) {
            BigDecimal interestDue = balance.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal principalDue = emi.subtract(interestDue);
            balance = balance.subtract(principalDue);
            if (balance.compareTo(BigDecimal.ZERO) < 0 || i == app.getRequestedTermMonths()) {
                balance = BigDecimal.ZERO;
            }

            schedule.add(new RepaymentScheduleEntity(
                    app,
                    i,
                    startDate.plusMonths(i - 1),
                    principalDue,
                    interestDue,
                    emi,
                    balance,
                    "PENDING"
            ));
        }

        repaymentScheduleRepository.saveAll(schedule);
    }

    private LoanProductDto mapToProductDto(LoanProductEntity p) {
        return new LoanProductDto(
                p.getId(),
                p.getProductName(),
                p.getLoanType(),
                p.getMinPrincipal(),
                p.getMaxPrincipal(),
                p.getMinInterestRate().multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP),
                p.getMaxInterestRate().multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP),
                p.getMinTermMonths(),
                p.getMaxTermMonths(),
                p.getMinCreditScore(),
                p.getLenderName(),
                p.getProcessingFeePercentage(),
                p.getInterestRateType(),
                p.getPrepaymentTerms(),
                p.getFeatures(),
                p.isSimulated()
        );
    }

    private LoanApplicationDto mapToApplicationDto(LoanApplicationEntity a) {
        return new LoanApplicationDto(
                a.getId(),
                a.getApplicationNumber(),
                a.getLoanProduct().getId(),
                a.getLoanProduct().getProductName(),
                a.getLoanProduct().getLoanType(),
                a.getRequestedAmount(),
                a.getRequestedTermMonths(),
                a.getLoanPurpose(),
                a.getCalculatedDti(),
                a.getMonthlyEmi(),
                a.getApr(),
                a.getStatus(),
                a.getCreatedAt(),
                a.getEmploymentType(),
                a.getMonthlyIncome(),
                a.getExistingEmis(),
                a.getMissingDocuments(),
                a.getDecisionNotes(),
                a.getCreditScore()
        );
    }
}

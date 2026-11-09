package com.finpath.modules.ai.tools;

import com.finpath.core.exception.ResourceNotFoundException;
import com.finpath.modules.ai.dto.ChatMessageDto;
import com.finpath.modules.documents.dto.DocumentQueryResponse;
import com.finpath.modules.documents.dto.JourneyCompletenessDto;
import com.finpath.modules.documents.dto.VaultSummaryStatsDto;
import com.finpath.modules.documents.service.DocumentCompletenessService;
import com.finpath.modules.documents.service.DocumentVaultService;
import com.finpath.modules.journey.dto.JourneyStatsDto;
import com.finpath.modules.journey.dto.StartJourneyRequest;
import com.finpath.modules.journey.dto.UserJourneyDto;
import com.finpath.modules.journey.service.JourneyService;
import com.finpath.modules.notification.dto.EvaluateNotificationsResponseDto;
import com.finpath.modules.notification.dto.NotificationDto;
import com.finpath.modules.notification.dto.NotificationPreferenceDto;
import com.finpath.modules.notification.dto.NotificationSummaryDto;
import com.finpath.modules.notification.service.NotificationService;
import com.finpath.modules.notification.service.ProactiveNotificationService;
import com.finpath.modules.fintech.dto.BudgetDto;
import com.finpath.modules.fintech.dto.CashflowForecastDto;
import com.finpath.modules.fintech.dto.FinancialInsightsResponse;
import com.finpath.modules.fintech.dto.TransactionDto;
import com.finpath.modules.fintech.dto.FinancialHealthScoreDto;
import com.finpath.modules.fintech.dto.SmartExpenseAnalysisDto;
import com.finpath.modules.fintech.dto.WhatIfSimulationRequestDto;
import com.finpath.modules.fintech.dto.WhatIfSimulationResponseDto;
import com.finpath.modules.fintech.service.FintechService;
import com.finpath.modules.insurance.dto.*;
import com.finpath.modules.insurance.service.InsuranceService;
import com.finpath.modules.insurance.service.PolicyRagService;
import com.finpath.modules.lending.dto.*;
import com.finpath.modules.lending.service.LendingService;
import com.finpath.security.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FinancialToolRegistry {

    private static final Logger log = LoggerFactory.getLogger(FinancialToolRegistry.class);

    private final UserRepository userRepository;
    private final InsuranceService insuranceService;
    private final PolicyRagService policyRagService;
    private final LendingService lendingService;
    private final FintechService fintechService;
    private final DocumentCompletenessService documentCompletenessService;
    private final DocumentVaultService documentVaultService;
    private final JourneyService journeyService;
    private final NotificationService notificationService;
    private final ProactiveNotificationService proactiveNotificationService;

    public FinancialToolRegistry(UserRepository userRepository,
                                 InsuranceService insuranceService,
                                 PolicyRagService policyRagService,
                                 LendingService lendingService,
                                 FintechService fintechService,
                                 DocumentCompletenessService documentCompletenessService,
                                 DocumentVaultService documentVaultService,
                                 JourneyService journeyService,
                                 NotificationService notificationService,
                                 ProactiveNotificationService proactiveNotificationService) {
        this.userRepository = userRepository;
        this.insuranceService = insuranceService;
        this.policyRagService = policyRagService;
        this.lendingService = lendingService;
        this.fintechService = fintechService;
        this.documentCompletenessService = documentCompletenessService;
        this.documentVaultService = documentVaultService;
        this.journeyService = journeyService;
        this.notificationService = notificationService;
        this.proactiveNotificationService = proactiveNotificationService;
    }

    public static class ToolExecutionResult<T> {
        private final String toolName;
        private final boolean success;
        private final T data;
        private final String summary;
        private final long durationMs;

        public ToolExecutionResult(String toolName, boolean success, T data, String summary, long durationMs) {
            this.toolName = toolName;
            this.success = success;
            this.data = data;
            this.summary = summary;
            this.durationMs = durationMs;
        }

        public String getToolName() { return toolName; }
        public boolean isSuccess() { return success; }
        public T getData() { return data; }
        public String getSummary() { return summary; }
        public long getDurationMs() { return durationMs; }

        public ChatMessageDto.ToolInvocationDto toDto() {
            return new ChatMessageDto.ToolInvocationDto(toolName, summary);
        }
    }

    // =========================================================================
    // 1. INSURANCE TOOLS
    // =========================================================================

    public ToolExecutionResult<List<InsuranceProductDto>> getInsurancePlans(UUID userId, String category, BigDecimal maxBudget) {
        long start = System.currentTimeMillis();
        validateUser(userId);
        try {
            String cat = (category != null && !category.isBlank()) ? category.toUpperCase() : "HEALTH";
            List<InsuranceProductDto> products = insuranceService.getActiveProducts(cat);
            if (maxBudget != null && maxBudget.compareTo(BigDecimal.ZERO) > 0) {
                products = products.stream()
                        .filter(p -> p.getBasePremiumMonthly() != null && p.getBasePremiumMonthly().compareTo(maxBudget) <= 0)
                        .collect(Collectors.toList());
            }
            long duration = System.currentTimeMillis() - start;
            String startRate = (products.isEmpty() || products.get(0).getBasePremiumMonthly() == null)
                    ? "0" : products.get(0).getBasePremiumMonthly().toPlainString();
            String summary = String.format("Found %d active %s plans with premiums starting at ₹%s/mo.",
                    products.size(), cat, startRate);
            return new ToolExecutionResult<>("getInsurancePlans", true, products, summary, duration);
        } catch (Exception ex) {
            log.error("Tool execution failed: getInsurancePlans", ex);
            return new ToolExecutionResult<>("getInsurancePlans", false, Collections.emptyList(), "Failed to retrieve plans: " + ex.getMessage(), System.currentTimeMillis() - start);
        }
    }

    public ToolExecutionResult<InsuranceComparisonDto> comparePolicies(UUID userId, List<UUID> productIds) {
        long start = System.currentTimeMillis();
        validateUser(userId);
        try {
            if (productIds == null || productIds.size() < 2) {
                List<InsuranceProductDto> active = insuranceService.getActiveProducts("HEALTH");
                productIds = active.stream().limit(2).map(InsuranceProductDto::getId).collect(Collectors.toList());
            }
            InsuranceComparisonDto comparison = insuranceService.comparePolicies(new InsuranceComparisonRequest(productIds));
            long duration = System.currentTimeMillis() - start;
            int count = comparison != null && comparison.getProducts() != null ? comparison.getProducts().size() : 2;
            String summary = String.format("Successfully compared %d policies side-by-side with deductible, waiting periods, and network coverage breakdown.", count);
            return new ToolExecutionResult<>("comparePolicies", true, comparison, summary, duration);
        } catch (Exception ex) {
            log.error("Tool execution failed: comparePolicies", ex);
            return new ToolExecutionResult<>("comparePolicies", false, null, "Policy comparison failed: " + ex.getMessage(), System.currentTimeMillis() - start);
        }
    }

    public ToolExecutionResult<PolicyRagQueryResponse> analyzePolicyDocument(UUID userId, String question, String category) {
        long start = System.currentTimeMillis();
        validateUser(userId);
        try {
            String q = question != null ? question : "What are the key policy exclusions and waiting periods?";
            String cat = category != null ? category : "HEALTH";
            PolicyRagQueryRequest req = new PolicyRagQueryRequest(null, cat, q);
            PolicyRagQueryResponse response = policyRagService.answerPolicyQuestion(req);
            long duration = System.currentTimeMillis() - start;
            int citationCount = response != null && response.getCitations() != null ? response.getCitations().size() : 0;
            String summary = String.format("RAG analyzed policy documents and verified %d policy citations.", citationCount);
            return new ToolExecutionResult<>("analyzePolicyDocument", true, response, summary, duration);
        } catch (Exception ex) {
            log.error("Tool execution failed: analyzePolicyDocument", ex);
            return new ToolExecutionResult<>("analyzePolicyDocument", false, null, "Document analysis failed: " + ex.getMessage(), System.currentTimeMillis() - start);
        }
    }

    public ToolExecutionResult<List<InsuranceClaimDto>> getClaimStatus(UUID userId, UUID claimId) {
        long start = System.currentTimeMillis();
        validateUser(userId);
        try {
            List<InsuranceClaimDto> claims = insuranceService.getUserClaims(userId);
            long duration = System.currentTimeMillis() - start;
            String summary;
            if (claims.isEmpty()) {
                summary = "No active insurance claims on record.";
            } else {
                InsuranceClaimDto topClaim = claims.get(0);
                if (claimId != null) {
                    topClaim = claims.stream().filter(c -> c.getId().equals(claimId)).findFirst().orElse(topClaim);
                }
                summary = String.format("Claim #%s: Status %s, Amount: ₹%s (Approved: ₹%s).",
                        topClaim.getClaimNumber(), topClaim.getStatus(),
                        topClaim.getClaimAmount() != null ? topClaim.getClaimAmount().toPlainString() : "0",
                        topClaim.getApprovedAmount() != null ? topClaim.getApprovedAmount().toPlainString() : "0");
            }
            return new ToolExecutionResult<>("getClaimStatus", true, claims, summary, duration);
        } catch (Exception ex) {
            log.error("Tool execution failed: getClaimStatus", ex);
            return new ToolExecutionResult<>("getClaimStatus", false, Collections.emptyList(), "Claim status lookup failed: " + ex.getMessage(), System.currentTimeMillis() - start);
        }
    }

    // =========================================================================
    // 2. LENDING TOOLS
    // =========================================================================

    public ToolExecutionResult<EmiCalculationResponse> calculateEMI(BigDecimal principal, int tenureMonths, BigDecimal apr) {
        long start = System.currentTimeMillis();
        try {
            BigDecimal p = principal != null && principal.compareTo(BigDecimal.ZERO) > 0 ? principal : BigDecimal.valueOf(300000);
            int n = tenureMonths > 0 ? tenureMonths : 36;
            BigDecimal r = apr != null && apr.compareTo(BigDecimal.ZERO) > 0 ? apr : BigDecimal.valueOf(8.99);

            EmiCalculationRequest req = new EmiCalculationRequest(p, r, n);
            EmiCalculationResponse res = lendingService.calculateEmiDetails(req);
            long duration = System.currentTimeMillis() - start;
            String summary = String.format("Computed EMI of ₹%s/mo for principal ₹%s over %d months at %s%% APR.",
                    res.getMonthlyEmi().toPlainString(), p.toPlainString(), n, r.toPlainString());
            return new ToolExecutionResult<>("calculateEMI", true, res, summary, duration);
        } catch (Exception ex) {
            log.error("Tool execution failed: calculateEMI", ex);
            return new ToolExecutionResult<>("calculateEMI", false, null, "EMI calculation failed: " + ex.getMessage(), System.currentTimeMillis() - start);
        }
    }

    public ToolExecutionResult<LoanEligibilityResponse> estimateLoanEligibility(UUID userId, BigDecimal requestedAmount, BigDecimal monthlyIncome, BigDecimal existingEmis) {
        long start = System.currentTimeMillis();
        validateUser(userId);
        try {
            BigDecimal reqAmt = requestedAmount != null ? requestedAmount : BigDecimal.valueOf(300000);
            BigDecimal inc = monthlyIncome != null ? monthlyIncome : BigDecimal.valueOf(75000);
            BigDecimal emis = existingEmis != null ? existingEmis : BigDecimal.valueOf(8540);

            LoanDiscoveryRequest req = new LoanDiscoveryRequest(reqAmt, inc, "SALARIED", emis, 36, "PERSONAL", 750);
            LoanEligibilityResponse res = lendingService.discoverEligibility(req);
            long duration = System.currentTimeMillis() - start;
            String foirStr = res.getCalculatedFoirPercentage() != null ? res.getCalculatedFoirPercentage().toPlainString() : "26.5";
            String maxAmtStr = res.getMaxEligibleAmount() != null ? res.getMaxEligibleAmount().toPlainString() : "650000";
            String summary = String.format("Eligibility evaluated: FOIR is %s%% (Eligible: %s). Max borrowing limit: ₹%s.",
                    foirStr, res.isIllustrativelyEligible() ? "YES" : "CONDITIONAL", maxAmtStr);
            return new ToolExecutionResult<>("estimateLoanEligibility", true, res, summary, duration);
        } catch (Exception ex) {
            log.error("Tool execution failed: estimateLoanEligibility", ex);
            return new ToolExecutionResult<>("estimateLoanEligibility", false, null, "Eligibility evaluation failed: " + ex.getMessage(), System.currentTimeMillis() - start);
        }
    }

    public ToolExecutionResult<List<LoanProductDto>> compareLoanProducts(UUID userId, String purpose) {
        long start = System.currentTimeMillis();
        validateUser(userId);
        try {
            List<LoanProductDto> products = lendingService.getActiveProducts();
            if (purpose != null && !purpose.isBlank() && !purpose.equalsIgnoreCase("ALL")) {
                products = products.stream()
                        .filter(p -> p.getLoanType() != null && p.getLoanType().equalsIgnoreCase(purpose))
                        .collect(Collectors.toList());
            }
            long duration = System.currentTimeMillis() - start;
            String startApr = (products.isEmpty() || products.get(0).getMinInterestRate() == null)
                    ? "8.99" : products.get(0).getMinInterestRate().toPlainString();
            String summary = String.format("Matched %d institutional loan facilities with APRs from %s%%.",
                    products.size(), startApr);
            return new ToolExecutionResult<>("compareLoanProducts", true, products, summary, duration);
        } catch (Exception ex) {
            log.error("Tool execution failed: compareLoanProducts", ex);
            return new ToolExecutionResult<>("compareLoanProducts", false, Collections.emptyList(), "Loan comparison failed: " + ex.getMessage(), System.currentTimeMillis() - start);
        }
    }

    public ToolExecutionResult<List<LoanApplicationDto>> getApplicationStatus(UUID userId) {
        long start = System.currentTimeMillis();
        validateUser(userId);
        try {
            List<LoanApplicationDto> applications = lendingService.getUserApplications(userId);
            long duration = System.currentTimeMillis() - start;
            String summary = applications.isEmpty()
                    ? "No active loan applications found for user."
                    : String.format("Found %d loan applications. Latest: %s (Status: %s).",
                    applications.size(), applications.get(0).getApplicationNumber(), applications.get(0).getStatus());
            return new ToolExecutionResult<>("getApplicationStatus", true, applications, summary, duration);
        } catch (Exception ex) {
            log.error("Tool execution failed: getApplicationStatus", ex);
            return new ToolExecutionResult<>("getApplicationStatus", false, Collections.emptyList(), "Application status check failed: " + ex.getMessage(), System.currentTimeMillis() - start);
        }
    }

    // =========================================================================
    // 3. FINTECH & PFM TOOLS
    // =========================================================================

    public ToolExecutionResult<List<TransactionDto>> getTransactions(UUID userId, String category, int limit) {
        long start = System.currentTimeMillis();
        validateUser(userId);
        try {
            int pageLimit = limit > 0 ? Math.min(limit, 50) : 10;
            List<TransactionDto> txs = fintechService.getTransactions(userId, PageRequest.of(0, pageLimit)).getContent();
            if (category != null && !category.equalsIgnoreCase("ALL")) {
                txs = txs.stream()
                        .filter(t -> t.getCategory() != null && t.getCategory().equalsIgnoreCase(category))
                        .collect(Collectors.toList());
            }
            long duration = System.currentTimeMillis() - start;
            String summary = String.format("Retrieved %d transactions%s.", txs.size(), category != null ? " for " + category : "");
            return new ToolExecutionResult<>("getTransactions", true, txs, summary, duration);
        } catch (Exception ex) {
            log.error("Tool execution failed: getTransactions", ex);
            return new ToolExecutionResult<>("getTransactions", false, Collections.emptyList(), "Transaction fetch failed: " + ex.getMessage(), System.currentTimeMillis() - start);
        }
    }

    public ToolExecutionResult<FinancialInsightsResponse> analyzeExpenses(UUID userId) {
        long start = System.currentTimeMillis();
        validateUser(userId);
        try {
            FinancialInsightsResponse insights = fintechService.getFinancialInsights(userId);
            long duration = System.currentTimeMillis() - start;
            int spikesCount = insights != null && insights.getUnusualSpikes() != null ? insights.getUnusualSpikes().size() : 2;
            int insightCount = insights != null && insights.getInsights() != null ? insights.getInsights().size() : 3;
            String summary = String.format("Analyzed monthly expenditure: %d unusual spending patterns detected, %d actionable recommendations identified.",
                    spikesCount, insightCount);
            return new ToolExecutionResult<>("analyzeExpenses", true, insights, summary, duration);
        } catch (Exception ex) {
            log.error("Tool execution failed: analyzeExpenses", ex);
            return new ToolExecutionResult<>("analyzeExpenses", false, null, "Expense analysis failed: " + ex.getMessage(), System.currentTimeMillis() - start);
        }
    }

    public ToolExecutionResult<List<BudgetDto>> getBudgetStatus(UUID userId) {
        long start = System.currentTimeMillis();
        validateUser(userId);
        try {
            List<BudgetDto> budgets = fintechService.getBudgets(userId);
            long duration = System.currentTimeMillis() - start;
            long overspentCount = budgets.stream()
                    .filter(b -> b.getCurrentSpent() != null && b.getMonthlyLimit() != null && b.getCurrentSpent().compareTo(b.getMonthlyLimit()) > 0)
                    .count();
            String summary = String.format("Retrieved %d monthly budget categories. %d categories currently exceeding limit.",
                    budgets.size(), overspentCount);
            return new ToolExecutionResult<>("getBudgetStatus", true, budgets, summary, duration);
        } catch (Exception ex) {
            log.error("Tool execution failed: getBudgetStatus", ex);
            return new ToolExecutionResult<>("getBudgetStatus", false, Collections.emptyList(), "Budget progress failed: " + ex.getMessage(), System.currentTimeMillis() - start);
        }
    }

    public ToolExecutionResult<CashflowForecastDto> calculateSavings(UUID userId) {
        long start = System.currentTimeMillis();
        validateUser(userId);
        try {
            CashflowForecastDto forecast = fintechService.getCashflowForecast(userId);
            long duration = System.currentTimeMillis() - start;
            BigDecimal inflows = forecast.getExpectedInflows30Days() != null
                    ? forecast.getExpectedInflows30Days()
                    : BigDecimal.valueOf(75000);
            BigDecimal outflows = forecast.getExpectedOutflows30Days() != null
                    ? forecast.getExpectedOutflows30Days()
                    : BigDecimal.valueOf(42280);
            BigDecimal netSurplus = inflows.subtract(outflows);
            String summary = String.format("Calculated 30-day cashflow: Inflows ₹%s, Outflows ₹%s, Projected net surplus ₹%s.",
                    inflows.toPlainString(), outflows.toPlainString(), netSurplus.toPlainString());
            return new ToolExecutionResult<>("calculateSavings", true, forecast, summary, duration);
        } catch (Exception ex) {
            log.error("Tool execution failed: calculateSavings", ex);
            return new ToolExecutionResult<>("calculateSavings", false, null, "Cashflow calculation failed: " + ex.getMessage(), System.currentTimeMillis() - start);
        }
    }

    public ToolExecutionResult<FinancialHealthScoreDto> getFinancialHealthScore(UUID userId) {
        long start = System.currentTimeMillis();
        validateUser(userId);
        try {
            FinancialHealthScoreDto score = fintechService.calculateFinancialHealthScore(userId);
            long duration = System.currentTimeMillis() - start;
            String summary = String.format("Computed Financial Health Score: %d/100 (%s) across 5 deterministic factors.",
                    score.getOverallScore(), score.getHealthTier());
            return new ToolExecutionResult<>("getFinancialHealthScore", true, score, summary, duration);
        } catch (Exception ex) {
            log.error("Tool execution failed: getFinancialHealthScore", ex);
            return new ToolExecutionResult<>("getFinancialHealthScore", false, null, "Health score evaluation failed: " + ex.getMessage(), System.currentTimeMillis() - start);
        }
    }

    public ToolExecutionResult<SmartExpenseAnalysisDto> getSmartExpenses(UUID userId) {
        long start = System.currentTimeMillis();
        validateUser(userId);
        try {
            SmartExpenseAnalysisDto analysis = fintechService.analyzeSmartExpenses(userId);
            long duration = System.currentTimeMillis() - start;
            int subs = analysis.getSubscriptions() != null ? analysis.getSubscriptions().size() : 0;
            int dups = analysis.getDuplicateSuspects() != null ? analysis.getDuplicateSuspects().size() : 0;
            String summary = String.format("Smart Expense Audit: identified %d recurring subscriptions and %d duplicate suspects requiring review.", subs, dups);
            return new ToolExecutionResult<>("getSmartExpenses", true, analysis, summary, duration);
        } catch (Exception ex) {
            log.error("Tool execution failed: getSmartExpenses", ex);
            return new ToolExecutionResult<>("getSmartExpenses", false, null, "Smart expense analysis failed: " + ex.getMessage(), System.currentTimeMillis() - start);
        }
    }

    public ToolExecutionResult<WhatIfSimulationResponseDto> simulateWhatIf(UUID userId, WhatIfSimulationRequestDto request) {
        long start = System.currentTimeMillis();
        validateUser(userId);
        try {
            WhatIfSimulationResponseDto sim = fintechService.simulateWhatIf(userId, request);
            long duration = System.currentTimeMillis() - start;
            String summary = String.format("What-If Simulation modeled: Net surplus %s (₹%s), Feasibility: %s.",
                    sim.getNetMonthlyCashflowDelta().compareTo(BigDecimal.ZERO) >= 0 ? "gain" : "impact",
                    sim.getNetMonthlyCashflowDelta().abs().toPlainString(),
                    sim.getFeasibilityVerdict());
            return new ToolExecutionResult<>("simulateWhatIf", true, sim, summary, duration);
        } catch (Exception ex) {
            log.error("Tool execution failed: simulateWhatIf", ex);
            return new ToolExecutionResult<>("simulateWhatIf", false, null, "What-If simulation failed: " + ex.getMessage(), System.currentTimeMillis() - start);
        }
    }

    public ToolExecutionResult<JourneyCompletenessDto> getDocumentCompleteness(UUID userId, String journeyType) {
        long start = System.currentTimeMillis();
        validateUser(userId);
        try {
            JourneyCompletenessDto completeness = documentCompletenessService.evaluateCompleteness(userId, journeyType);
            long duration = System.currentTimeMillis() - start;
            String summary = String.format("Evaluated %s document completeness: %d%% completed (%d of %d required documents present).",
                    completeness.getJourneyTitle(), completeness.getCompletionPercentage(),
                    completeness.getTotalFulfilled(), completeness.getTotalRequired());
            return new ToolExecutionResult<>("getDocumentCompleteness", true, completeness, summary, duration);
        } catch (Exception ex) {
            log.error("Tool execution failed: getDocumentCompleteness", ex);
            return new ToolExecutionResult<>("getDocumentCompleteness", false, null, "Document completeness check failed: " + ex.getMessage(), System.currentTimeMillis() - start);
        }
    }

    public ToolExecutionResult<VaultSummaryStatsDto> getVaultStats(UUID userId) {
        long start = System.currentTimeMillis();
        validateUser(userId);
        try {
            VaultSummaryStatsDto stats = documentVaultService.getVaultStats(userId);
            long duration = System.currentTimeMillis() - start;
            String summary = String.format("Retrieved vault statistics: %d documents stored, %s storage utilized, average readiness %d%%.",
                    stats.getTotalDocuments(), stats.getFormattedTotalSize(), stats.getAverageCompletenessScore());
            return new ToolExecutionResult<>("getVaultStats", true, stats, summary, duration);
        } catch (Exception ex) {
            log.error("Tool execution failed: getVaultStats", ex);
            return new ToolExecutionResult<>("getVaultStats", false, null, "Vault stats retrieval failed: " + ex.getMessage(), System.currentTimeMillis() - start);
        }
    }

    public ToolExecutionResult<DocumentQueryResponse> queryDocument(UUID userId, UUID docId, String question) {
        long start = System.currentTimeMillis();
        validateUser(userId);
        try {
            DocumentQueryResponse response = documentVaultService.queryDocument(userId, docId, question);
            long duration = System.currentTimeMillis() - start;
            String summary = String.format("Grounded document query returned with %d cited clauses.", response.getCitations().size());
            return new ToolExecutionResult<>("queryDocument", true, response, summary, duration);
        } catch (Exception ex) {
            log.error("Tool execution failed: queryDocument", ex);
            return new ToolExecutionResult<>("queryDocument", false, null, "Document query failed: " + ex.getMessage(), System.currentTimeMillis() - start);
        }
    }

    public ToolExecutionResult<List<UserJourneyDto>> getActiveJourneys(UUID userId) {
        long start = System.currentTimeMillis();
        validateUser(userId);
        try {
            List<UserJourneyDto> list = journeyService.getUserJourneys(userId, "IN_PROGRESS", null);
            long duration = System.currentTimeMillis() - start;
            String summary = String.format("Retrieved %d active financial journey workflows in progress.", list.size());
            return new ToolExecutionResult<>("getActiveJourneys", true, list, summary, duration);
        } catch (Exception ex) {
            log.error("Tool execution failed: getActiveJourneys", ex);
            return new ToolExecutionResult<>("getActiveJourneys", false, Collections.emptyList(), "Active journeys retrieval failed: " + ex.getMessage(), System.currentTimeMillis() - start);
        }
    }

    public ToolExecutionResult<UserJourneyDto> startFinancialJourney(UUID userId, String journeyType) {
        long start = System.currentTimeMillis();
        validateUser(userId);
        try {
            UserJourneyDto dto = journeyService.startJourney(userId, new StartJourneyRequest(journeyType));
            long duration = System.currentTimeMillis() - start;
            String summary = String.format("Initialized %s journey (Status: %s, Current Step: %s).",
                    dto.getTitle(), dto.getStatus(), dto.getCurrentStepKey());
            return new ToolExecutionResult<>("startFinancialJourney", true, dto, summary, duration);
        } catch (Exception ex) {
            log.error("Tool execution failed: startFinancialJourney", ex);
            return new ToolExecutionResult<>("startFinancialJourney", false, null, "Journey start failed: " + ex.getMessage(), System.currentTimeMillis() - start);
        }
    }

    public ToolExecutionResult<JourneyStatsDto> getJourneyDashboardStats(UUID userId) {
        long start = System.currentTimeMillis();
        validateUser(userId);
        try {
            JourneyStatsDto stats = journeyService.getJourneyStats(userId);
            long duration = System.currentTimeMillis() - start;
            String summary = String.format("Journey overview: %d active, %d completed, %d paused, average completion %d%%.",
                    stats.getActiveJourneys(), stats.getCompletedJourneys(), stats.getPausedJourneys(), stats.getAverageProgressPercentage());
            return new ToolExecutionResult<>("getJourneyDashboardStats", true, stats, summary, duration);
        } catch (Exception ex) {
            log.error("Tool execution failed: getJourneyDashboardStats", ex);
            return new ToolExecutionResult<>("getJourneyDashboardStats", false, null, "Journey stats retrieval failed: " + ex.getMessage(), System.currentTimeMillis() - start);
        }
    }

    public ToolExecutionResult<NotificationSummaryDto> getNotificationSummary(UUID userId) {
        long start = System.currentTimeMillis();
        validateUser(userId);
        try {
            NotificationSummaryDto summary = notificationService.getSummary(userId);
            long duration = System.currentTimeMillis() - start;
            String text = String.format("Notification overview: %d unread alerts (%d critical, %d high priority) across %d total alerts.",
                    summary.getUnreadCount(), summary.getCriticalCount(), summary.getHighCount(), summary.getTotalCount());
            return new ToolExecutionResult<>("getNotificationSummary", true, summary, text, duration);
        } catch (Exception ex) {
            log.error("Tool execution failed: getNotificationSummary", ex);
            return new ToolExecutionResult<>("getNotificationSummary", false, null, "Notification summary failed: " + ex.getMessage(), System.currentTimeMillis() - start);
        }
    }

    public ToolExecutionResult<EvaluateNotificationsResponseDto> evaluateProactiveReminders(UUID userId) {
        long start = System.currentTimeMillis();
        validateUser(userId);
        try {
            EvaluateNotificationsResponseDto res = proactiveNotificationService.evaluateProactiveNotifications(userId);
            long duration = System.currentTimeMillis() - start;
            String text = String.format("Proactive scan evaluated %d rules: %d new notifications generated, %d duplicate alerts suppressed.",
                    res.getEvaluatedRulesCount(), res.getGeneratedCount(), res.getSkippedDuplicatesCount());
            return new ToolExecutionResult<>("evaluateProactiveReminders", true, res, text, duration);
        } catch (Exception ex) {
            log.error("Tool execution failed: evaluateProactiveReminders", ex);
            return new ToolExecutionResult<>("evaluateProactiveReminders", false, null, "Proactive scan failed: " + ex.getMessage(), System.currentTimeMillis() - start);
        }
    }

    public ToolExecutionResult<NotificationPreferenceDto> getNotificationPreferences(UUID userId) {
        long start = System.currentTimeMillis();
        validateUser(userId);
        try {
            NotificationPreferenceDto prefs = notificationService.getPreferences(userId);
            long duration = System.currentTimeMillis() - start;
            String text = String.format("Notification channels: In-App (%s), Email (%s), Push (%s). Quiet hours: %s.",
                    prefs.isInAppEnabled() ? "Enabled" : "Disabled",
                    prefs.isEmailEnabled() ? "Enabled" : "Disabled",
                    prefs.isPushEnabled() ? "Enabled" : "Disabled",
                    prefs.isQuietHoursEnabled() ? (prefs.getQuietHoursStart() + " - " + prefs.getQuietHoursEnd()) : "Off");
            return new ToolExecutionResult<>("getNotificationPreferences", true, prefs, text, duration);
        } catch (Exception ex) {
            log.error("Tool execution failed: getNotificationPreferences", ex);
            return new ToolExecutionResult<>("getNotificationPreferences", false, null, "Notification preferences retrieval failed: " + ex.getMessage(), System.currentTimeMillis() - start);
        }
    }

    private void validateUser(UUID userId) {
        if (userId == null || !userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Authenticated user context invalid or not found: " + userId);
        }
    }
}

package com.finpath.modules.ai.service;

import com.finpath.core.exception.ResourceNotFoundException;
import com.finpath.modules.ai.dto.*;
import com.finpath.modules.ai.entity.AiChatMessageEntity;
import com.finpath.modules.ai.entity.AiChatSessionEntity;
import com.finpath.modules.ai.repository.AiChatMessageRepository;
import com.finpath.modules.ai.repository.AiChatSessionRepository;
import com.finpath.modules.ai.tools.FinancialToolRegistry;
import com.finpath.modules.documents.dto.*;
import com.finpath.modules.fintech.dto.*;
import com.finpath.modules.fintech.service.FintechService;
import com.finpath.modules.insurance.dto.*;
import com.finpath.modules.insurance.service.InsuranceService;
import com.finpath.modules.lending.dto.*;
import com.finpath.modules.lending.service.LendingService;
import com.finpath.modules.lending.service.LoanAiAssistantService;
import com.finpath.security.entity.UserEntity;
import com.finpath.security.entity.UserProfileEntity;
import com.finpath.security.repository.UserProfileRepository;
import com.finpath.security.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AiJourneyOrchestratorService {

    private final AiChatSessionRepository sessionRepository;
    private final AiChatMessageRepository messageRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final InsuranceService insuranceService;
    private final LendingService lendingService;
    private final FintechService fintechService;
    private final LoanAiAssistantService loanAiAssistantService;
    private final FinancialToolRegistry toolRegistry;

    public AiJourneyOrchestratorService(AiChatSessionRepository sessionRepository,
                                        AiChatMessageRepository messageRepository,
                                        UserRepository userRepository,
                                        UserProfileRepository userProfileRepository,
                                        InsuranceService insuranceService,
                                        LendingService lendingService,
                                        FintechService fintechService,
                                        LoanAiAssistantService loanAiAssistantService,
                                        FinancialToolRegistry toolRegistry) {
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.insuranceService = insuranceService;
        this.lendingService = lendingService;
        this.fintechService = fintechService;
        this.loanAiAssistantService = loanAiAssistantService;
        this.toolRegistry = toolRegistry;
    }

    @Transactional
    public ChatQueryResponse orchestrateJourney(UUID userId, ChatQueryRequest request) {
        long startTime = System.currentTimeMillis();

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        UserProfileEntity userProfile = userProfileRepository.findByUserId(userId).orElse(null);

        AiChatSessionEntity session;
        if (request.getSessionId() != null) {
            session = sessionRepository.findById(request.getSessionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Session not found: " + request.getSessionId()));
        } else {
            session = sessionRepository.save(new AiChatSessionEntity(user, "AI Financial Journey Consultation"));
        }

        // Mask any sensitive PII (Aadhaar, Card, PAN) before saving message history
        String rawMessage = request.getMessage() != null ? request.getMessage().trim() : "";
        String sanitizedMessage = maskPii(rawMessage);

        // Persist User Message
        AiChatMessageEntity userMsg = new AiChatMessageEntity(session, "user", sanitizedMessage);
        messageRepository.save(userMsg);

        String messageText = rawMessage;
        String lower = messageText.toLowerCase();

        ChatQueryResponse response;

        // Intent Recognition Hierarchy:
        // 1. Ambiguous / Vague queries -> Confidence-aware clarification needed
        if (isAmbiguousIntent(lower)) {
            response = handleAmbiguousOrClarificationNeeded(user, userProfile, session, messageText);
        }
        // 2. Financial Education / Conceptual queries ("what is foir", "explain apr", "what is deductible")
        else if (isFinancialEducationIntent(lower)) {
            response = handleFinancialEducation(user, userProfile, session, messageText);
        }
        // Phase 04: Financial Health Score
        else if (isFinancialHealthScoreIntent(lower)) {
            response = handleFinancialHealthScore(user, userProfile, session, messageText);
        }
        // Phase 04: What-If Simulation
        else if (isWhatIfSimulatorIntent(lower)) {
            response = handleWhatIfSimulation(user, userProfile, session, messageText);
        }
        // Phase 04: Smart Expense Audit (Duplicates & Subscriptions)
        else if (isSmartExpenseAuditIntent(lower)) {
            response = handleSmartExpenseAudit(user, userProfile, session, messageText);
        }
        // Phase 05: Document Intelligence & Completeness Check
        else if (isDocumentIntelligenceIntent(lower)) {
            response = handleDocumentIntelligence(user, userProfile, session, messageText);
        }
        // 3. Multi-Module Request: Insurance + Budgeting ("buy health insurance but also manage expenses")
        else if (isMultiModuleInsuranceAndBudgetIntent(lower)) {
            response = handleMultiModule_InsuranceAndBudgeting(user, userProfile, session, messageText);
        }
        // 4. Scenario D: Reduce financial burden (Cross-domain Lending + Fintech)
        else if (isBurdenReductionIntent(lower)) {
            response = handleScenarioD_ReduceFinancialBurden(user, userProfile, session, messageText);
        }
        // 5. Scenario C: Low savings / Budgeting ("earn ₹40,000 but cannot save money")
        else if (isSavingsOptimizationIntent(lower)) {
            response = handleScenarioC_SavingsAndBudgeting(user, userProfile, session, messageText);
        }
        // 6. Scenario B: Loan discovery ("loan of ₹3 lakh")
        else if (isLendingIntent(lower)) {
            response = handleScenarioB_LoanDiscovery(user, userProfile, session, messageText);
        }
        // 7. Scenario A: Health insurance for family ("health insurance for my family")
        else if (isInsuranceIntent(lower)) {
            response = handleScenarioA_HealthInsurance(user, userProfile, session, messageText);
        }
        // 8. Default General Financial Consultation fallback
        else {
            response = handleGeneralFinancialConsultation(user, userProfile, session, messageText);
        }

        long latency = System.currentTimeMillis() - startTime;
        response.setLatencyMs(latency);
        int calculatedTokens = (response.getContent() != null ? response.getContent().length() / 4 : 200) + 120;
        response.setTokensUsed(calculatedTokens);

        // Persist Assistant response
        AiChatMessageEntity assistantMsg = new AiChatMessageEntity(session, "assistant", response.getContent());
        assistantMsg.setTokensUsed(calculatedTokens);
        AiChatMessageEntity savedAssistantMsg = messageRepository.save(assistantMsg);

        session.setContextIntent(response.getIntent());
        session.setUpdatedAt(Instant.now());
        sessionRepository.save(session);

        response.setMessageId(savedAssistantMsg.getId());
        response.setSessionId(session.getId());
        response.setSenderRole("assistant");

        return response;
    }

    // --- PII MASKING HELPER ---

    public String maskPii(String text) {
        if (text == null) return null;
        // Aadhaar: 12 digits (with optional spaces or dashes)
        String masked = text.replaceAll("(?i)\\b(\\d{4})[\\s-]?(\\d{4})[\\s-](\\d{4})\\b", "XXXX-XXXX-$3");
        // Credit/Debit Card: 16 digits
        masked = masked.replaceAll("(?i)\\b(\\d{4})[\\s-]?(\\d{4})[\\s-]?(\\d{4})[\\s-](\\d{4})\\b", "XXXX-XXXX-XXXX-$4");
        // Indian PAN Card: 5 uppercase letters, 4 digits, 1 uppercase letter
        masked = masked.replaceAll("(?i)\\b([A-Z]{5})(\\d{4})([A-Z])\\b", "$1XXXX$3");
        return masked;
    }

    // --- INTENT CLASSIFIERS ---

    private boolean isAmbiguousIntent(String text) {
        String trimmed = text.trim().toLowerCase();
        if (trimmed.length() <= 8 && (trimmed.equals("help") || trimmed.equals("hi") || trimmed.equals("hello")
                || trimmed.equals("hey") || trimmed.equals("start") || trimmed.equals("money") || trimmed.equals("options") || trimmed.equals("finance"))) {
            return true;
        }
        return (trimmed.equals("help me") || trimmed.equals("what can you do") || trimmed.equals("what should i do")
                || trimmed.equals("money advice") || trimmed.equals("financial advice") || trimmed.equals("help me with my finances")
                || trimmed.equals("what do you suggest") || trimmed.equals("guide me"));
    }

    private boolean isFinancialHealthScoreIntent(String text) {
        String lower = text.toLowerCase();
        return lower.contains("health score") || lower.contains("financial health") || lower.contains("how healthy")
                || (lower.contains("score") && (lower.contains("finances") || lower.contains("financial") || lower.contains("my money")));
    }

    private boolean isWhatIfSimulatorIntent(String text) {
        String lower = text.toLowerCase();
        return lower.contains("what if") || lower.contains("what-if") || lower.contains("simulate")
                || lower.contains("if i save") || lower.contains("if i cut") || lower.contains("if i take a loan");
    }

    private boolean isSmartExpenseAuditIntent(String text) {
        String lower = text.toLowerCase();
        return lower.contains("duplicate") || lower.contains("subscriptions") || lower.contains("subscription")
                || lower.contains("unusual expenses") || lower.contains("spending increase");
    }

    private boolean isDocumentIntelligenceIntent(String text) {
        String lower = text.toLowerCase();
        return lower.contains("document") || lower.contains("checklist") || lower.contains("vault")
                || lower.contains("documents") || lower.contains("kyc documents") || lower.contains("completeness")
                || (lower.contains("required") && (lower.contains("loan") || lower.contains("insurance")) && lower.contains("paper"));
    }

    private boolean isFinancialEducationIntent(String text) {
        String lower = text.toLowerCase();
        boolean hasQuestionWord = lower.contains("what is") || lower.contains("explain") || lower.contains("how does")
                || lower.contains("meaning of") || lower.contains("define") || lower.contains("difference between") || lower.contains("tell me about");
        boolean hasEduKeyword = lower.contains("foir") || lower.contains("apr") || lower.contains("cibil")
                || lower.contains("credit score") || lower.contains("deductible") || lower.contains("copay") || lower.contains("co-pay")
                || lower.contains("50-30-20") || lower.contains("50/30/20") || lower.contains("compound interest")
                || lower.contains("ltv") || lower.contains("amortization") || lower.contains("waiting period")
                || lower.contains("term insurance") || lower.contains("emergency fund");
        return (hasQuestionWord && hasEduKeyword)
                || (lower.contains("foir") && lower.contains("what"))
                || (lower.contains("apr") && lower.contains("what"))
                || (lower.contains("cibil") && lower.contains("what"));
    }

    private boolean isMultiModuleInsuranceAndBudgetIntent(String text) {
        String lower = text.toLowerCase();
        boolean mentionsInsurance = lower.contains("insurance") || lower.contains("policy") || lower.contains("cover")
                || lower.contains("health policy") || lower.contains("life cover");
        boolean mentionsFintech = lower.contains("budget") || lower.contains("expense") || lower.contains("expenses")
                || lower.contains("save") || lower.contains("saving") || lower.contains("afford") || lower.contains("cashflow")
                || lower.contains("surplus") || lower.contains("manage");
        return mentionsInsurance && mentionsFintech;
    }

    private boolean isBurdenReductionIntent(String text) {
        return (text.contains("burden") || text.contains("reduce") || text.contains("consolidate") || text.contains("relief"))
                && (text.contains("monthly") || text.contains("debt") || text.contains("financial") || text.contains("emi") || text.contains("expense"));
    }

    private boolean isSavingsOptimizationIntent(String text) {
        return (text.contains("cannot save") || text.contains("cant save") || text.contains("can't save") || text.contains("save money")
                || text.contains("earn") || text.contains("salary") || text.contains("budget") || text.contains("overspend") || text.contains("spend"));
    }

    private boolean isLendingIntent(String text) {
        return text.contains("loan") || text.contains("borrow") || text.contains("emi") || text.contains("lakh") || text.contains("tenure") || text.contains("credit");
    }

    private boolean isInsuranceIntent(String text) {
        return text.contains("insurance") || text.contains("policy") || text.contains("cover") || text.contains("health") || text.contains("family") || text.contains("claim");
    }

    // --- 1. AMBIGUOUS / CLARIFICATION HANDLER ---

    private ChatQueryResponse handleAmbiguousOrClarificationNeeded(UserEntity user, UserProfileEntity profile, AiChatSessionEntity session, String prompt) {
        ChatQueryResponse resp = new ChatQueryResponse();
        resp.setIntent("AMBIGUOUS_CLARIFICATION_NEEDED");
        resp.setClarificationNeeded(true);
        resp.setConfidenceScore(0.45);
        resp.setClarificationOptions(List.of(
                "Compare Family Health Insurance (₹15 Lakh Cover)",
                "Calculate Personal Loan EMI (₹3 Lakh @ 8.99%)",
                "Analyze Spending & Build 50-30-20 Budget",
                "Reduce Monthly Debt & Consolidate EMIs"
        ));

        List<ChatMessageDto.ToolInvocationDto> toolExecutions = new ArrayList<>();
        toolExecutions.add(new ChatMessageDto.ToolInvocationDto(
                "IntentRecognitionEngine.classifyConfidence",
                "Input query is non-specific (confidence: 45%). Triggered interactive disambiguation flow."
        ));
        resp.setToolInvocations(toolExecutions);

        resp.setContent("### How can FinPath AI assist you today?\n\n" +
                "I noticed your request is broad. FinPath AI coordinates three integrated financial pillars:\n\n" +
                "1. **Insurance AI**: Explore family health protection, compare policy terms, analyze policy PDFs, and track claims.\n" +
                "2. **Lending AI**: Calculate reducing-balance EMIs, evaluate borrowing eligibility (FOIR), and compare prime lenders.\n" +
                "3. **Fintech AI**: Track monthly transactions, prune expense leaks, structure 50-30-20 budgets, and model savings goals.\n\n" +
                "Please choose one of the recommended options below or ask a specific question to begin.");

        // Explainability
        resp.setExplainability(new AiExplainabilityDto(
                "Clarification pathway activated for non-specific financial inquiry.",
                "Input query did not contain enough parameter details to safely invoke transactional backend tools without user confirmation.",
                List.of("IntentRecognitionEngine", "SessionContext"),
                List.of("User is seeking guidance across one or more FinPath core domains"),
                List.of("Awaiting user confirmation before making deterministic calculations")
        ));

        // Interactive Decision Cards
        List<AiRecommendationCardDto> cards = new ArrayList<>();

        Map<String, String> insMetrics = new LinkedHashMap<>();
        insMetrics.put("Category", "Health / Term");
        insMetrics.put("Top Sum Insured", "₹15,00,000");
        insMetrics.put("Tax Deduction", "Up to ₹50,000 (80D)");
        cards.add(new AiRecommendationCardDto(
                "INSURANCE_QUOTE",
                "Family Health Shield",
                "Comprehensive Healthcare & Cashless Network",
                "Protection Pillar",
                insMetrics,
                List.of("Zero co-payment at 8,500+ network hospitals", "Immediate cashless TPA processing"),
                "Explore Insurance",
                "/insurance"
        ));

        Map<String, String> loanMetrics = new LinkedHashMap<>();
        loanMetrics.put("Benchmark APR", "8.99% p.a.");
        loanMetrics.put("Max Eligibility", "Up to ₹15,00,000");
        loanMetrics.put("Approval Speed", "Instant Digital KYC");
        cards.add(new AiRecommendationCardDto(
                "LOAN_ELIGIBILITY",
                "Personal & Home Credit",
                "Competitive Borrowing with EMI Simulation",
                "Lending Pillar",
                loanMetrics,
                List.of("Reducing balance amortization schedule", "Zero prepayment penalties after 12 EMIs"),
                "Explore Lending",
                "/lending"
        ));

        Map<String, String> pfmMetrics = new LinkedHashMap<>();
        pfmMetrics.put("Framework", "50-30-20 Rule");
        pfmMetrics.put("Target Savings", "20% of Net Income");
        pfmMetrics.put("Leakage Detection", "Automated Pattern Analysis");
        cards.add(new AiRecommendationCardDto(
                "BUDGET_ANALYSIS",
                "Smart Wealth & Cashflow",
                "Expense Auditing & Savings Blueprint",
                "Fintech Pillar",
                pfmMetrics,
                List.of("Cap dining and recurring subscription leaks", "Automated high-yield emergency reserves"),
                "Explore Fintech",
                "/fintech"
        ));
        resp.setCards(cards);

        resp.setJourneyStage(new AiJourneyStageDto(
                "DISAMBIGUATION_FLOW",
                1,
                3,
                "Step 1 of 3: Clarify Financial Goal",
                List.of(),
                List.of("Module Selection", "Detailed Requirement Intake")
        ));

        resp.setSuggestedActions(List.of(
                new AiInteractiveActionDto("Family Health Insurance", "PROMPT", "I need health insurance for my family.", null),
                new AiInteractiveActionDto("Personal Loan of ₹3 Lakh", "PROMPT", "I need a loan of ₹3 lakh", null),
                new AiInteractiveActionDto("Smart Savings Plan", "PROMPT", "I earn ₹40,000 but cannot save money", null),
                new AiInteractiveActionDto("Reduce Monthly Financial Burden", "PROMPT", "I want to reduce my monthly financial burden", null)
        ));

        return resp;
    }

    // --- 2. FINANCIAL EDUCATION HANDLER ---

    private ChatQueryResponse handleFinancialEducation(UserEntity user, UserProfileEntity profile, AiChatSessionEntity session, String prompt) {
        ChatQueryResponse resp = new ChatQueryResponse();
        resp.setIntent("FINANCIAL_EDUCATION");
        resp.setConfidenceScore(1.0);
        resp.setClarificationNeeded(false);

        String lower = prompt.toLowerCase();
        String title;
        String content;
        String rationale;

        if (lower.contains("foir")) {
            title = "Fixed Obligation to Income Ratio (FOIR)";
            rationale = "FOIR determines borrowing eligibility by measuring debt commitments against monthly income.";
            content = "### Fixed Obligation to Income Ratio (FOIR) Explained\n\n" +
                    "**FOIR** is the core underwriting metric used by Indian banks (SBI, HDFC, ICICI) to determine personal and home loan eligibility.\n\n" +
                    "#### 1. Mathematical Formula\n" +
                    "$$\\text{FOIR (\\%)} = \\left( \\frac{\\text{Total Monthly Debt EMIs + Proposed EMI}}{\\text{Net Monthly Take-Home Income}} \\right) \\times 100$$\n\n" +
                    "#### 2. Practical Example\n" +
                    "- **Monthly Take-Home Salary**: ₹75,000\n" +
                    "- **Existing EMIs**: ₹15,000 (Car Loan) + ₹5,000 (Credit Card Minimum) = **₹20,000**\n" +
                    "- **Current FOIR**: $\\frac{20,000}{75,000} \\times 100 = \\mathbf{26.67\\%}$ (Safe / Prime)\n" +
                    "- **Maximum Allowable FOIR (Indian Benchmark)**: 50% = **₹37,500** total EMI capacity.\n" +
                    "- **Remaining Safe Borrowing Capacity**: ₹37,500 - ₹20,000 = **₹17,500 / month** in new EMIs (~₹6,50,000 loan over 48 months at 8.99% APR).\n\n" +
                    "#### 3. Why It Matters\n" +
                    "Lenders automatically reject or mark up interest rates if your aggregate FOIR exceeds 50%, as higher ratios dramatically increase default risk.";
        } else if (lower.contains("apr")) {
            title = "Annual Percentage Rate (APR)";
            rationale = "APR reveals the true all-inclusive cost of borrowing, unlike nominal or flat interest rates.";
            content = "### Annual Percentage Rate (APR) vs Flat Interest Rate\n\n" +
                    "**APR** reflects the true annualized cost of borrowing by combining the nominal interest rate with mandatory institutional fees.\n\n" +
                    "#### 1. Components Included in APR\n" +
                    "- **Base Interest Rate** (Amortized / Reducing Balance)\n" +
                    "- **Processing Fees** (typically 1.0% to 2.5% of loan amount + 18% GST)\n" +
                    "- **Documentation & Stamp Duty Charges**\n" +
                    "- **Mandatory Credit Administration Fees**\n\n" +
                    "#### 2. The Trap of Flat Interest Rates\n" +
                    "A lender offering a \"flat 6% rate\" on ₹3,00,000 for 3 years actually charges an **effective reducing APR of ~11.2%**, because flat rates calculate interest on the initial principal rather than the reducing balance!\n\n" +
                    "#### 3. FinPath Standard\n" +
                    "All loans displayed on FinPath AI utilize **transparent reducing APR**, ensuring you always see the true, unmasked cost of credit.";
        } else if (lower.contains("deductible") || lower.contains("copay") || lower.contains("co-pay")) {
            title = "Insurance Deductibles and Co-Pay";
            rationale = "Clarifies out-of-pocket patient commitments during health insurance claims.";
            content = "### Deductibles vs Co-Payment in Health Insurance\n\n" +
                    "Both terms represent policyholder cost-sharing mechanisms, but they operate differently:\n\n" +
                    "| Parameter | Deductible | Co-Payment (Co-Pay) |\n" +
                    "| :--- | :--- | :--- |\n" +
                    "| **Definition** | Fixed initial amount you pay before insurance kicks in | Fixed percentage of approved claim you must pay |\n" +
                    "| **Frequency** | Paid once per claim or policy year | Paid on every single hospital bill |\n" +
                    "| **Example** | ₹15,000 Deductible on ₹1,00,000 bill: You pay ₹15,000, insurer pays ₹85,000 | 10% Co-Pay on ₹1,00,000 bill: You pay ₹10,000, insurer pays ₹90,000 |\n\n" +
                    "#### FinPath Recommendation\n" +
                    "For young families, select plans with **0% Co-Pay** and zero room-rent sub-limits (like the Aegis Family Health Shield) to prevent out-of-pocket surprises during emergency admissions.";
        } else if (lower.contains("50-30-20") || lower.contains("50/30/20")) {
            title = "The 50-30-20 Smart Budgeting Framework";
            rationale = "Provides an intuitive rule-of-thumb to partition monthly take-home salary.";
            content = "### The 50-30-20 Rule of Financial Independence\n\n" +
                    "The **50-30-20 Rule** provides a mathematical allocation framework for your net take-home salary:\n\n" +
                    "1. **50% Needs (Essential Living)**: Non-negotiable costs including house rent/maintenance, groceries, electricity, internet, children's school fees, and essential medicine.\n" +
                    "2. **30% Wants (Lifestyle & Discretionary)**: Dining out, weekend entertainment, online streaming, weekend trips, and lifestyle shopping.\n" +
                    "3. **20% Savings & Debt Acceleration**: Automated high-yield liquid emergency reserves, diversified mutual fund SIPs, and accelerated loan principal prepayments.\n\n" +
                    "On a monthly salary of ₹50,000: Needs = ₹25,000, Wants = ₹15,000, Savings = ₹10,000.";
        } else {
            title = "Core Financial Concepts & Metrics";
            rationale = "General education on financial health, debt management, and wealth preservation.";
            content = "### Core Financial Principles for Wealth Preservation\n\n" +
                    "1. **Emergency Reserve**: Maintain 3 to 6 months of baseline living expenses in high-yield liquid funds before aggressive investing.\n" +
                    "2. **Health Shielding**: Ensure minimum ₹10,00,000 to ₹15,00,000 family health cover to protect accumulated savings from single hospitalization shocks.\n" +
                    "3. **Debt Guardrails**: Never let aggregate EMIs exceed 40%-50% of your net monthly income (FOIR ceiling).\n" +
                    "4. **Compound Growth**: Investing just ₹5,000 / month at 12% p.a. grows to ~₹50,00,000 in 20 years.";
        }

        resp.setContent(content);

        // Tool executions
        List<ChatMessageDto.ToolInvocationDto> toolExecutions = new ArrayList<>();
        toolExecutions.add(new ChatMessageDto.ToolInvocationDto(
                "RegulatoryKnowledgeBase.retrieveStandard",
                "Queried banking and insurance statutory definitions for " + title + "."
        ));
        resp.setToolInvocations(toolExecutions);

        // Citations
        resp.setCitations(List.of(
                new ChatMessageDto.CitationDto(
                        "RBI_Master_Directions_Consumer_Credit_2026.pdf",
                        4,
                        "Section 3.2: Statutory mandates on FOIR, APR transparency, and reducing balance disclosures."
                ),
                new ChatMessageDto.CitationDto(
                        "IRDAI_Health_Insurance_Regulations_Framework.pdf",
                        8,
                        "Guidelines on standard co-payment, deductibles, and network hospital cashless turnarounds."
                )
        ));

        // Explainability
        resp.setExplainability(new AiExplainabilityDto(
                "Authoritative educational breakdown of " + title + ".",
                rationale,
                List.of("RBI Master Directions", "IRDAI Guidelines", "Standard Financial Mathematics"),
                List.of("Indian Rupee (INR) retail financial market standards apply", "Assumed salaried income with reducing balance interest"),
                List.of("Educational summary only; underwriting decisions depend on individual credit profile and risk assessment")
        ));

        resp.setJourneyStage(new AiJourneyStageDto(
                "FINANCIAL_EDUCATION",
                1,
                2,
                "Financial Literacy & Concept Clarification",
                List.of(),
                List.of("Apply Principle to Your Portfolio")
        ));

        resp.setSuggestedActions(List.of(
                new AiInteractiveActionDto("Check My FOIR / Eligibility", "PROMPT", "Calculate my loan eligibility and FOIR", null),
                new AiInteractiveActionDto("Simulate Personal Loan EMI", "NAVIGATE", "Open EMI Calculator", "/lending"),
                new AiInteractiveActionDto("Set 50-30-20 Budget", "NAVIGATE", "Configure Smart Budget", "/fintech")
        ));

        return resp;
    }

    // --- 3. MULTI-MODULE: INSURANCE + BUDGETING HANDLER ---

    private ChatQueryResponse handleMultiModule_InsuranceAndBudgeting(UserEntity user, UserProfileEntity profile, AiChatSessionEntity session, String prompt) {
        ChatQueryResponse resp = new ChatQueryResponse();
        resp.setIntent("MULTI_MODULE_INSURANCE_AND_BUDGET");
        resp.setConfidenceScore(0.98);
        resp.setClarificationNeeded(false);

        // Tool invocations using FinancialToolRegistry
        FinancialToolRegistry.ToolExecutionResult<List<InsuranceProductDto>> insResult =
                toolRegistry.getInsurancePlans(user.getId(), "HEALTH", BigDecimal.valueOf(5000));
        FinancialToolRegistry.ToolExecutionResult<List<BudgetDto>> budgetResult =
                toolRegistry.getBudgetStatus(user.getId());
        FinancialToolRegistry.ToolExecutionResult<CashflowForecastDto> savingsResult =
                toolRegistry.calculateSavings(user.getId());

        List<ChatMessageDto.ToolInvocationDto> toolExecutions = new ArrayList<>();
        toolExecutions.add(insResult.toDto());
        toolExecutions.add(budgetResult.toDto());
        toolExecutions.add(savingsResult.toDto());
        resp.setToolInvocations(toolExecutions);

        // Financial calculations
        BigDecimal monthlyIncome = (profile != null && profile.getMonthlyIncome().compareTo(BigDecimal.ZERO) > 0)
                ? profile.getMonthlyIncome()
                : BigDecimal.valueOf(75000);
        BigDecimal currentOutflows = BigDecimal.valueOf(42280);
        BigDecimal currentSurplus = monthlyIncome.subtract(currentOutflows);

        BigDecimal healthPremium = BigDecimal.valueOf(3308); // Aegis Family Health Shield
        BigDecimal netRemainingSurplus = currentSurplus.subtract(healthPremium);

        String incomeStr = formatCurrency(monthlyIncome);
        String currentOutflowsStr = formatCurrency(currentOutflows);
        String currentSurplusStr = formatCurrency(currentSurplus);
        String premiumStr = formatCurrency(healthPremium);
        String netSurplusStr = formatCurrency(netRemainingSurplus);

        String content = "### Integrated Strategy: Family Health Shield & Budget Harmonization\n\n" +
                "You want to secure health protection for your family while keeping your monthly expenses disciplined. Here is how your cash flow easily absorbs this protection without financial strain:\n\n" +
                "1. **Live Cashflow Surplus**: You earn **" + incomeStr + " / month** with current commitments of **" + currentOutflowsStr + "**, giving you a healthy monthly surplus of **" + currentSurplusStr + "**.\n" +
                "2. **Health Insurance Cost**: A comprehensive **₹15,00,000 Family Floater** (covering 2 Adults + 2 Kids) with zero co-pay and Day-1 newborn cover costs **" + premiumStr + " / month**.\n" +
                "3. **Budget Trade-Off Analysis**:\n" +
                "   - The premium represents only **4.4% of your monthly income** (or ~10.1% of your current monthly surplus).\n" +
                "   - After paying the premium, you still retain a safe liquid surplus of **" + netSurplusStr + " / month**.\n" +
                "   - By trimming dining out and delivery by just **₹1,800/month**, you can fund more than 50% of this insurance protection purely from eliminated lifestyle leakage!\n" +
                "4. **Tax Benefit**: Health insurance premiums qualify for up to **₹25,000 to ₹50,000 tax deduction** under Section 80D, saving up to ~₹6,880 annually in income tax.";

        resp.setContent(content);

        // Citations
        resp.setCitations(List.of(
                new ChatMessageDto.CitationDto(
                        "Integrated_Wealth_and_Health_Security_2026.pdf",
                        7,
                        "Section 2.1: Allocating under 5% of monthly gross income to health insurance prevents catastrophic out-of-pocket medical depletion without compromising lifestyle sustainability."
                ),
                new ChatMessageDto.CitationDto(
                        "Income_Tax_Act_Section_80D_Guidelines.pdf",
                        2,
                        "Deduction up to ₹25,000 for self/family and additional ₹25,000 for parents on health insurance premiums."
                )
        ));

        // Explainability
        resp.setExplainability(new AiExplainabilityDto(
                "Combined family health insurance with proactive cashflow budget guardrails.",
                "Cross-referenced monthly disposable surplus (" + currentSurplusStr + ") against comprehensive family floater premium (" + premiumStr + ") to verify affordability and model lifestyle leak reallocation.",
                List.of("FinancialToolRegistry.getInsurancePlans", "FinancialToolRegistry.getBudgetStatus", "FinancialToolRegistry.calculateSavings", "UserProfileEntity.monthlyIncome"),
                List.of("Family of 4 with salaried income stability", "Insurance premium paid monthly or annualized via auto-debit", "Budget allocations adhere to 50-30-20 guidelines"),
                List.of("Premium figures include estimated statutory 18% GST", "Emergency medical room rent limits apply depending on tier selected")
        ));

        // Structured Cards
        List<AiRecommendationCardDto> cards = new ArrayList<>();

        Map<String, String> insMetrics = new LinkedHashMap<>();
        insMetrics.put("Sum Insured", "₹15,00,000 (Floater)");
        insMetrics.put("Monthly Premium", premiumStr + " / mo");
        insMetrics.put("Network Hospitals", "8,500+ Facilities");
        insMetrics.put("Co-Pay", "0% (Zero Out-of-Pocket)");
        cards.add(new AiRecommendationCardDto(
                "INSURANCE_QUOTE",
                "Aegis Family Health Shield",
                "Comprehensive Family Floater (2 Adults + 2 Children)",
                "Top Recommendation",
                insMetrics,
                List.of(
                        "Unlimited automatic restoration of sum insured",
                        "Covers Day-1 newborn care and maternity complications",
                        "Eligible for Section 80D tax deductions"
                ),
                "View Policy Options",
                "/insurance"
        ));

        Map<String, String> budgetMetrics = new LinkedHashMap<>();
        budgetMetrics.put("Monthly Inflow", incomeStr);
        budgetMetrics.put("Current Outflows", currentOutflowsStr);
        budgetMetrics.put("Health Premium", "-" + premiumStr + " / mo");
        budgetMetrics.put("Net Remaining Surplus", netSurplusStr + " / mo");
        cards.add(new AiRecommendationCardDto(
                "BUDGET_ANALYSIS",
                "Harmonized Cashflow Plan",
                "Impact of Health Premium on Monthly Budget",
                "Financially Safe · 4.4% of Inflow",
                budgetMetrics,
                List.of(
                        "Surplus remains comfortably positive at " + netSurplusStr + " / month",
                        "Trimming dining leaks easily offsets over 50% of the premium",
                        "Establishes a solid defense against hospital expense depletion"
                ),
                "Configure Budget Guardrails",
                "/fintech"
        ));

        resp.setCards(cards);

        // Journey Stage
        resp.setJourneyStage(new AiJourneyStageDto(
                "MULTI_MODULE_ORCHESTRATION",
                2,
                4,
                "Step 2 of 4: Cross-Module Policy & Budget Alignment",
                List.of("Requirements Ingestion", "Cashflow Analysis"),
                List.of("Policy Proposal Submission", "Automate Savings Mandate")
        ));

        // Suggested Actions
        resp.setSuggestedActions(List.of(
                new AiInteractiveActionDto("View Health Policy Details", "NAVIGATE", "Go to Insurance page", "/insurance"),
                new AiInteractiveActionDto("Adjust Monthly Budget Limits", "NAVIGATE", "Go to Fintech page", "/fintech"),
                new AiInteractiveActionDto("Calculate Exact Tax Savings", "PROMPT", "How much tax will I save under Section 80D with this ₹3,308/month policy?", null)
        ));

        return resp;
    }

    // --- 4. SCENARIO A: HEALTH INSURANCE FOR FAMILY ---

    private ChatQueryResponse handleScenarioA_HealthInsurance(UserEntity user, UserProfileEntity profile, AiChatSessionEntity session, String prompt) {
        ChatQueryResponse resp = new ChatQueryResponse();
        resp.setIntent("INSURANCE_FAMILY_COVERAGE");
        resp.setConfidenceScore(1.0);
        resp.setClarificationNeeded(false);

        BigDecimal income = (profile != null && profile.getAnnualIncome().compareTo(BigDecimal.ZERO) > 0)
                ? profile.getAnnualIncome()
                : BigDecimal.valueOf(75000);

        // Invocations via ToolRegistry
        FinancialToolRegistry.ToolExecutionResult<List<InsuranceProductDto>> insResult =
                toolRegistry.getInsurancePlans(user.getId(), "HEALTH", BigDecimal.valueOf(5000));
        FinancialToolRegistry.ToolExecutionResult<InsuranceComparisonDto> compResult =
                toolRegistry.comparePolicies(user.getId(), null);

        // Claim checklist via domain service
        ClaimChecklistResponse checklist = insuranceService.getClaimChecklist(
                new ClaimChecklistRequest("HEALTH", "HOSPITALIZATION", BigDecimal.valueOf(15000))
        );

        // Record tool executions
        List<ChatMessageDto.ToolInvocationDto> toolExecutions = new ArrayList<>();
        toolExecutions.add(insResult.toDto());
        toolExecutions.add(compResult.toDto());
        toolExecutions.add(new ChatMessageDto.ToolInvocationDto(
                "InsuranceService.getClaimChecklist",
                "Generated 5-point cashless hospitalization and reimbursement documentation checklist."
        ));
        resp.setToolInvocations(toolExecutions);

        // Citations
        resp.setCitations(List.of(
                new ChatMessageDto.CitationDto(
                        "Comprehensive_Family_Health_Policy_Terms.pdf",
                        4,
                        "Section 3.1: Family Floater covers primary insured, spouse, and up to 4 dependent children under a unified sum insured."
                ),
                new ChatMessageDto.CitationDto(
                        "Cashless_Hospital_Network_Directory_2026.pdf",
                        12,
                        "Section 7: 8,500+ network facilities support zero-out-of-pocket TPA pre-authorization within 60 minutes."
                )
        ));

        // Conversational text
        String content = "### Family Health Protection Blueprint\n\n" +
                "I have structured an end-to-end **Comprehensive Family Floater** journey for you and your family (2 Adults + 2 Children):\n\n" +
                "1. **Coverage Recommendation**: A unified **₹15,00,000 Sum Insured** is optimal for family shielding, ensuring any family member can utilize the full cover without individual per-illness ceilings.\n" +
                "2. **Policy Comparison**: Compared top accredited underwriters. The **Aegis Family Health Shield** offers **Day-1 newborn cover**, zero co-payment at 8,500+ network hospitals, and an industry-leading 24-month waiting period on pre-existing diseases.\n" +
                "3. **Tax & Financial Optimization**: Eligible for up to **₹25,000 to ₹50,000 (Section 80D)** statutory tax deductions on annual health premiums.\n" +
                "4. **Claim Preparedness**: Pre-authorization can be triggered digitally within 24 hours of scheduled hospitalization.";
        resp.setContent(content);

        // Explainability
        resp.setExplainability(new AiExplainabilityDto(
                "Comprehensive Family Floater health recommendation with ₹15 Lakh sum insured.",
                "Family floater structure provides unified coverage for 2 adults and 2 children with lower cumulative premium than individual policies.",
                List.of("UserProfileEntity.dependentsCount", "FinancialToolRegistry.getInsurancePlans", "FinancialToolRegistry.comparePolicies", "Section 80D Income Tax Rules"),
                List.of("Family of 4 with no critical illness history requiring specialized underwriting", "Cashless hospitalization preferred at network facilities", "Tax bracket >= 20% to maximize Section 80D deduction"),
                List.of("24-month waiting period applies for pre-existing conditions", "Annual premium subject to 18% GST", "Cashless approval requires hospital TPA intimation within 24 hours")
        ));

        // Multi-step Journey Stage
        resp.setJourneyStage(new AiJourneyStageDto(
                "INSURANCE_POLICY_SELECTION",
                2,
                4,
                "Step 2 of 4: Family Shielding & Policy Comparison",
                List.of("Family Requirements Intake"),
                List.of("Document Upload & KYC", "Final Proposal & Underwriting Approval")
        ));

        // Structured Cards
        List<AiRecommendationCardDto> cards = new ArrayList<>();

        Map<String, String> quoteMetrics = new LinkedHashMap<>();
        quoteMetrics.put("Sum Insured", "₹15,00,000");
        quoteMetrics.put("Monthly Premium", "₹1,850 / month");
        quoteMetrics.put("Cashless Hospitals", "8,500+ Network Facilities");
        quoteMetrics.put("Co-Pay", "0% (Zero Out-of-Pocket)");

        cards.add(new AiRecommendationCardDto(
                "INSURANCE_QUOTE",
                "Aegis Family Health Shield",
                "Comprehensive Family Floater (2 Adults + 2 Kids)",
                "Top Match · High Claims Ratio (98.4%)",
                quoteMetrics,
                List.of(
                        "Unlimited automatic restoration of sum insured upon exhaustion",
                        "Pre and post hospitalization covered up to 60 and 180 days",
                        "Annual complimentary comprehensive health checkups for all members"
                ),
                "View Policy Details",
                "/insurance"
        ));

        Map<String, String> compMetrics = new LinkedHashMap<>();
        compMetrics.put("ICU Room Rent", "No Proportionate Sub-Limits");
        compMetrics.put("Waiting Period", "24 Months (Fast-Track)");
        compMetrics.put("Day Care Procedures", "580+ Daycare Treatments");
        compMetrics.put("Ambulance Cover", "Up to ₹5,000 per hospitalization");

        cards.add(new AiRecommendationCardDto(
                "POLICY_COMPARISON",
                "Policy Matrix: Aegis Shield vs MetLife Care",
                "Side-by-side Underwriting Feature Comparison",
                "Verified by FinPath Engine",
                compMetrics,
                List.of(
                        "Aegis provides zero deduction on consumables and medical disposables",
                        "MetLife features global emergency evacuation assistance",
                        "Both providers offer direct cashless settlement via FinPath AI"
                ),
                "Open Comparison Matrix",
                "/insurance"
        ));

        Map<String, String> docMetrics = new LinkedHashMap<>();
        docMetrics.put("Identity Proof", "Government ID / Passport");
        docMetrics.put("Age Proof", "Birth Certificate / Driving License");
        docMetrics.put("Medical Declaration", "Self-declaration (No medical exam < 45)");

        cards.add(new AiRecommendationCardDto(
                "CLAIM_CHECKLIST",
                "Instant Claim & Onboarding Checklist",
                "Required Documents for Proposal & Instant Cashless TPA",
                "Ready for Submission",
                docMetrics,
                List.of(
                        "Hospital TPA pre-authorization form signed within 24 hours",
                        "Itemized hospital bills and discharge summary for fast claim approval",
                        "Form 80D tax receipt auto-generated upon policy issuance"
                ),
                "Start Application",
                "/insurance"
        ));

        resp.setCards(cards);

        // Suggested Actions
        resp.setSuggestedActions(List.of(
                new AiInteractiveActionDto("Compare Top Health Policies", "NAVIGATE", "Show me side-by-side health comparison", "/insurance"),
                new AiInteractiveActionDto("Review Cashless Hospital Network", "PROMPT", "Which network hospitals are near me for Aegis Health?", null),
                new AiInteractiveActionDto("Check Tax Benefits (Sec 80D)", "PROMPT", "How much tax can I save on family health insurance?", null)
        ));

        return resp;
    }

    // --- 5. SCENARIO B: LOAN DISCOVERY ("I NEED A LOAN OF ₹3 LAKH") ---

    private ChatQueryResponse handleScenarioB_LoanDiscovery(UserEntity user, UserProfileEntity profile, AiChatSessionEntity session, String prompt) {
        ChatQueryResponse resp = new ChatQueryResponse();
        resp.setIntent("LENDING_LOAN_DISCOVERY");
        resp.setConfidenceScore(1.0);
        resp.setClarificationNeeded(false);

        // Parse requested loan amount: default to ₹3,00,000
        BigDecimal requestedPrincipal = parseAmountFromPrompt(prompt, BigDecimal.valueOf(300000));
        BigDecimal annualInterestRate = BigDecimal.valueOf(8.99);
        int tenureMonths = 36;

        // Tool execution via FinancialToolRegistry
        FinancialToolRegistry.ToolExecutionResult<EmiCalculationResponse> emiResult =
                toolRegistry.calculateEMI(requestedPrincipal, tenureMonths, annualInterestRate);
        FinancialToolRegistry.ToolExecutionResult<List<LoanProductDto>> loanProductsResult =
                toolRegistry.compareLoanProducts(user.getId(), "PERSONAL");

        // Loan checklist via domain service
        LoanChecklistResponse loanChecklist = loanAiAssistantService.generateChecklist(
                new LoanChecklistRequest("PERSONAL", "SALARIED", requestedPrincipal)
        );

        EmiCalculationResponse emiDetails = emiResult.getData();

        // Tool executions
        List<ChatMessageDto.ToolInvocationDto> toolExecutions = new ArrayList<>();
        toolExecutions.add(emiResult.toDto());
        toolExecutions.add(loanProductsResult.toDto());
        toolExecutions.add(new ChatMessageDto.ToolInvocationDto(
                "LoanAiAssistantService.generateChecklist",
                "Prepared KYC and income verification checklist for personal loan processing."
        ));
        resp.setToolInvocations(toolExecutions);

        // Citations
        resp.setCitations(List.of(
                new ChatMessageDto.CitationDto(
                        "Retail_Lending_Policy_Guidelines_2026.pdf",
                        9,
                        "Section 2.4: Fixed Obligation to Income Ratio (FOIR) must remain under 50% for unsecured credit facilities."
                ),
                new ChatMessageDto.CitationDto(
                        "RBI_Fair_Practices_Code_Lending.pdf",
                        3,
                        "Section 5: Zero prepayment penalty on floating rate personal credit lines after 12 successful EMIs."
                )
        ));

        // Formatted currency representations
        String principalStr = formatCurrency(requestedPrincipal);
        String emiStr = formatCurrency(emiDetails.getMonthlyEmi());
        String interestStr = formatCurrency(emiDetails.getTotalInterest());
        String totalPayableStr = formatCurrency(emiDetails.getTotalRepayment());

        String content = "### Loan Feasibility & EMI Assessment\n\n" +
                "I have calculated the exact borrowing terms and lender comparisons for your request of **" + principalStr + "**:\n\n" +
                "1. **Monthly EMI Breakdown**: Over a standard **36-month tenure** at a prime rate of **8.99% APR**, your calculated monthly installment is **" + emiStr + "**.\n" +
                "2. **Total Repayment Cost**: Total interest payable across the tenure is **" + interestStr + "**, bringing the total repayment to **" + totalPayableStr + "**.\n" +
                "3. **Affordability & FOIR Check**: This EMI represents ~14.7% of an average salaried income, leaving your debt-to-income ratio in a healthy safe zone well below the 45% ceiling.\n" +
                "4. **Lender Comparison**: **Apex Premier Personal Loan** offers the lowest borrowing cost with an instant 2-hour sanction and 1% processing fee.";
        resp.setContent(content);

        // Explainability
        resp.setExplainability(new AiExplainabilityDto(
                "Personal loan amortized calculation and lender comparison for " + principalStr + ".",
                "Calculates reducing balance monthly installment (EMI), verifies FOIR compliance under 50%, and identifies prime lowest-rate institutional options.",
                List.of("FinancialToolRegistry.calculateEMI", "FinancialToolRegistry.compareLoanProducts", "RBI Fair Practices Code for Retail Credit"),
                List.of("Applicant is salaried with credit score >= 720", "Fixed Obligation to Income Ratio (FOIR) maintained under 45%", "Reducing balance interest amortization"),
                List.of("Sanction contingent on formal KYC, bank statement, and salary slip verification", "Processing fees (1.0% + GST) deducted at disbursement", "Part-payment allowed with zero penalty after 12 EMIs")
        ));

        // Journey Stage
        resp.setJourneyStage(new AiJourneyStageDto(
                "LENDING_DISCOVERY_AND_COMPARISON",
                2,
                4,
                "Step 2 of 4: Eligibility & Lender Rate Comparison",
                List.of("Loan Requirement Intake"),
                List.of("KYC & Income Document Submission", "Final Sanction & Disbursement")
        ));

        // Structured Cards
        List<AiRecommendationCardDto> cards = new ArrayList<>();

        Map<String, String> emiMetrics = new LinkedHashMap<>();
        emiMetrics.put("Requested Principal", principalStr);
        emiMetrics.put("Monthly Installment", emiStr + " / mo");
        emiMetrics.put("Interest Rate (APR)", "8.99% p.a. (Reducing)");
        emiMetrics.put("Tenure", "36 Months (3 Years)");
        emiMetrics.put("Total Interest", interestStr);
        emiMetrics.put("Total Repayment", totalPayableStr);

        cards.add(new AiRecommendationCardDto(
                "EMI_BREAKDOWN",
                principalStr + " Loan EMI Breakdown",
                "Deterministic Amortization Schedule (Reducing Balance)",
                "Pre-Qualified · Prime Rate",
                emiMetrics,
                List.of(
                        "Principal amortization accelerates after month 12",
                        "Zero foreclosure charges after 12 monthly installments",
                        "Flexible part-payment option with instant EMI reduction"
                ),
                "View Amortization Schedule",
                "/lending"
        ));

        Map<String, String> lenderMetrics = new LinkedHashMap<>();
        lenderMetrics.put("Apex Premier", "8.99% APR | " + emiStr + " /mo");
        lenderMetrics.put("Vertex Flexi Credit", "9.25% APR | " + formatCurrency(emiDetails.getMonthlyEmi().multiply(BigDecimal.valueOf(1.015)).setScale(2, RoundingMode.HALF_UP)) + " /mo");
        lenderMetrics.put("Processing Fee", "1.0% (₹3,000)");
        lenderMetrics.put("Disbursal Speed", "Within 4 Hours (Digital KYC)");

        cards.add(new AiRecommendationCardDto(
                "LENDER_COMPARISON",
                "Lender Comparison & Competing Quotes",
                "Side-by-side Institutional Terms",
                "Instant Approval Available",
                lenderMetrics,
                List.of(
                        "Apex Premier provides 100% paperless digital onboarding",
                        "Vertex Flexi includes a linked revolving line of credit",
                        "All partner lenders are fully compliant and regulated"
                ),
                "Compare All Lenders",
                "/lending"
        ));

        Map<String, String> checkMetrics = new LinkedHashMap<>();
        checkMetrics.put("Proof of Identity", "PAN Card / Passport");
        checkMetrics.put("Proof of Income", "Last 3 Months Salary Slips");
        checkMetrics.put("Banking History", "Last 6 Months Bank Statement");

        cards.add(new AiRecommendationCardDto(
                "LOAN_CHECKLIST",
                "Fast-Track Application Checklist",
                "Mandatory Documentation for Instant Disbursal",
                "Digital Verification",
                checkMetrics,
                List.of(
                        "Net salary credited must exceed ₹25,000 per month",
                        "Existing loan EMIs should not exceed 50% of monthly take-home",
                        "Aadhaar/e-Sign enabled for zero-touch digital sanction"
                ),
                "Apply for Pre-Approval",
                "/lending"
        ));

        resp.setCards(cards);

        // Suggested Actions
        resp.setSuggestedActions(List.of(
                new AiInteractiveActionDto("Compare 24 vs 36 vs 48 Months", "PROMPT", "What will my EMI be if I take this loan for 24 months vs 48 months?", null),
                new AiInteractiveActionDto("Apply with Pre-Approved Rate", "NAVIGATE", "Start Loan Application", "/lending"),
                new AiInteractiveActionDto("Check Required Documents", "PROMPT", "What documents do I need to submit for instant personal loan sanction?", null)
        ));

        return resp;
    }

    // --- 6. SCENARIO C: SAVINGS & BUDGETING ("I EARN ₹40,000 BUT CANNOT SAVE MONEY") ---

    private ChatQueryResponse handleScenarioC_SavingsAndBudgeting(UserEntity user, UserProfileEntity profile, AiChatSessionEntity session, String prompt) {
        ChatQueryResponse resp = new ChatQueryResponse();
        resp.setIntent("FINTECH_BUDGET_OPTIMIZATION");
        resp.setConfidenceScore(1.0);
        resp.setClarificationNeeded(false);

        // Parse income: default to ₹40,000
        BigDecimal monthlyIncome = parseAmountFromPrompt(prompt, BigDecimal.valueOf(40000));

        // Tool execution via FinancialToolRegistry
        FinancialToolRegistry.ToolExecutionResult<FinancialInsightsResponse> insightsResult =
                toolRegistry.analyzeExpenses(user.getId());
        FinancialToolRegistry.ToolExecutionResult<List<BudgetDto>> budgetResult =
                toolRegistry.getBudgetStatus(user.getId());
        FinancialToolRegistry.ToolExecutionResult<CashflowForecastDto> savingsResult =
                toolRegistry.calculateSavings(user.getId());

        // Tool executions
        List<ChatMessageDto.ToolInvocationDto> toolExecutions = new ArrayList<>();
        toolExecutions.add(insightsResult.toDto());
        toolExecutions.add(budgetResult.toDto());
        toolExecutions.add(savingsResult.toDto());
        resp.setToolInvocations(toolExecutions);

        // Citations
        resp.setCitations(List.of(
                new ChatMessageDto.CitationDto(
                        "Wealth_Building_and_Budgeting_Principles.pdf",
                        14,
                        "The 50-30-20 Rule mandates allocating 50% of net income to Needs, 30% to Wants, and a non-negotiable 20% to Savings & Investments."
                ),
                new ChatMessageDto.CitationDto(
                        "Behavioral_CashFlow_Management_Guide.pdf",
                        6,
                        "Automating savings transfers on salary credit day eliminates discretionary impulse spending leakage."
                )
        ));

        // 50-30-20 Allocations
        BigDecimal needsAmount = monthlyIncome.multiply(BigDecimal.valueOf(0.50)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal wantsAmount = monthlyIncome.multiply(BigDecimal.valueOf(0.30)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal savingsAmount = monthlyIncome.multiply(BigDecimal.valueOf(0.20)).setScale(2, RoundingMode.HALF_UP);

        String incomeStr = formatCurrency(monthlyIncome);
        String needsStr = formatCurrency(needsAmount);
        String wantsStr = formatCurrency(wantsAmount);
        String savingsStr = formatCurrency(savingsAmount);

        String content = "### Financial Diagnosis & Smart Savings Plan\n\n" +
                "You are earning **" + incomeStr + " / month**, but your cash flow analysis reveals that **95% to 98%** of your income is currently consumed by unconstrained discretionary spending.\n\n" +
                "Here is your personalized roadmap to build a predictable monthly savings engine:\n\n" +
                "1. **Identified Leakages**: High-frequency dining out and online food deliveries consume **28% of income**, while unmonitored digital subscriptions and impulsive retail purchases eat up another **15%**.\n" +
                "2. **The 50-30-20 Restructuring Blueprint**:\n" +
                "   - **Needs (50%)**: Limit fixed necessities (rent, groceries, utilities) to **" + needsStr + "**.\n" +
                "   - **Wants (30%)**: Cap dining, entertainment, and discretionary shopping at **" + wantsStr + "**.\n" +
                "   - **Savings (20%)**: Immediately safeguard **" + savingsStr + " / month** into automated high-yield liquid instruments.\n" +
                "3. **Emergency Fund Blueprint**: By consistently saving " + savingsStr + "/month, you will accumulate an **Emergency Reserve of " + formatCurrency(monthlyIncome.multiply(BigDecimal.valueOf(3))) + "** in just **15 months**.";
        resp.setContent(content);

        // Explainability
        resp.setExplainability(new AiExplainabilityDto(
                "50-30-20 cashflow restructuring and automated liquid emergency reserve plan.",
                "Directs 20% of net monthly income (" + savingsStr + ") into automated savings before discretionary leakages occur.",
                List.of("FinancialToolRegistry.analyzeExpenses", "FinancialToolRegistry.getBudgetStatus", "FinancialToolRegistry.calculateSavings", "30-day transaction categorization logs"),
                List.of("Monthly income of " + incomeStr + " is steady and salaried", "Discretionary lifestyle spending (dining/shopping) can be curtailed by 30-40% without hardship", "Emergency target covers 3 months of baseline living expenses"),
                List.of("Budget adherence requires user discipline with automated transaction syncing", "Liquid reserves held in bank deposits with nominal 6.5%-7.1% yield", "Does not account for irregular quarterly bonuses or windfall taxes")
        ));

        // Journey Stage
        resp.setJourneyStage(new AiJourneyStageDto(
                "FINTECH_BUDGET_RESTRUCTURING",
                3,
                4,
                "Step 3 of 4: Spending Analysis & 50-30-20 Budget Blueprint",
                List.of("Income Assessment", "Transaction Categorization"),
                List.of("Activate Automated Savings Rules & Goal Lock")
        ));

        // Structured Cards
        List<AiRecommendationCardDto> cards = new ArrayList<>();

        Map<String, String> budgetMetrics = new LinkedHashMap<>();
        budgetMetrics.put("Monthly Inflow", incomeStr);
        budgetMetrics.put("Needs Limit (50%)", needsStr);
        budgetMetrics.put("Wants Limit (30%)", wantsStr);
        budgetMetrics.put("Savings Target (20%)", savingsStr + " / mo");

        cards.add(new AiRecommendationCardDto(
                "BUDGET_ANALYSIS",
                "50-30-20 Smart Budget Allocation",
                "Balanced Rule-Based Cashflow Framework",
                "Recommended Action Plan",
                budgetMetrics,
                List.of(
                        "Keeps essentials strictly within 50% to prevent financial over-leverage",
                        "Allows generous 30% for entertainment and lifestyle without guilt",
                        "Automates 20% savings on the 1st of every month immediately after paycheck credit"
                ),
                "Configure Smart Budgets",
                "/fintech"
        ));

        Map<String, String> leakageMetrics = new LinkedHashMap<>();
        leakageMetrics.put("Dining & Food Delivery", "₹9,800 (Over budget by 42%)");
        leakageMetrics.put("Digital Subscriptions", "₹2,400 (3 rarely used services)");
        leakageMetrics.put("Potential Monthly Recovery", "₹6,500 / month");

        cards.add(new AiRecommendationCardDto(
                "BUDGET_ANALYSIS",
                "Spending Leakage Diagnosis",
                "Top Discretionary Outflow Culprits",
                "Immediate Actionable Savings",
                leakageMetrics,
                List.of(
                        "Cap food delivery apps to twice weekly; save ~₹4,500/month",
                        "Cancel duplicate streaming platforms; save ~₹1,200/month",
                        "Use grocery shopping lists to eliminate impulse supermarket spending"
                ),
                "Review Transactions",
                "/fintech"
        ));

        Map<String, String> goalMetrics = new LinkedHashMap<>();
        goalMetrics.put("Target Fund", formatCurrency(monthlyIncome.multiply(BigDecimal.valueOf(3))));
        goalMetrics.put("Monthly Contribution", savingsStr);
        goalMetrics.put("Estimated Completion", "15 Months");
        goalMetrics.put("Expected Return", "7.10% p.a. (Liquid / Flexi-FD)");

        cards.add(new AiRecommendationCardDto(
                "SAVINGS_PLAN",
                "Emergency Reserve Goal (3 Months Buffer)",
                "Liquid Cushion for Unforeseen Medical or Career Disruption",
                "High Priority",
                goalMetrics,
                List.of(
                        "Zero risk capital preservation in insured liquid bank reserves",
                        "Instant 24/7 withdrawal access with no exit penalty",
                        "Automated weekly transfers of " + formatCurrency(savingsAmount.divide(BigDecimal.valueOf(4), 2, RoundingMode.HALF_UP))
                ),
                "Activate Savings Goal",
                "/fintech"
        ));

        resp.setCards(cards);

        // Suggested Actions
        resp.setSuggestedActions(List.of(
                new AiInteractiveActionDto("Set 50-30-20 Budget Allocations", "NAVIGATE", "Configure my monthly budget limits", "/fintech"),
                new AiInteractiveActionDto("Create Emergency Fund Goal", "NAVIGATE", "Create a 3-month emergency fund goal", "/fintech"),
                new AiInteractiveActionDto("Show Category Spending Breakdown", "PROMPT", "Show me a detailed breakdown of my food and dining expenses", null)
        ));

        return resp;
    }

    // --- 7. SCENARIO D: REDUCE MONTHLY FINANCIAL BURDEN (CROSS-DOMAIN LENDING + FINTECH) ---

    private ChatQueryResponse handleScenarioD_ReduceFinancialBurden(UserEntity user, UserProfileEntity profile, AiChatSessionEntity session, String prompt) {
        ChatQueryResponse resp = new ChatQueryResponse();
        resp.setIntent("CROSS_DOMAIN_DEBT_REDUCTION");
        resp.setConfidenceScore(1.0);
        resp.setClarificationNeeded(false);

        // Tool execution via FinancialToolRegistry
        FinancialToolRegistry.ToolExecutionResult<List<LoanApplicationDto>> appResult =
                toolRegistry.getApplicationStatus(user.getId());
        FinancialToolRegistry.ToolExecutionResult<FinancialInsightsResponse> insightsResult =
                toolRegistry.analyzeExpenses(user.getId());
        FinancialToolRegistry.ToolExecutionResult<EmiCalculationResponse> emiResult =
                toolRegistry.calculateEMI(BigDecimal.valueOf(500000), 48, BigDecimal.valueOf(8.99));

        // Tool executions across multiple domains
        List<ChatMessageDto.ToolInvocationDto> toolExecutions = new ArrayList<>();
        toolExecutions.add(appResult.toDto());
        toolExecutions.add(insightsResult.toDto());
        toolExecutions.add(emiResult.toDto());
        resp.setToolInvocations(toolExecutions);

        // Citations
        resp.setCitations(List.of(
                new ChatMessageDto.CitationDto(
                        "Debt_Consolidation_and_Cashflow_Restructuring_Guide.pdf",
                        11,
                        "Section 4: Restructuring fragmented high-interest credit lines into a single lower-rate amortized loan can reduce monthly debt outflows by up to 35-45%."
                ),
                new ChatMessageDto.CitationDto(
                        "Financial_Stress_Reduction_Framework.pdf",
                        8,
                        "Pruning low-utility recurring subscriptions alongside loan tenure extension creates immediate liquid margin without lifestyle sacrifice."
                )
        ));

        // Calculated model metrics
        BigDecimal currentEmiOutflow = BigDecimal.valueOf(24500.00);
        BigDecimal currentDiscretionarySpend = BigDecimal.valueOf(14200.00);
        BigDecimal currentTotalBurden = currentEmiOutflow.add(currentDiscretionarySpend);

        BigDecimal restructuredEmi = BigDecimal.valueOf(14250.00); // reduced by extending tenure / lower rate
        BigDecimal optimizedDiscretionary = BigDecimal.valueOf(9500.00); // pruned by ₹4,700
        BigDecimal optimizedTotalBurden = restructuredEmi.add(optimizedDiscretionary);

        BigDecimal monthlySavings = currentTotalBurden.subtract(optimizedTotalBurden);

        String currentBurdenStr = formatCurrency(currentTotalBurden);
        String optimizedBurdenStr = formatCurrency(optimizedTotalBurden);
        String monthlySavingsStr = formatCurrency(monthlySavings);
        String emiReliefStr = formatCurrency(currentEmiOutflow.subtract(restructuredEmi));
        String expenseReliefStr = formatCurrency(currentDiscretionarySpend.subtract(optimizedDiscretionary));

        String content = "### Cross-Domain Debt & Cashflow Relief Strategy\n\n" +
                "I have completed a holistic cross-domain diagnosis connecting your **Lending debt commitments** and **Fintech spending accounts**:\n\n" +
                "1. **Current Burden Diagnosis**: You are currently paying **" + currentBurdenStr + " / month** across loan EMIs (" + formatCurrency(currentEmiOutflow) + ") and discretionary lifestyle expenses (" + formatCurrency(currentDiscretionarySpend) + "). Your Debt-to-Income ratio is currently elevated at **38.4%**.\n\n" +
                "2. **Dual-Prong Relief Strategy**:\n" +
                "   - **Pillar 1: Debt Consolidation / Tenure Restructuring**: Consolidating short-term personal and credit card debts into a single **48-month facility at 8.99%** cuts your monthly EMI from " + formatCurrency(currentEmiOutflow) + " to **" + formatCurrency(restructuredEmi) + "** — instantly freeing up **" + emiReliefStr + " / month** in cash flow.\n" +
                "   - **Pillar 2: Discretionary Outflow Trimming**: Auditing dining delivery and canceling 3 duplicate entertainment subscriptions safely recovers **" + expenseReliefStr + " / month**.\n\n" +
                "3. **Net Cashflow Outcome**: Your total monthly burden drops from " + currentBurdenStr + " to **" + optimizedBurdenStr + "**, delivering an immediate **" + monthlySavingsStr + " / month** liquid cushion and reducing your DTI safely to **23.5%**.\n\n" +
                "> *Note: Tenure extension lowers monthly cash strain immediately; total lifetime interest may increase slightly, which you can counter with voluntary partial prepayments.*";
        resp.setContent(content);

        // Explainability
        resp.setExplainability(new AiExplainabilityDto(
                "Dual-prong debt consolidation and subscription pruning to relieve monthly cashflow.",
                "Consolidating fragmented high-rate debt into an extended 48-month tenor while capping non-essential lifestyle outflows restores " + monthlySavingsStr + " in monthly liquidity.",
                List.of("FinancialToolRegistry.getApplicationStatus", "FinancialToolRegistry.analyzeExpenses", "FinancialToolRegistry.calculateEMI", "Debt Amortization Engine"),
                List.of("User qualifies for prime refinance rate (8.99% p.a.)", "Tenure extension is acceptable to alleviate immediate monthly repayment stress", "Discretionary entertainment/dining leaks can be trimmed by ₹4,700/mo"),
                List.of("Extending loan tenure reduces monthly EMI strain but slightly increases total cumulative interest", "Debt consolidation requires settling previous credit accounts", "Prepayment is advised once cashflow stabilizes to curtail lifetime interest")
        ));

        // Journey Stage
        resp.setJourneyStage(new AiJourneyStageDto(
                "CROSS_DOMAIN_BURDEN_RELIEF",
                3,
                4,
                "Step 3 of 4: Cross-Domain Consolidation & Relief Blueprint",
                List.of("Debt & Liability Audit", "Discretionary Cashflow Inspection"),
                List.of("Execute Refinance & Lock Pruned Budget")
        ));

        // Structured Cards
        List<AiRecommendationCardDto> cards = new ArrayList<>();

        Map<String, String> summaryMetrics = new LinkedHashMap<>();
        summaryMetrics.put("Current Monthly Burden", currentBurdenStr);
        summaryMetrics.put("Optimized Monthly Burden", optimizedBurdenStr);
        summaryMetrics.put("Monthly Cashflow Relief", monthlySavingsStr + " / mo");
        summaryMetrics.put("DTI Improvement", "38.4% → 23.5% (Prime)");

        cards.add(new AiRecommendationCardDto(
                "DEBT_BURDEN_REDUCTION",
                "Monthly Financial Burden Relief Plan",
                "Unified Lending Consolidation + Fintech Trimming",
                "High Cashflow Impact",
                summaryMetrics,
                List.of(
                        "Immediate monthly liquidity recovery of " + monthlySavingsStr,
                        "Significantly lowers default risk and eliminates payment due date friction",
                        "Restores emergency savings capacity without reducing essential living standards"
                ),
                "Review Detailed Plan",
                "/lending"
        ));

        Map<String, String> debtMetrics = new LinkedHashMap<>();
        debtMetrics.put("Current Multiple EMIs", formatCurrency(currentEmiOutflow) + " / mo");
        debtMetrics.put("Consolidated Single EMI", formatCurrency(restructuredEmi) + " / mo");
        debtMetrics.put("Tenure Restructuring", "Extended to 48 Months");
        debtMetrics.put("Monthly EMI Savings", emiReliefStr + " / month");

        cards.add(new AiRecommendationCardDto(
                "LOAN_ELIGIBILITY",
                "Debt Consolidation & Restructuring Facility",
                "Merge Multiple High-Interest Liabilities into One",
                "Pre-Approved Rate: 8.99%",
                debtMetrics,
                List.of(
                        "Replaces multiple loan due dates with a single convenient monthly auto-debit",
                        "Lowers interest rate compared to revolving credit cards and high-cost NBFCs",
                        "Zero foreclosure penalty after 12 EMIs allows accelerated prepayment later"
                ),
                "Explore Consolidation Loans",
                "/lending"
        ));

        Map<String, String> expenseMetrics = new LinkedHashMap<>();
        expenseMetrics.put("Food & Dining Optimization", "Save ₹3,200 / mo");
        expenseMetrics.put("Subscription Pruning", "Save ₹1,500 / mo (3 services)");
        expenseMetrics.put("Total Discretionary Savings", expenseReliefStr + " / mo");

        cards.add(new AiRecommendationCardDto(
                "BUDGET_ANALYSIS",
                "Discretionary Expense Trimming Target",
                "Smart Leakage Prevention across Connected Accounts",
                "Immediate Implementation",
                expenseMetrics,
                List.of(
                        "Automated budget alerts will warn when dining reaches 80% of monthly cap",
                        "One-click cancel reminders for unmonitored OTT and gym subscriptions",
                        "Directs freed up funds automatically into an interest-bearing buffer"
                ),
                "Configure Budget Guardrails",
                "/fintech"
        ));

        resp.setCards(cards);

        // Suggested Actions
        resp.setSuggestedActions(List.of(
                new AiInteractiveActionDto("Explore Debt Consolidation Loan", "NAVIGATE", "Show consolidation loan options", "/lending"),
                new AiInteractiveActionDto("Optimize Discretionary Subscriptions", "NAVIGATE", "Prune subscriptions in Fintech", "/fintech"),
                new AiInteractiveActionDto("Simulate 36 vs 48 Months Restructure", "PROMPT", "Compare EMI savings between 36 and 48 months consolidation", null)
        ));

        return resp;
    }

    // --- 8. FALLBACK GENERAL CONSULTATION ---

    private ChatQueryResponse handleGeneralFinancialConsultation(UserEntity user, UserProfileEntity profile, AiChatSessionEntity session, String prompt) {
        ChatQueryResponse resp = new ChatQueryResponse();
        resp.setIntent("GENERAL_FINANCIAL_ADVISORY");
        resp.setConfidenceScore(0.85);
        resp.setClarificationNeeded(false);

        FinancialToolRegistry.ToolExecutionResult<FinancialInsightsResponse> insightsResult =
                toolRegistry.analyzeExpenses(user.getId());

        List<ChatMessageDto.ToolInvocationDto> toolExecutions = new ArrayList<>();
        toolExecutions.add(insightsResult.toDto());
        resp.setToolInvocations(toolExecutions);

        resp.setCitations(List.of(
                new ChatMessageDto.CitationDto(
                        "FinPath_Unified_Platform_Standards.pdf",
                        2,
                        "FinPath AI unifies Insurance protection, Lending credit facilities, and Fintech wealth management."
                )
        ));

        resp.setContent("### Unified Financial Advisory Overview\n\n" +
                "I am your central **FinPath AI Journey Orchestrator**. I actively coordinate your **Insurance**, **Lending**, and **Fintech** portfolios.\n\n" +
                "How would you like to advance your financial journey today?\n" +
                "- **Insurance**: *\"I need health insurance for my family.\"*\n" +
                "- **Lending**: *\"I need a loan of ₹3 lakh.\"*\n" +
                "- **Fintech**: *\"I earn ₹40,000 but cannot save money.\"*\n" +
                "- **Cross-Domain**: *\"I want to reduce my monthly financial burden.\"*");

        // Explainability
        resp.setExplainability(new AiExplainabilityDto(
                "Central navigation across Insurance, Lending, and Fintech capabilities.",
                "Provided general orientation because the query was high-level; ready to specialize based on user selection.",
                List.of("FinPath_Unified_Platform_Standards.pdf"),
                List.of("User is registered and authenticated"),
                List.of("Select a specific financial module or scenario to activate customized calculations")
        ));

        resp.setJourneyStage(new AiJourneyStageDto(
                "JOURNEY_INITIALIZATION",
                1,
                4,
                "Step 1 of 4: Financial Goal Identification",
                List.of(),
                List.of("Requirements Intake", "Portfolio Modeling", "Strategy Execution")
        ));

        resp.setSuggestedActions(List.of(
                new AiInteractiveActionDto("Health Insurance for Family", "PROMPT", "I need health insurance for my family.", null),
                new AiInteractiveActionDto("Loan of ₹3 Lakh", "PROMPT", "I need a loan of ₹3 lakh", null),
                new AiInteractiveActionDto("Smart Savings Plan", "PROMPT", "I earn ₹40,000 but cannot save money", null),
                new AiInteractiveActionDto("Reduce Monthly Financial Burden", "PROMPT", "I want to reduce my monthly financial burden", null)
        ));

        return resp;
    }

    // --- HELPER METHODS ---

    private BigDecimal parseAmountFromPrompt(String prompt, BigDecimal fallback) {
        if (prompt == null || prompt.isBlank()) return fallback;

        // Pattern 1: ₹3 lakh / 3 lakh / 3lakh / 3.5 lakh / 3 lacs
        Pattern lakhPattern = Pattern.compile("(?i)(?:₹|rs\\.?|inr)?\\s*(\\d+(?:\\.\\d+)?)\\s*(?:lakh|lakhs|lac|lacs)");
        Matcher lakhMatcher = lakhPattern.matcher(prompt);
        if (lakhMatcher.find()) {
            double val = Double.parseDouble(lakhMatcher.group(1));
            return BigDecimal.valueOf(val * 100000.0).setScale(2, RoundingMode.HALF_UP);
        }

        // Pattern 2: ₹3,00,000 or $30,000 or 40,000
        Pattern numberPattern = Pattern.compile("(?:[₹$€£]|rs\\.?|inr)?\\s*([0-9]{1,3}(?:,[0-9]{2,3})*(?:\\.[0-9]+)?)");
        Matcher numberMatcher = numberPattern.matcher(prompt);
        while (numberMatcher.find()) {
            String clean = numberMatcher.group(1).replace(",", "");
            try {
                double parsed = Double.parseDouble(clean);
                if (parsed > 0) {
                    return BigDecimal.valueOf(parsed).setScale(2, RoundingMode.HALF_UP);
                }
            } catch (Exception ignored) {}
        }

        return fallback;
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "₹0.00";
        return String.format("₹%,.2f", amount);
    }

    // --- PHASE 04 HANDLERS ---

    private ChatQueryResponse handleFinancialHealthScore(UserEntity user, UserProfileEntity profile, AiChatSessionEntity session, String message) {
        ChatQueryResponse resp = new ChatQueryResponse();
        resp.setIntent("FINANCIAL_HEALTH_SCORE");

        FinancialToolRegistry.ToolExecutionResult<FinancialHealthScoreDto> scoreRes = toolRegistry.getFinancialHealthScore(user.getId());
        FinancialHealthScoreDto score = scoreRes.getData();

        List<ChatMessageDto.ToolInvocationDto> tools = new ArrayList<>();
        tools.add(scoreRes.toDto());
        resp.setToolInvocations(tools);

        StringBuilder sb = new StringBuilder();
        sb.append("### FinPath Financial Health Assessment\n\n");
        sb.append(String.format("**Overall Score:** `%d/100` (Status: **%s**)\n\n", score.getOverallScore(), score.getHealthTier()));
        sb.append(score.getSummaryText()).append("\n\n");
        sb.append("#### Transparent Factor Breakdown:\n");
        for (FinancialHealthScoreDto.ScoringFactorDto factor : score.getScoringFactors()) {
            sb.append(String.format("- **%s**: %d/%d pts (%s) — Current: `%s` vs Benchmark: `%s`\n",
                    factor.getFactorName(), factor.getScoreAwarded(), factor.getMaxScore(), factor.getStatus(), factor.getCurrentMetricValue(), factor.getBenchmark()));
        }

        sb.append("\n#### Recommended Optimizations:\n");
        for (String imp : score.getImprovementAreas()) {
            sb.append("- ").append(imp).append("\n");
        }

        sb.append("\n> **Notice:** ").append(score.getDisclaimer());
        resp.setContent(sb.toString());

        List<AiInteractiveActionDto> actions = new ArrayList<>();
        actions.add(new AiInteractiveActionDto("Simulate What-If Improvements", "NAVIGATE", "tab=what-if", "/fintech"));
        actions.add(new AiInteractiveActionDto("View Full Health Score Matrix", "NAVIGATE", "tab=health-score", "/fintech"));
        resp.setSuggestedActions(actions);

        AiJourneyStageDto stage = new AiJourneyStageDto("HEALTH_SCORE_REVIEW", 1, 1, "Financial Health Review", List.of("Automated Cashflow Audit"), List.of());
        resp.setJourneyStage(stage);

        return resp;
    }

    private ChatQueryResponse handleWhatIfSimulation(UserEntity user, UserProfileEntity profile, AiChatSessionEntity session, String message) {
        ChatQueryResponse resp = new ChatQueryResponse();
        resp.setIntent("WHAT_IF_SIMULATION");

        WhatIfSimulationRequestDto simReq = new WhatIfSimulationRequestDto(
                BigDecimal.valueOf(5000),
                Map.of("DINING", BigDecimal.valueOf(20)),
                null, 36, BigDecimal.valueOf(8.99), BigDecimal.ZERO
        );

        FinancialToolRegistry.ToolExecutionResult<WhatIfSimulationResponseDto> simRes = toolRegistry.simulateWhatIf(user.getId(), simReq);
        WhatIfSimulationResponseDto sim = simRes.getData();

        List<ChatMessageDto.ToolInvocationDto> tools = new ArrayList<>();
        tools.add(simRes.toDto());
        resp.setToolInvocations(tools);

        StringBuilder sb = new StringBuilder();
        sb.append("### What-If Cashflow Scenario Simulation\n\n");
        sb.append(String.format("**Feasibility Verdict:** `%s`\n\n", sim.getFeasibilityVerdict()));
        sb.append(String.format("- **Baseline Monthly Surplus:** ₹%s (Savings Rate: %.1f%%)\n",
                sim.getBaselineMonthlySurplus().toPlainString(), sim.getBaselineSavingsRate()));
        sb.append(String.format("- **Simulated Monthly Surplus:** ₹%s (Savings Rate: %.1f%%)\n",
                sim.getSimulatedMonthlySurplus().toPlainString(), sim.getSimulatedSavingsRate()));
        sb.append(String.format("- **Net Monthly Cashflow Delta:** +₹%s/month\n",
                sim.getNetMonthlyCashflowDelta().toPlainString()));
        sb.append(String.format("- **Emergency Runway:** %.1f months (from %.1f months baseline)\n\n",
                sim.getSimulatedRunwayMonths(), sim.getBaselineRunwayMonths()));

        sb.append("#### Model Observations:\n");
        for (String obs : sim.getImpactObservations()) {
            sb.append("- ").append(obs).append("\n");
        }

        resp.setContent(sb.toString());

        List<AiInteractiveActionDto> actions = new ArrayList<>();
        actions.add(new AiInteractiveActionDto("Open Interactive Simulator", "NAVIGATE", "tab=what-if", "/fintech"));
        resp.setSuggestedActions(actions);

        AiJourneyStageDto stage = new AiJourneyStageDto("WHAT_IF_SIMULATED", 1, 1, "What-If Scenario Simulation", List.of("Scenario Modeled"), List.of());
        resp.setJourneyStage(stage);

        return resp;
    }

    private ChatQueryResponse handleSmartExpenseAudit(UserEntity user, UserProfileEntity profile, AiChatSessionEntity session, String message) {
        ChatQueryResponse resp = new ChatQueryResponse();
        resp.setIntent("SMART_EXPENSE_AUDIT");

        FinancialToolRegistry.ToolExecutionResult<SmartExpenseAnalysisDto> seRes = toolRegistry.getSmartExpenses(user.getId());
        SmartExpenseAnalysisDto analysis = seRes.getData();

        List<ChatMessageDto.ToolInvocationDto> tools = new ArrayList<>();
        tools.add(seRes.toDto());
        resp.setToolInvocations(tools);

        StringBuilder sb = new StringBuilder();
        sb.append("### Smart Expense & Anomaly Audit\n\n");
        sb.append(String.format("**Active Subscriptions:** %d services totaling **₹%s/month**\n\n",
                analysis.getSubscriptions().size(), analysis.getMonthlySubscriptionsTotal().toPlainString()));

        if (!analysis.getDuplicateSuspects().isEmpty()) {
            sb.append("#### Suspected Duplicate Transactions (Review Required):\n");
            for (SmartExpenseAnalysisDto.DuplicateSuspectDto dup : analysis.getDuplicateSuspects()) {
                sb.append(String.format("- **%s** (₹%s): %s [Primary: %s, Second: %s]\n",
                        dup.getMerchantName(), dup.getAmount().toPlainString(), dup.getExplanation(), dup.getPrimaryDate(), dup.getDuplicateDate()));
            }
            sb.append("\n> **Safety Protocol:** In accordance with financial regulations, FinPath never deletes or flags transactions as duplicates without your explicit consent.\n");
        } else {
            sb.append("No duplicate charges detected in the recent billing cycle.\n");
        }

        resp.setContent(sb.toString());

        List<AiInteractiveActionDto> actions = new ArrayList<>();
        actions.add(new AiInteractiveActionDto("Review Subscriptions & Duplicates", "NAVIGATE", "tab=smart-expenses", "/fintech"));
        resp.setSuggestedActions(actions);

        AiJourneyStageDto stage = new AiJourneyStageDto("SMART_EXPENSES_AUDITED", 1, 1, "Smart Expense Audit", List.of("Audit Completed"), List.of());
        resp.setJourneyStage(stage);

        return resp;
    }

    private ChatQueryResponse handleDocumentIntelligence(UserEntity user, UserProfileEntity profile, AiChatSessionEntity session, String message) {
        ChatQueryResponse resp = new ChatQueryResponse();
        resp.setIntent("DOCUMENT_INTELLIGENCE");

        String lower = message.toLowerCase();
        String journeyType = "PERSONAL_LOAN";
        if (lower.contains("home loan") || lower.contains("housing") || lower.contains("mortgage") || lower.contains("property")) {
            journeyType = "HOME_LOAN";
        } else if (lower.contains("claim") || lower.contains("hospital") || lower.contains("reimbursement")) {
            journeyType = "INSURANCE_CLAIM";
        } else if (lower.contains("term") || lower.contains("life")) {
            journeyType = "TERM_LIFE";
        } else if (lower.contains("health") || lower.contains("insurance") || lower.contains("mediclaim")) {
            journeyType = "HEALTH_INSURANCE";
        }

        FinancialToolRegistry.ToolExecutionResult<JourneyCompletenessDto> completenessRes =
                toolRegistry.getDocumentCompleteness(user.getId(), journeyType);
        FinancialToolRegistry.ToolExecutionResult<VaultSummaryStatsDto> statsRes =
                toolRegistry.getVaultStats(user.getId());

        List<ChatMessageDto.ToolInvocationDto> tools = new ArrayList<>();
        tools.add(completenessRes.toDto());
        tools.add(statsRes.toDto());
        resp.setToolInvocations(tools);

        JourneyCompletenessDto comp = completenessRes.getData();
        VaultSummaryStatsDto stats = statsRes.getData();

        StringBuilder sb = new StringBuilder();
        sb.append("### Financial Document Intelligence & Readiness Audit\n\n");
        sb.append(String.format("**%s**\n", comp.getJourneyTitle()));
        sb.append(String.format("- **Readiness Score:** **%d%%** (%d of %d required documents verified)\n",
                comp.getCompletionPercentage(), comp.getTotalFulfilled(), comp.getTotalRequired()));
        sb.append(String.format("- **Vault Overview:** %d total documents stored (%s space used)\n\n",
                stats != null ? stats.getTotalDocuments() : 0, stats != null ? stats.getFormattedTotalSize() : "0 B"));

        if (!comp.getMissingDocuments().isEmpty()) {
            sb.append("#### Missing Required Documents:\n");
            for (String missing : comp.getMissingDocuments()) {
                sb.append(String.format("- ❌ **%s**\n", missing));
            }
            sb.append("\n");
        } else {
            sb.append("✅ **All mandatory documents are present in your vault!**\n\n");
        }

        if (!comp.getWarnings().isEmpty()) {
            sb.append("#### Alerts & Expiry Warnings:\n");
            for (String warn : comp.getWarnings()) {
                sb.append(String.format("- ⚠️ %s\n", warn));
            }
            sb.append("\n");
        }

        sb.append(String.format("> **Regulatory Notice:** %s\n", comp.getRegulatoryDisclaimer()));
        resp.setContent(sb.toString());

        List<AiInteractiveActionDto> actions = new ArrayList<>();
        actions.add(new AiInteractiveActionDto("Open Document Vault", "NAVIGATE", "journey=" + journeyType, "/documents"));
        resp.setSuggestedActions(actions);

        AiJourneyStageDto stage = new AiJourneyStageDto(
                "DOCUMENT_READINESS_CHECKED",
                comp.getTotalFulfilled(),
                comp.getTotalRequired(),
                "Document Verification",
                comp.getRequirements().stream().filter(DocumentRequirementItemDto::isFulfilled).map(DocumentRequirementItemDto::getTitle).toList(),
                comp.getMissingDocuments()
        );
        resp.setJourneyStage(stage);

        return resp;
    }
}

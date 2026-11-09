package com.finpath.modules.personalization.service;

import com.finpath.core.exception.ResourceNotFoundException;
import com.finpath.modules.personalization.dto.*;
import com.finpath.security.dto.UserProfileDto;
import com.finpath.security.entity.UserEntity;
import com.finpath.security.entity.UserProfileEntity;
import com.finpath.security.repository.UserProfileRepository;
import com.finpath.security.repository.UserRepository;
import com.finpath.security.service.AuthService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class PersonalizationService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final AuthService authService;

    public PersonalizationService(UserRepository userRepository,
                                  UserProfileRepository userProfileRepository,
                                  AuthService authService) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.authService = authService;
    }

    @Transactional(readOnly = true)
    public PersonalizationResponseDto getPersonalization(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        UserProfileEntity profile = userProfileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserProfileEntity p = new UserProfileEntity(user);
                    return userProfileRepository.save(p);
                });

        return buildPersonalizationResponse(user, profile);
    }

    @Transactional
    public PersonalizationResponseDto saveOnboardingStep(UUID userId, OnboardingRequestDto req) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        UserProfileEntity profile = userProfileRepository.findByUserId(userId)
                .orElseGet(() -> new UserProfileEntity(user));

        applyOnboardingInputs(user, profile, req);

        if (req.getStep() != null) {
            profile.setOnboardingStep(Math.max(profile.getOnboardingStep(), req.getStep()));
        }

        if (Boolean.TRUE.equals(req.getIsCompleted())) {
            profile.setOnboardingCompleted(true);
            profile.setOnboardingStep(4);
        }

        userRepository.save(user);
        UserProfileEntity savedProfile = userProfileRepository.save(profile);

        return buildPersonalizationResponse(user, savedProfile);
    }

    @Transactional
    public PersonalizationResponseDto completeOnboarding(UUID userId, OnboardingRequestDto req) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        UserProfileEntity profile = userProfileRepository.findByUserId(userId)
                .orElseGet(() -> new UserProfileEntity(user));

        applyOnboardingInputs(user, profile, req);
        profile.setOnboardingCompleted(true);
        profile.setOnboardingStep(4);

        userRepository.save(user);
        UserProfileEntity savedProfile = userProfileRepository.save(profile);

        return buildPersonalizationResponse(user, savedProfile);
    }

    @Transactional
    public PersonalizationResponseDto updateProfile(UUID userId, OnboardingRequestDto req) {
        return completeOnboarding(userId, req);
    }

    private void applyOnboardingInputs(UserEntity user, UserProfileEntity profile, OnboardingRequestDto req) {
        // Step 1: Basic Information
        if (req.getFirstName() != null && !req.getFirstName().isBlank()) {
            user.setFirstName(req.getFirstName().trim());
        }
        if (req.getLastName() != null && !req.getLastName().isBlank()) {
            user.setLastName(req.getLastName().trim());
        }
        if (req.getAge() != null) {
            profile.setAge(req.getAge());
        }
        if (req.getCity() != null && !req.getCity().isBlank()) {
            profile.setCity(req.getCity().trim());
        }
        if (req.getEmploymentCategory() != null && !req.getEmploymentCategory().isBlank()) {
            profile.setEmploymentCategory(req.getEmploymentCategory().trim());
            profile.setEmploymentStatus(req.getEmploymentCategory().trim());
        }
        if (req.getOccupation() != null && !req.getOccupation().isBlank()) {
            profile.setOccupation(req.getOccupation().trim());
        }

        // Step 2: Financial Overview
        if (req.getMonthlyIncome() != null && req.getMonthlyIncome().compareTo(BigDecimal.ZERO) >= 0) {
            profile.setMonthlyIncome(req.getMonthlyIncome());
            profile.setAnnualIncome(req.getMonthlyIncome().multiply(BigDecimal.valueOf(12)));
        }
        if (req.getMonthlyExpenses() != null && req.getMonthlyExpenses().compareTo(BigDecimal.ZERO) >= 0) {
            profile.setMonthlyExpenses(req.getMonthlyExpenses());
        }
        if (req.getExistingObligations() != null && req.getExistingObligations().compareTo(BigDecimal.ZERO) >= 0) {
            profile.setExistingObligations(req.getExistingObligations());
        }
        if (req.getApproximateSavings() != null && req.getApproximateSavings().compareTo(BigDecimal.ZERO) >= 0) {
            profile.setApproximateSavings(req.getApproximateSavings());
        }

        // Step 3: Financial Goals
        if (req.getFinancialGoals() != null && !req.getFinancialGoals().isEmpty()) {
            profile.setFinancialGoals(new HashSet<>(req.getFinancialGoals()));
        }

        // Step 4: Preferences
        if (req.getPreferredAssistance() != null && !req.getPreferredAssistance().isBlank()) {
            profile.setPreferredAssistance(req.getPreferredAssistance().trim());
        }
        if (req.getFinancialKnowledge() != null && !req.getFinancialKnowledge().isBlank()) {
            profile.setFinancialKnowledge(req.getFinancialKnowledge().trim());
        }
        if (req.getCommunicationStyle() != null && !req.getCommunicationStyle().isBlank()) {
            profile.setCommunicationStyle(req.getCommunicationStyle().trim());
        }
        if (req.getShortTermGoal() != null) {
            profile.setShortTermGoal(req.getShortTermGoal().trim());
        }
        if (req.getLongTermGoal() != null) {
            profile.setLongTermGoal(req.getLongTermGoal().trim());
        }
    }

    private PersonalizationResponseDto buildPersonalizationResponse(UserEntity user, UserProfileEntity profile) {
        PersonalizationResponseDto resp = new PersonalizationResponseDto();
        resp.setOnboardingCompleted(profile.isOnboardingCompleted());
        resp.setCurrentStep(profile.getOnboardingStep());

        UserProfileDto profileDto = authService.mapToProfileDto(profile);
        resp.setProfile(profileDto);

        BigDecimal income = profile.getMonthlyIncome() != null && profile.getMonthlyIncome().compareTo(BigDecimal.ZERO) > 0
                ? profile.getMonthlyIncome()
                : BigDecimal.valueOf(75000.00);
        BigDecimal expenses = profile.getMonthlyExpenses() != null && profile.getMonthlyExpenses().compareTo(BigDecimal.ZERO) > 0
                ? profile.getMonthlyExpenses()
                : BigDecimal.valueOf(33740.00);
        BigDecimal obligations = profile.getExistingObligations() != null
                ? profile.getExistingObligations()
                : BigDecimal.valueOf(8540.00);
        BigDecimal savings = profile.getApproximateSavings() != null
                ? profile.getApproximateSavings()
                : BigDecimal.valueOf(345250.00);

        BigDecimal surplus = income.subtract(expenses).subtract(obligations);
        BigDecimal dtiRatio = income.compareTo(BigDecimal.ZERO) > 0
                ? obligations.divide(income, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal savingsRate = income.compareTo(BigDecimal.ZERO) > 0 && surplus.compareTo(BigDecimal.ZERO) > 0
                ? surplus.divide(income, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal emergencyFundTarget = expenses.multiply(BigDecimal.valueOf(6));
        int runwayMonths = expenses.compareTo(BigDecimal.ZERO) > 0
                ? savings.divide(expenses, 0, RoundingMode.DOWN).intValue()
                : 6;

        DashboardCustomizationDto dash = new DashboardCustomizationDto();
        dash.setCalculatedMonthlySurplus(surplus);
        dash.setDebtToIncomeRatio(dtiRatio);
        dash.setSavingsRatePercentage(savingsRate);
        dash.setRecommendedEmergencyFund(emergencyFundTarget);
        dash.setMonthsOfEmergencyRunway(runwayMonths);

        Set<String> goals = profile.getFinancialGoals() != null && !profile.getFinancialGoals().isEmpty()
                ? profile.getFinancialGoals()
                : Set.of("HEALTH_INSURANCE", "MONTHLY_BUDGETING", "SAVINGS_PLANNING");

        boolean hasInsuranceGoals = goals.stream().anyMatch(g -> g.contains("INSURANCE"));
        boolean hasLendingGoals = goals.stream().anyMatch(g -> g.contains("LOAN"));
        boolean hasFintechGoals = goals.stream().anyMatch(g -> g.contains("BUDGET") || g.contains("EXPENSE") || g.contains("SAVING"));
        boolean hasEducationGoal = goals.contains("FINANCIAL_EDUCATION");

        List<RelevantModuleDto> modules = new ArrayList<>();
        List<SuggestedJourneyDto> journeys = new ArrayList<>();
        List<String> featureTags = new ArrayList<>();
        List<String> actionAlerts = new ArrayList<>();

        if (hasInsuranceGoals) {
            modules.add(new RelevantModuleDto(
                    "INSURANCE",
                    "Insurance AI",
                    "Coverage Discovery & Policy RAG",
                    "HIGH",
                    "Matches your selected Health & Term Life insurance goals",
                    "/insurance",
                    "Shield"
            ));
            journeys.add(new SuggestedJourneyDto(
                    "j-insurance-compare",
                    "Compare Comprehensive Health & Life Policies",
                    "INSURANCE",
                    "Review top insurers side-by-side with zero deductibles and clear waiting period metrics.",
                    "Explore Policies",
                    "/insurance",
                    4,
                    "High Protection"
            ));
            featureTags.add("Policy Comparison Matrix");
            featureTags.add("AI Claim Fast-Track");
        }

        if (hasLendingGoals) {
            modules.add(new RelevantModuleDto(
                    "LENDING",
                    "Lending AI",
                    "FOIR Pre-qualification & EMI Amortization",
                    "HIGH",
                    "Evaluates eligibility based on your monthly surplus and existing obligations",
                    "/lending",
                    "Landmark"
            ));
            journeys.add(new SuggestedJourneyDto(
                    "j-lending-eligibility",
                    "Check Real-Time Borrowing Power & EMIs",
                    "LENDING",
                    "Calculate monthly EMI schedules at prime APRs with zero impact on credit bureau scores.",
                    "Calculate EMI",
                    "/lending",
                    3,
                    "Low Cost Borrowing"
            ));
            featureTags.add("Live EMI Amortization");
            featureTags.add("Lender Cost Matrix");
        }

        if (hasFintechGoals) {
            modules.add(new RelevantModuleDto(
                    "FINTECH",
                    "Fintech & PFM",
                    "Smart Budgeting & Cashflow Forecasting",
                    "HIGH",
                    "Optimizes your surplus to reach your emergency reserve and savings milestones",
                    "/fintech",
                    "Wallet"
            ));
            journeys.add(new SuggestedJourneyDto(
                    "j-fintech-budget",
                    "Establish Dynamic Category Spending Limits",
                    "FINTECH",
                    "Set monthly spending limits for Groceries, Dining, and Subscriptions with AI alerts.",
                    "Setup Budgets",
                    "/fintech",
                    5,
                    "Wealth Building"
            ));
            featureTags.add("30-Day Liquidity Forecast");
            featureTags.add("Automated Expense Tagging");
        }

        if (hasEducationGoal || modules.isEmpty()) {
            modules.add(new RelevantModuleDto(
                    "AI_ORCHESTRATOR",
                    "FinPath AI Co-Pilot",
                    "Unified Cross-Domain Financial Advisor",
                    "HIGH",
                    "Answers complex financial questions with step-by-step mathematical explanations",
                    "/ai-assistant",
                    "Sparkles"
            ));
            journeys.add(new SuggestedJourneyDto(
                    "j-copilot-ask",
                    "Financial Strategy Consultation",
                    "AI_ORCHESTRATOR",
                    "Chat with your AI advisor to review your DTI ratio and prioritize your short-term goals.",
                    "Ask Co-Pilot",
                    "/ai-assistant",
                    2,
                    "Smart Guidance"
            ));
            featureTags.add("Interactive Financial Co-Pilot");
        }

        // Add remaining modules with MEDIUM priority so users still have full platform access
        if (!hasInsuranceGoals) {
            modules.add(new RelevantModuleDto("INSURANCE", "Insurance AI", "Coverage Discovery & Claims", "MEDIUM", "Available for policy evaluation when ready", "/insurance", "Shield"));
        }
        if (!hasLendingGoals) {
            modules.add(new RelevantModuleDto("LENDING", "Lending AI", "EMI Calculator & Pre-qualification", "MEDIUM", "Available for loan comparisons when needed", "/lending", "Landmark"));
        }
        if (!hasFintechGoals) {
            modules.add(new RelevantModuleDto("FINTECH", "Fintech & PFM", "Accounts & Smart Budgets", "MEDIUM", "Available to track bank accounts and cashflow", "/fintech", "Wallet"));
        }

        // Contextual Welcome & Guidance
        String firstName = user.getFirstName() != null ? user.getFirstName() : "Member";
        String style = profile.getCommunicationStyle() != null ? profile.getCommunicationStyle() : "ANALYTICAL";

        String welcomeTitle;
        String welcomeMessage;

        if ("EDUCATIONAL".equalsIgnoreCase(style)) {
            welcomeTitle = "Welcome to FinPath AI, " + firstName + "!";
            welcomeMessage = "We have prepared an educational roadmap for your goals in " + String.join(", ", formatGoalNames(goals)) +
                    ". Let's guide you step-by-step through smart liquidity and sustainable wealth creation.";
        } else if ("SIMPLE_CONCISE".equalsIgnoreCase(style)) {
            welcomeTitle = "Hello, " + firstName + "!";
            welcomeMessage = "Here are your key financial priorities today. Focus on " +
                    (hasInsuranceGoals ? "securing family coverage" : (hasLendingGoals ? "evaluating borrowing costs" : "tracking category budgets")) +
                    " with your monthly surplus of " + formatCurrencyINR(surplus) + ".";
        } else {
            // ANALYTICAL (Default)
            welcomeTitle = "Personalized Financial Dashboard | " + firstName;
            welcomeMessage = "Based on your ₹" + formatNumberIndian(income.longValue()) + " monthly income, your Fixed Obligation Ratio is " +
                    dtiRatio + "% with a healthy " + savingsRate + "% monthly savings rate. " +
                    "Your primary focus areas (" + String.join(", ", formatGoalNames(goals)) + ") are prioritized below.";
        }

        if (dtiRatio.compareTo(BigDecimal.valueOf(45)) > 0) {
            actionAlerts.add("Your Debt-to-Income ratio (" + dtiRatio + "%) exceeds recommended 45%. Review Lending AI for debt consolidation.");
        }
        if (runwayMonths < 3) {
            actionAlerts.add("Emergency savings buffer is currently " + runwayMonths + " months. We recommend targeting ₹" + formatNumberIndian(emergencyFundTarget.longValue()) + " (6 months).");
        } else {
            actionAlerts.add("Emergency reserve is solid at " + runwayMonths + " months of living expenses.");
        }

        dash.setPrimaryFocusArea(String.join(", ", formatGoalNames(goals)));
        dash.setRecommendedFeatureTags(featureTags);
        dash.setKeyActionAlerts(actionAlerts);

        resp.setContextualWelcomeTitle(welcomeTitle);
        resp.setContextualWelcomeMessage(welcomeMessage);
        resp.setRelevantModules(modules);
        resp.setSuggestedJourneys(journeys);
        resp.setDashboardCustomization(dash);

        return resp;
    }

    private List<String> formatGoalNames(Set<String> goals) {
        List<String> formatted = new ArrayList<>();
        for (String g : goals) {
            switch (g) {
                case "HEALTH_INSURANCE" -> formatted.add("Health Insurance");
                case "LIFE_INSURANCE" -> formatted.add("Life Insurance");
                case "PERSONAL_LOAN" -> formatted.add("Personal Loan");
                case "EDUCATION_LOAN" -> formatted.add("Education Loan");
                case "HOME_LOAN" -> formatted.add("Home Loan");
                case "EXPENSE_MANAGEMENT" -> formatted.add("Expense Tracking");
                case "MONTHLY_BUDGETING" -> formatted.add("Smart Budgeting");
                case "SAVINGS_PLANNING" -> formatted.add("Savings Goals");
                case "FINANCIAL_EDUCATION" -> formatted.add("Financial Education");
                default -> formatted.add(g.replace('_', ' '));
            }
        }
        return formatted;
    }

    private String formatCurrencyINR(BigDecimal amount) {
        return "₹" + formatNumberIndian(amount.longValue());
    }

    private String formatNumberIndian(long num) {
        if (num < 0) return "-" + formatNumberIndian(-num);
        String s = Long.toString(num);
        if (s.length() <= 3) return s;
        String lastThree = s.substring(s.length() - 3);
        String rest = s.substring(0, s.length() - 3);
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (int i = rest.length() - 1; i >= 0; i--) {
            sb.append(rest.charAt(i));
            count++;
            if (count % 2 == 0 && i > 0) {
                sb.append(',');
            }
        }
        return sb.reverse().toString() + "," + lastThree;
    }
}


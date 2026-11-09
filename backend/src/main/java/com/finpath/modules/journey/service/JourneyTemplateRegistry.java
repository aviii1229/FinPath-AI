package com.finpath.modules.journey.service;

import com.finpath.core.exception.BadRequestException;
import com.finpath.modules.journey.dto.JourneyStepTemplateDto;
import com.finpath.modules.journey.dto.JourneyTemplateDto;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class JourneyTemplateRegistry {

    private final Map<String, JourneyTemplateDto> templates = new LinkedHashMap<>();

    public JourneyTemplateRegistry() {
        registerInsuranceJourney();
        registerLendingJourney();
        registerFintechJourney();
    }

    private void registerInsuranceJourney() {
        List<JourneyStepTemplateDto> steps = List.of(
                new JourneyStepTemplateDto(
                        "INSURANCE_DISCOVERY", 1,
                        "Insurance Discovery & Plan Selection",
                        "Identify the optimal insurance category (Health, Term Life, Motor, Family Floater) matching your financial life stage.",
                        List.of(),
                        "Evaluate whether individual or multi-member family floater coverage best matches your dependents.",
                        List.of("category", "coverageType", "targetBudget")
                ),
                new JourneyStepTemplateDto(
                        "REQUIREMENT_COLLECTION", 2,
                        "Health & Financial Requirement Profiling",
                        "Capture age of insured members, city tier, pre-existing health declarations, and sum insured in INR (₹).",
                        List.of(),
                        "Declare all pre-existing medical conditions transparently to ensure zero dispute during hospital claim settlement.",
                        List.of("proposerAge", "sumInsuredInr", "dependentsCount", "preExistingConditions")
                ),
                new JourneyStepTemplateDto(
                        "POLICY_COMPARISON", 3,
                        "Multi-Insurer Policy Matrix Comparison",
                        "Evaluate side-by-side features: room rent limits, cashless hospital network, co-pay clauses, and premium in INR (₹).",
                        List.of(),
                        "Check IRDAI claim settlement ratio (>95% recommended) and ensure zero room rent sub-limits are chosen.",
                        List.of("selectedPlanId", "monthlyPremiumInr", "selectedInsurer")
                ),
                new JourneyStepTemplateDto(
                        "DOCUMENT_PREPARATION", 4,
                        "Document Preparation & Verification",
                        "Prepare and verify KYC identity proof (PAN/Aadhaar) and prior medical records in your Document Vault.",
                        List.of("IDENTITY_PROOF", "ADDRESS_PROOF"),
                        "Ensure your PAN card name exactly matches government databases to prevent underwriting delays.",
                        List.of("panCardVerified", "addressProofDocId")
                ),
                new JourneyStepTemplateDto(
                        "APPLICATION_PREPARATION", 5,
                        "Nominee Designation & Proposal Review",
                        "Specify primary and secondary nominee designations, relationship, and review the underwriting declaration.",
                        List.of(),
                        "Verify nominee date of birth and share percentage. Any simulated proposal requires your explicit confirmation.",
                        List.of("nomineeName", "nomineeRelation", "userConfirmationApproved")
                ),
                new JourneyStepTemplateDto(
                        "APPLICATION_TRACKING", 6,
                        "Proposal Submission & Underwriting Tracking",
                        "Track real-time underwriting milestones, policy schedule issuance, and digital health e-card delivery.",
                        List.of(),
                        "Simulated submission completed under regulatory compliance mode. Policy schedule accessible in Document Vault.",
                        List.of("proposalNumber", "issuanceStatus")
                )
        );

        JourneyTemplateDto template = new JourneyTemplateDto(
                "INSURANCE_APPLICATION_JOURNEY",
                "INSURANCE",
                "Comprehensive Insurance Discovery & Application",
                "End-to-end guided workflow from health/life insurance discovery through requirement collection, comparison, KYC verification, and policy issuance.",
                15,
                steps
        );
        templates.put(template.getJourneyType(), template);
    }

    private void registerLendingJourney() {
        List<JourneyStepTemplateDto> steps = List.of(
                new JourneyStepTemplateDto(
                        "LOAN_DISCOVERY", 1,
                        "Loan Discovery & Product Selection",
                        "Select the loan facility (Home Loan, Personal Loan, Education Loan) with desired financing amount in INR (₹).",
                        List.of(),
                        "Home loans qualify for tax benefits under Section 80C and Section 24b, whereas personal loans offer instant liquidity.",
                        List.of("loanType", "requestedAmountInr")
                ),
                new JourneyStepTemplateDto(
                        "FINANCIAL_INFO_COLLECTION", 2,
                        "Income & Employment Information",
                        "Record employer category, gross monthly salary, existing debt commitments, and FOIR ratio.",
                        List.of(),
                        "Lenders require a Fixed Obligation to Income Ratio (FOIR) below 50% for optimal interest rate pricing.",
                        List.of("monthlyIncomeInr", "existingEmiObligationsInr", "employerType", "tenureYears")
                ),
                new JourneyStepTemplateDto(
                        "EMI_CALCULATION", 3,
                        "Interactive EMI & Amortization Modeling",
                        "Calculate monthly EMI in INR (₹), total interest burden, and payoff horizon across tenure configurations.",
                        List.of(),
                        "Increasing your tenure lowers monthly EMI but significantly increases the cumulative interest payout over time.",
                        List.of("calculatedEmiInr", "totalInterestInr", "tenureMonths")
                ),
                new JourneyStepTemplateDto(
                        "LOAN_COMPARISON", 4,
                        "Lender Comparison & Rate Shopping",
                        "Compare competitive quotes across top scheduled commercial banks and NBFCs.",
                        List.of(),
                        "Floating-rate retail term loans incur zero prepayment/foreclosure penalties per RBI master directives.",
                        List.of("selectedLenderId", "interestRateApr", "processingFeeInr")
                ),
                new JourneyStepTemplateDto(
                        "DOCUMENT_CHECKLIST", 5,
                        "Mandatory Document Checklist",
                        "Gather and link required documents: PAN card, 3 months salary slips, and 6 months bank statement.",
                        List.of("IDENTITY_PROOF", "SALARY_SLIP", "BANK_STATEMENT"),
                        "Bank statements must clearly display salary credits matching your declared employer.",
                        List.of("bankStatementVerified", "salarySlipDocId")
                ),
                new JourneyStepTemplateDto(
                        "APPLICATION_PREPARATION", 6,
                        "Disbursement Account & Mandate Setup",
                        "Specify disbursement savings account number, IFSC code, and verify NACH e-mandate preferences.",
                        List.of(),
                        "Ensure your primary salary account is used for auto-debit NACH mandate to maintain flawless repayment records.",
                        List.of("bankAccountNumber", "ifscCode", "userConfirmationApproved")
                ),
                new JourneyStepTemplateDto(
                        "APPLICATION_TRACKING", 7,
                        "Sanction Letter & Application Tracking",
                        "Track loan file movement, credit committee sanction note, and digital agreement e-signing.",
                        List.of(),
                        "Simulated sanction completed. Sanction letter generated and indexed in your Document Vault.",
                        List.of("sanctionLetterNumber", "disbursementStatus")
                )
        );

        JourneyTemplateDto template = new JourneyTemplateDto(
                "LENDING_APPLICATION_JOURNEY",
                "LENDING",
                "Retail Loan Discovery, Sanction & Tracking",
                "Seamless guided credit journey: loan discovery, FOIR/income assessment, EMI simulation, multi-lender comparison, KYC checklist, and sanction tracking.",
                20,
                steps
        );
        templates.put(template.getJourneyType(), template);
    }

    private void registerFintechJourney() {
        List<JourneyStepTemplateDto> steps = List.of(
                new JourneyStepTemplateDto(
                        "EXPENSE_ANALYSIS", 1,
                        "Expense & Cash Flow Analysis",
                        "Analyze recent bank transactions, recurring subscriptions, and detect spending anomalies in INR (₹).",
                        List.of(),
                        "Categorizing non-essential recurring subscriptions unlocks immediate monthly cash surplus for savings.",
                        List.of("monthlyDiscretionaryInr", "identifiedSubscriptionsCount")
                ),
                new JourneyStepTemplateDto(
                        "BUDGET_CREATION", 2,
                        "50-30-20 Smart Budget Allocation",
                        "Configure structured monthly spending limits in INR (₹) across Needs (50%), Wants (30%), and Savings (20%).",
                        List.of(),
                        "The 50/30/20 guideline creates a predictable monthly surplus while preventing debt dependency.",
                        List.of("needsBudgetInr", "wantsBudgetInr", "savingsBudgetInr")
                ),
                new JourneyStepTemplateDto(
                        "SAVINGS_GOAL_CREATION", 3,
                        "Emergency Fund & Goal Formulation",
                        "Establish an Emergency Safety Reserve equal to 6 months of mandatory living expenses plus target date.",
                        List.of(),
                        "Liquid emergency reserves protect you from liquidating long-term investments during unexpected emergencies.",
                        List.of("emergencyFundTargetInr", "monthlyContributionInr", "targetDate")
                ),
                new JourneyStepTemplateDto(
                        "MONTHLY_FINANCIAL_REVIEW", 4,
                        "Holistic Financial Health & Routine Review",
                        "Run the automated deterministic Financial Health Score audit and review progress against targets.",
                        List.of(),
                        "Routine monthly reviews increase savings goal adherence by over 4x over an annual cycle.",
                        List.of("financialHealthScore", "monthlyReviewApproved")
                )
        );

        JourneyTemplateDto template = new JourneyTemplateDto(
                "FINTECH_WEALTH_JOURNEY",
                "FINTECH",
                "Wealth Discipline & Financial Health Journey",
                "Comprehensive Personal Finance Management: expense analysis, subscription audit, 50-30-20 budget creation, emergency goal planning, and monthly review.",
                12,
                steps
        );
        templates.put(template.getJourneyType(), template);
    }

    public List<JourneyTemplateDto> getAllTemplates() {
        return new ArrayList<>(templates.values());
    }

    public Optional<JourneyTemplateDto> getTemplate(String journeyType) {
        if (journeyType == null) return Optional.empty();
        return Optional.ofNullable(templates.get(journeyType.toUpperCase().trim()));
    }

    public JourneyTemplateDto getRequiredTemplate(String journeyType) {
        return getTemplate(journeyType)
                .orElseThrow(() -> new BadRequestException("Unknown journey type: " + journeyType + ". Valid types: " + templates.keySet()));
    }
}

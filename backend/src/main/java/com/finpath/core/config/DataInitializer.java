package com.finpath.core.config;

import com.finpath.modules.fintech.entity.BudgetEntity;
import com.finpath.modules.fintech.entity.FinancialAccountEntity;
import com.finpath.modules.fintech.entity.SavingsGoalEntity;
import com.finpath.modules.fintech.entity.TransactionEntity;
import com.finpath.modules.fintech.repository.BudgetRepository;
import com.finpath.modules.fintech.repository.FinancialAccountRepository;
import com.finpath.modules.fintech.repository.SavingsGoalRepository;
import com.finpath.modules.fintech.repository.TransactionRepository;
import com.finpath.modules.insurance.entity.InsuranceClaimEntity;
import com.finpath.modules.insurance.entity.InsurancePolicyEntity;
import com.finpath.modules.insurance.entity.InsuranceProductEntity;
import com.finpath.modules.insurance.entity.PolicyDocumentChunkEntity;
import com.finpath.modules.insurance.repository.InsuranceClaimRepository;
import com.finpath.modules.insurance.repository.InsurancePolicyRepository;
import com.finpath.modules.insurance.repository.InsuranceProductRepository;
import com.finpath.modules.insurance.repository.PolicyDocumentChunkRepository;
import com.finpath.modules.lending.entity.CreditProfileEntity;
import com.finpath.modules.lending.entity.LoanApplicationEntity;
import com.finpath.modules.lending.entity.LoanProductEntity;
import com.finpath.modules.lending.entity.RepaymentScheduleEntity;
import com.finpath.modules.lending.repository.CreditProfileRepository;
import com.finpath.modules.lending.repository.LoanApplicationRepository;
import com.finpath.modules.lending.repository.LoanProductRepository;
import com.finpath.modules.lending.repository.RepaymentScheduleRepository;
import com.finpath.modules.notification.entity.NotificationEntity;
import com.finpath.modules.notification.repository.NotificationRepository;
import com.finpath.security.entity.RoleEntity;
import com.finpath.security.entity.UserEntity;
import com.finpath.security.entity.UserProfileEntity;
import com.finpath.security.model.RoleName;
import com.finpath.security.repository.RoleRepository;
import com.finpath.security.repository.UserProfileRepository;
import com.finpath.security.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.HashSet;
import java.util.UUID;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final FinancialAccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;
    private final SavingsGoalRepository savingsGoalRepository;
    private final LoanProductRepository loanProductRepository;
    private final LoanApplicationRepository loanApplicationRepository;
    private final RepaymentScheduleRepository repaymentScheduleRepository;
    private final CreditProfileRepository creditProfileRepository;
    private final InsuranceProductRepository insuranceProductRepository;
    private final InsurancePolicyRepository insurancePolicyRepository;
    private final InsuranceClaimRepository insuranceClaimRepository;
    private final NotificationRepository notificationRepository;
    private final PolicyDocumentChunkRepository policyDocumentChunkRepository;

    public DataInitializer(UserRepository userRepository,
                           RoleRepository roleRepository,
                           UserProfileRepository userProfileRepository,
                           PasswordEncoder passwordEncoder,
                           FinancialAccountRepository accountRepository,
                           TransactionRepository transactionRepository,
                           BudgetRepository budgetRepository,
                           SavingsGoalRepository savingsGoalRepository,
                           LoanProductRepository loanProductRepository,
                           LoanApplicationRepository loanApplicationRepository,
                           RepaymentScheduleRepository repaymentScheduleRepository,
                           CreditProfileRepository creditProfileRepository,
                           InsuranceProductRepository insuranceProductRepository,
                           InsurancePolicyRepository insurancePolicyRepository,
                           InsuranceClaimRepository insuranceClaimRepository,
                           NotificationRepository notificationRepository,
                           PolicyDocumentChunkRepository policyDocumentChunkRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userProfileRepository = userProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.budgetRepository = budgetRepository;
        this.savingsGoalRepository = savingsGoalRepository;
        this.loanProductRepository = loanProductRepository;
        this.loanApplicationRepository = loanApplicationRepository;
        this.repaymentScheduleRepository = repaymentScheduleRepository;
        this.creditProfileRepository = creditProfileRepository;
        this.insuranceProductRepository = insuranceProductRepository;
        this.insurancePolicyRepository = insurancePolicyRepository;
        this.insuranceClaimRepository = insuranceClaimRepository;
        this.notificationRepository = notificationRepository;
        this.policyDocumentChunkRepository = policyDocumentChunkRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // 1. Roles
        RoleEntity customerRole = roleRepository.findById(RoleName.ROLE_CUSTOMER.name())
                .orElseGet(() -> roleRepository.save(new RoleEntity(RoleName.ROLE_CUSTOMER.name(), "Customer Role")));
        roleRepository.findById(RoleName.ROLE_ADMIN.name())
                .orElseGet(() -> roleRepository.save(new RoleEntity(RoleName.ROLE_ADMIN.name(), "Admin Role")));
        roleRepository.findById(RoleName.ROLE_LOAN_OFFICER.name())
                .orElseGet(() -> roleRepository.save(new RoleEntity(RoleName.ROLE_LOAN_OFFICER.name(), "Loan Officer Role")));
        roleRepository.findById(RoleName.ROLE_UNDERWRITER.name())
                .orElseGet(() -> roleRepository.save(new RoleEntity(RoleName.ROLE_UNDERWRITER.name(), "Underwriter Role")));

        // 2. Demo User (Sarah Connor)
        String demoEmail = "sarah.connor@finpath.ai";
        if (userRepository.findByEmail(demoEmail).isEmpty()) {
            log.info("Seeding demo user: {}", demoEmail);

            UserEntity user = new UserEntity(
                    demoEmail,
                    passwordEncoder.encode("SecurePass123!"),
                    "Sarah",
                    "Connor",
                    "+14155552671"
            );
            user.setRoles(new HashSet<>(Collections.singletonList(customerRole)));
            UserEntity savedUser = userRepository.save(user);

            // User Profile
            UserProfileEntity profile = new UserProfileEntity(savedUser);
            profile.setDateOfBirth(LocalDate.of(1992, 5, 14));
            profile.setAge(32);
            profile.setEmploymentCategory("SALARIED");
            profile.setOccupation("Senior Systems Architect");
            profile.setMonthlyIncome(BigDecimal.valueOf(75000.00));
            profile.setMonthlyExpenses(BigDecimal.valueOf(33740.00));
            profile.setExistingObligations(BigDecimal.valueOf(8540.00));
            profile.setApproximateSavings(BigDecimal.valueOf(345250.00));
            profile.setAnnualIncome(BigDecimal.valueOf(900000.00));
            profile.setRiskTolerance("MODERATE");
            profile.setEmploymentStatus("EMPLOYED");
            profile.setCity("Bengaluru");
            profile.setStateProvince("Karnataka");
            profile.setFinancialGoals(new HashSet<>(Set.of(
                    "HEALTH_INSURANCE",
                    "LIFE_INSURANCE",
                    "MONTHLY_BUDGETING",
                    "SAVINGS_PLANNING"
            )));
            profile.setPreferredAssistance("AI_CONVERSATIONAL");
            profile.setFinancialKnowledge("INTERMEDIATE");
            profile.setCommunicationStyle("ANALYTICAL");
            profile.setShortTermGoal("Optimize monthly savings rate to 35% with emergency reserve");
            profile.setLongTermGoal("Family health security and term insurance portfolio");
            profile.setOnboardingCompleted(true);
            profile.setOnboardingStep(4);
            userProfileRepository.save(profile);

            // Credit Profile
            CreditProfileEntity creditProfile = new CreditProfileEntity(
                    savedUser,
                    765,
                    BigDecimal.valueOf(650000.00),
                    BigDecimal.valueOf(8540.00)
            );
            creditProfileRepository.save(creditProfile);

            // 3. Fintech: Accounts
            FinancialAccountEntity chaseChecking = accountRepository.save(new FinancialAccountEntity(
                    savedUser, "HDFC Bank (Salary Account)", "CHECKING", "•••• 4821", BigDecimal.valueOf(142500.50), BigDecimal.valueOf(135000.00)
            ));
            FinancialAccountEntity marcusSavings = accountRepository.save(new FinancialAccountEntity(
                    savedUser, "State Bank of India (Savings)", "SAVINGS", "•••• 9140", BigDecimal.valueOf(450000.00), BigDecimal.valueOf(450000.00)
            ));
            accountRepository.save(new FinancialAccountEntity(
                    savedUser, "Zerodha Coin (Mutual Funds)", "INVESTMENT", "•••• 6632", BigDecimal.valueOf(850000.00), BigDecimal.valueOf(850000.00)
            ));
            FinancialAccountEntity sapphireCard = accountRepository.save(new FinancialAccountEntity(
                    savedUser, "ICICI Bank Coral Credit Card", "CREDIT_CARD", "•••• 3098", BigDecimal.valueOf(-14250.00), BigDecimal.valueOf(185750.00)
            ));

            // Transactions
            transactionRepository.save(new TransactionEntity(chaseChecking, BigDecimal.valueOf(75000.00), LocalDate.now().minusDays(7), "Infosys Technologies (Payroll)", "SALARY", true, "POSTED"));
            transactionRepository.save(new TransactionEntity(chaseChecking, BigDecimal.valueOf(-16000.00), LocalDate.now().minusDays(6), "Prestige Shantiniketan Residences (Rent)", "HOUSING", true, "POSTED"));
            transactionRepository.save(new TransactionEntity(sapphireCard, BigDecimal.valueOf(-3450.00), LocalDate.now().minusDays(4), "Nature Basket & Blinkit (Groceries)", "GROCERIES", false, "POSTED"));
            transactionRepository.save(new TransactionEntity(sapphireCard, BigDecimal.valueOf(-2200.00), LocalDate.now().minusDays(3), "Indian Oil Fuel Station", "TRANSPORTATION", false, "POSTED"));
            transactionRepository.save(new TransactionEntity(chaseChecking, BigDecimal.valueOf(-8540.00), LocalDate.now().minusDays(3), "HDFC Bank Car Loan (Auto EMI)", "LOAN_PAYMENT", true, "POSTED"));
            transactionRepository.save(new TransactionEntity(chaseChecking, BigDecimal.valueOf(-1250.00), LocalDate.now().minusDays(2), "Star Health Family Shield (Premium)", "HEALTHCARE", true, "POSTED"));
            transactionRepository.save(new TransactionEntity(sapphireCard, BigDecimal.valueOf(-1450.00), LocalDate.now().minusDays(1), "Toit Brewpub Indiranagar", "ENTERTAINMENT", false, "POSTED"));
            transactionRepository.save(new TransactionEntity(sapphireCard, BigDecimal.valueOf(-649.00), LocalDate.now(), "Netflix India Subscription", "ENTERTAINMENT", true, "PENDING"));

            // Budgets
            budgetRepository.save(new BudgetEntity(savedUser, "HOUSING", BigDecimal.valueOf(18000.00), 90));
            budgetRepository.save(new BudgetEntity(savedUser, "GROCERIES", BigDecimal.valueOf(8000.00), 85));
            budgetRepository.save(new BudgetEntity(savedUser, "ENTERTAINMENT", BigDecimal.valueOf(5000.00), 80));
            budgetRepository.save(new BudgetEntity(savedUser, "TRANSPORTATION", BigDecimal.valueOf(4000.00), 85));

            // Savings Goals
            savingsGoalRepository.save(new SavingsGoalEntity(
                    savedUser, "6-Month Emergency Safety Reserve", "EMERGENCY_FUND",
                    BigDecimal.valueOf(300000.00), BigDecimal.valueOf(180000.00), LocalDate.now().plusMonths(10)
            ));
            savingsGoalRepository.save(new SavingsGoalEntity(
                    savedUser, "Down Payment for Electric Vehicle (Tata Nexon EV)", "VEHICLE",
                    BigDecimal.valueOf(250000.00), BigDecimal.valueOf(110000.00), LocalDate.now().plusMonths(14)
            ));

            // 4. Lending: Products & Applications
            LoanProductEntity autoLoanProd = new LoanProductEntity(
                    "Clean Energy & Auto Loan", "AUTO", BigDecimal.valueOf(10000), BigDecimal.valueOf(1500000), BigDecimal.valueOf(0.0750), BigDecimal.valueOf(0.0950), 24, 84, 660
            );
            autoLoanProd.setLenderName("HDFC Bank Auto Finance");
            autoLoanProd.setProcessingFeePercentage(BigDecimal.valueOf(0.75));
            autoLoanProd.setInterestRateType("FIXED");
            autoLoanProd.setPrepaymentTerms("No prepayment penalty at any time");
            autoLoanProd.setFeatures("EV Discount 0.25% APR, 100% on-road financing, Instant dealership disbursement");
            autoLoanProd.setSimulated(true);
            autoLoanProd = loanProductRepository.save(autoLoanProd);

            LoanProductEntity personalProd = new LoanProductEntity(
                    "Flexi-Personal Borrowing", "PERSONAL", BigDecimal.valueOf(50000), BigDecimal.valueOf(1000000), BigDecimal.valueOf(0.1050), BigDecimal.valueOf(0.1450), 12, 60, 640
            );
            personalProd.setLenderName("State Bank of India (SBI Xpress)");
            personalProd.setProcessingFeePercentage(BigDecimal.valueOf(1.00));
            personalProd.setInterestRateType("FIXED");
            personalProd.setPrepaymentTerms("Zero exit charge after 6 months");
            personalProd.setFeatures("Funds disbursed within 2 hours, No physical documentation, Automated paperless e-mandate");
            personalProd.setSimulated(true);
            loanProductRepository.save(personalProd);

            LoanProductEntity mortgageProd = new LoanProductEntity(
                    "Conforming 30-Year Home Loan", "MORTGAGE", BigDecimal.valueOf(1500000), BigDecimal.valueOf(20000000), BigDecimal.valueOf(0.0840), BigDecimal.valueOf(0.0920), 180, 360, 650
            );
            mortgageProd.setLenderName("ICICI Home Finance");
            mortgageProd.setProcessingFeePercentage(BigDecimal.valueOf(0.50));
            mortgageProd.setInterestRateType("FLOATING");
            mortgageProd.setPrepaymentTerms("No penalty for up to 20% annual principal curtailment");
            mortgageProd.setFeatures("Rate lock protection for 60 days, Free legal & title evaluation, Digital property valuation");
            mortgageProd.setSimulated(true);
            loanProductRepository.save(mortgageProd);

            LoanProductEntity smeProd = new LoanProductEntity(
                    "FastTrack SME Business Line", "BUSINESS_EXPANSION", BigDecimal.valueOf(500000), BigDecimal.valueOf(5000000), BigDecimal.valueOf(0.1150), BigDecimal.valueOf(0.1600), 12, 84, 680
            );
            smeProd.setLenderName("Axis Bank SME Growth Line");
            smeProd.setProcessingFeePercentage(BigDecimal.valueOf(1.25));
            smeProd.setInterestRateType("FLOATING");
            smeProd.setPrepaymentTerms("1% exit fee in first 12 months, zero thereafter");
            smeProd.setFeatures("Revenue-linked credit limits, Revolving drawdown facility, Zero collateral up to ₹10 Lakhs");
            smeProd.setSimulated(true);
            loanProductRepository.save(smeProd);

            LoanApplicationEntity app1 = new LoanApplicationEntity(
                    "LN-2024-88412", savedUser, autoLoanProd, BigDecimal.valueOf(650000), 60, "VEHICLE_PURCHASE", BigDecimal.valueOf(11.4), BigDecimal.valueOf(8540.00), BigDecimal.valueOf(8.50), "DISBURSED"
            );
            app1.setEmploymentType("SALARIED");
            app1.setMonthlyIncome(BigDecimal.valueOf(75000.00));
            app1.setExistingEmis(BigDecimal.valueOf(0.00));
            app1.setCreditScore(765);
            app1.setDecisionNotes("Approved under prime auto lending tier with NACH auto-debit discount.");
            app1 = loanApplicationRepository.save(app1);

            // Seed sample repayment item
            repaymentScheduleRepository.save(new RepaymentScheduleEntity(
                    app1, 1, LocalDate.now().plusMonths(1), BigDecimal.valueOf(3935.00), BigDecimal.valueOf(4605.00), BigDecimal.valueOf(8540.00), BigDecimal.valueOf(646065.00), "PENDING"
            ));

            // 5. Insurance: Products, Policies & Claims
            InsuranceProductEntity healthProd = new InsuranceProductEntity(
                    "Star Health Insurance", "Comprehensive Platinum Family Shield", "HEALTH", BigDecimal.valueOf(1250.00), BigDecimal.valueOf(500000), BigDecimal.valueOf(2500000), "Comprehensive cashless health cover with ₹15,000 deductible."
            );
            healthProd.setWaitingPeriodDays(30);
            healthProd.setBenefits("100% Inpatient Care, Telehealth Consultations, Emergency Ambulance (₹2,500), AYUSH Treatment, Health Checkup");
            healthProd.setExclusions("Cosmetic surgery, routine adult orthodontics, non-emergency care outside network, experimental procedures");
            healthProd.setDeductibleOptions("₹5,000 / ₹15,000 / ₹25,000");
            healthProd.setCopayPercentage(BigDecimal.valueOf(10.00));
            healthProd.setSimulated(true);
            healthProd = insuranceProductRepository.save(healthProd);

            InsuranceProductEntity lifeProd = new InsuranceProductEntity(
                    "HDFC Life Insurance", "20-Year Term Family Shield", "TERM_LIFE", BigDecimal.valueOf(850.00), BigDecimal.valueOf(2500000), BigDecimal.valueOf(10000000), "Pure term life insurance with guaranteed death benefit."
            );
            lifeProd.setWaitingPeriodDays(0);
            lifeProd.setBenefits("Guaranteed Death Benefit, Accelerated Terminal Illness Benefit, Accidental Death Rider (2x)");
            lifeProd.setExclusions("Suicide within first 24 months, acts of declared war, aviation other than commercial passenger");
            lifeProd.setDeductibleOptions("₹0 (Not Applicable)");
            lifeProd.setCopayPercentage(BigDecimal.ZERO);
            lifeProd.setSimulated(true);
            lifeProd = insuranceProductRepository.save(lifeProd);

            InsuranceProductEntity autoInsurProd = new InsuranceProductEntity(
                    "New India Assurance", "Comprehensive Vehicle Protection", "AUTO", BigDecimal.valueOf(1208.00), BigDecimal.valueOf(300000), BigDecimal.valueOf(800000), "Zero-depreciation collision and third-party protection."
            );
            autoInsurProd.setWaitingPeriodDays(0);
            autoInsurProd.setBenefits("Collision & Comprehensive, 24/7 Roadside Assistance, Consumables Cover, Engine Protector");
            autoInsurProd.setExclusions("Commercial rideshare use without endorsement, unlicensed driving, normal mechanical wear & tear");
            autoInsurProd.setDeductibleOptions("₹1,000 / ₹2,500 / ₹5,000");
            autoInsurProd.setCopayPercentage(BigDecimal.ZERO);
            autoInsurProd.setSimulated(true);
            autoInsurProd = insuranceProductRepository.save(autoInsurProd);

            // Seed Sample Policy Agreement Chunks for AI RAG Assistant
            if (policyDocumentChunkRepository.count() == 0) {
                UUID sampleDocId = UUID.randomUUID();
                String docName = "Aegis_Comprehensive_Platinum_Care_Policy.pdf";

                policyDocumentChunkRepository.save(new PolicyDocumentChunkEntity(
                        sampleDocId, "AEG-HLTH-99482", docName,
                        "Section 1: Schedule of Benefits & Deductibles", 1,
                        "SECTION 1: SCHEDULE OF BENEFITS & COST SHARING\n" +
                        "1.1 Inpatient Hospital Services: Covered at 100% of negotiated allowance after applicable individual deductible (₹15,000/year).\n" +
                        "1.2 Emergency Room Copay: A standard ₹1,000 copayment applies per emergency visit, waived if admitted directly into intensive care.\n" +
                        "1.3 Annual Out-of-Pocket Maximum: Individual maximum is capped at ₹50,000 per policy year; family maximum is ₹1,00,000.",
                        "HEALTH"
                ));

                policyDocumentChunkRepository.save(new PolicyDocumentChunkEntity(
                        sampleDocId, "AEG-HLTH-99482", docName,
                        "Section 2: Emergency & Worldwide Travel Coverage", 3,
                        "SECTION 2: EMERGENCY & WORLDWIDE TRAVEL PROVISIONS\n" +
                        "2.1 Emergency Care While Traveling: Genuine medical emergencies occurring outside the primary service area or internationally are reimbursed at the in-network tier.\n" +
                        "2.2 Medical Repatriation: Air ambulance and evacuation benefit up to ₹10,00,000 when certified necessary by the attending medical officer.\n" +
                        "2.3 Non-Emergency Out of State Care: Elective procedures performed out-of-network require written pre-authorization 14 days prior.",
                        "HEALTH"
                ));

                policyDocumentChunkRepository.save(new PolicyDocumentChunkEntity(
                        sampleDocId, "AEG-HLTH-99482", docName,
                        "Section 4.2: Exclusions & Non-Covered Procedures", 7,
                        "SECTION 4.2: EXCLUSIONS & LIMITATIONS\n" +
                        "The following treatments and expenses are specifically excluded from coverage under this policy:\n" +
                        "(a) Elective cosmetic surgery, aesthetic treatments, and non-reconstructive procedures.\n" +
                        "(b) Adult orthodontic care and routine dental cleanings (unless supplemental Dental rider is elected).\n" +
                        "(c) Injuries sustained while participating in hazardous or extreme motorsports or professional athletic competitions.\n" +
                        "(d) Pre-existing conditions within the first 30 days of coverage (standard waiting period).",
                        "HEALTH"
                ));

                policyDocumentChunkRepository.save(new PolicyDocumentChunkEntity(
                        sampleDocId, "AEG-HLTH-99482", docName,
                        "Section 6: Claim Submission & Deadlines", 12,
                        "SECTION 6: CLAIMS PROCEDURE & ADJUDICATION\n" +
                        "6.1 Submission Window: All claims must be intimated within 48 hours of emergency admission and filed with itemized receipts within 90 days of discharge.\n" +
                        "6.2 Fast-Track Adjudication: Clean claims supported by itemized GST invoices and diagnostic reports under ₹25,000 are adjudicated within 48 business hours.\n" +
                        "6.3 Appeals: Insured members retain the right to dispute adverse benefit determinations within 180 days of notification.",
                        "HEALTH"
                ));
            }

            insurancePolicyRepository.save(new InsurancePolicyEntity(
                    "AEG-HLTH-99482", savedUser, healthProd, BigDecimal.valueOf(1500000), BigDecimal.valueOf(1250.00), BigDecimal.valueOf(0), LocalDate.now().minusMonths(6), LocalDate.now().plusMonths(6), "ACTIVE"
            ));
            InsurancePolicyEntity lifePolicy = insurancePolicyRepository.save(new InsurancePolicyEntity(
                    "PRU-LIFE-38190", savedUser, lifeProd, BigDecimal.valueOf(10000000), BigDecimal.valueOf(850.00), BigDecimal.ZERO, LocalDate.now().minusMonths(11), LocalDate.now().plusDays(50), "RENEWAL_DUE"
            ));
            InsurancePolicyEntity autoPolicy = insurancePolicyRepository.save(new InsurancePolicyEntity(
                    "GEO-AUTO-77123", savedUser, autoInsurProd, BigDecimal.valueOf(650000), BigDecimal.valueOf(1208.00), BigDecimal.valueOf(1000), LocalDate.now().minusMonths(4), LocalDate.now().plusMonths(8), "ACTIVE"
            ));

            // Claims
            insuranceClaimRepository.save(new InsuranceClaimEntity(
                    "CLM-2026-4482", autoPolicy, savedUser, BigDecimal.valueOf(18500.00), LocalDate.now().minusDays(20), "Bumper collision in parking lot.", "APPROVED", BigDecimal.valueOf(4.5)
            ));
            insuranceClaimRepository.save(new InsuranceClaimEntity(
                    "CLM-2026-9021", lifePolicy, savedUser, BigDecimal.valueOf(4200.00), LocalDate.now().minusDays(5), "Preventative outpatient prescription deductible.", "IN_REVIEW", BigDecimal.valueOf(3.8)
            ));

            // 6. Notifications
            notificationRepository.save(new NotificationEntity(savedUser, "Underwriting Approved", "Your Auto Loan pre-qualification passed with 8.50% APR.", "SUCCESS"));
            notificationRepository.save(new NotificationEntity(savedUser, "Insurance Renewal Due", "Term Life Shield policy renewal in 50 days.", "WARNING"));
            notificationRepository.save(new NotificationEntity(savedUser, "Accounts Synced", "HDFC Bank & Zerodha accounts successfully synchronized.", "INFO"));

            log.info("Demo user seed completed successfully!");
        }
    }
}


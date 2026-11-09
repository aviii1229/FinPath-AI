package com.finpath.modules.lending.service;

import com.finpath.modules.lending.dto.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class LoanAiAssistantService {

    public LoanAssistantQueryResponse answerLendingQuery(LoanAssistantQueryRequest request) {
        String q = request.getQuery() != null ? request.getQuery().toLowerCase() : "";

        if (q.contains("apr") || q.contains("flat") || q.contains("interest rate")) {
            return new LoanAssistantQueryResponse(
                    "Annual Percentage Rate (APR) vs Flat Interest Rate",
                    "A flat interest rate calculates interest on your original principal for the entire loan duration, ignoring principal repayments. In contrast, APR / Reducing Balance Rate charges interest strictly on your outstanding balance as you pay it down. A 10% flat rate roughly equals an effective APR of 18-19%. FinPath algorithms always use reducing-balance APR to protect you from inflated true borrowing costs.",
                    "Reducing APR means interest decreases every month as your remaining balance shrinks, whereas flat rates keep charging you interest on money you already repaid.",
                    Arrays.asList(
                            "A $20,000 loan at 8% flat for 3 years costs $4,800 in total interest.",
                            "The same loan at 8% APR reducing balance costs only $2,562 in interest — saving you over $2,238.",
                            "Always insist on seeing the APR and total borrowing cost rather than the nominal headline rate."
                    ),
                    Arrays.asList("Amortization", "Effective Rate", "Equated Monthly Installment (EMI)", "Processing Fee"),
                    Arrays.asList("Use our Interactive EMI Amortization Simulator to compare true cost", "Check if lenders impose front-loaded processing fees")
            );
        }

        if (q.contains("foir") || q.contains("dti") || q.contains("debt to income") || q.contains("eligibility")) {
            return new LoanAssistantQueryResponse(
                    "Fixed Obligation to Income Ratio (FOIR / DTI)",
                    "FOIR (Fixed Obligation to Income Ratio), also known as Debt-to-Income (DTI), measures what percentage of your net monthly income goes toward servicing debt (existing EMIs + proposed new loan EMI). Institutional underwriting guidelines typically mandate a FOIR ceiling of 40% to 50% to ensure you have sufficient discretionary cash flow for living expenses and emergencies.",
                    "FOIR is the fraction of your monthly paycheck committed to loans. If your income is $6,000 and total debt payments are $2,400, your FOIR is 40%.",
                    Arrays.asList(
                            "FOIR < 35%: Excellent tier. Lenders offer preferential interest rate discounts.",
                            "FOIR 36% - 45%: Standard approval range with standard documentation.",
                            "FOIR > 50%: Elevated default risk; underwriters require tenure extensions or debt consolidation."
                    ),
                    Arrays.asList("Net Disposable Income", "Tenure Amortization", "Co-applicant Eligibility", "Debt Consolidation"),
                    Arrays.asList("Extend your proposed tenure from 36 to 60 months to lower the monthly EMI", "Pay off high-interest revolving credit cards to free up debt capacity")
            );
        }

        if (q.contains("amortization") || q.contains("schedule") || q.contains("principal vs interest")) {
            return new LoanAssistantQueryResponse(
                    "Loan Amortization Dynamics",
                    "Amortization is the process of spreading your loan into equal monthly payments. In the early stages of an amortizing loan, the majority of your monthly EMI pays down accrued interest because the principal balance is at its highest. As installments progress, the interest portion declines and the principal repayment component accelerates exponentially.",
                    "Early payments are mostly interest; later payments are mostly principal. This is why prepaying extra principal in the first 1-2 years yields massive lifetime interest savings.",
                    Arrays.asList(
                            "On a 5-year loan, over 45% of your total lifetime interest is paid in the first 18 months.",
                            "Making one extra EMI per year can shorten a 48-month loan by nearly 5-7 months."
                    ),
                    Arrays.asList("Prepayment Penalty", "Principal Component", "Reducing Balance", "Tenure Compression"),
                    Arrays.asList("Review our Amortization Schedule breakdown for month-by-month balance tracking", "Verify if your selected product permits zero-penalty early principal prepayments")
            );
        }

        if (q.contains("prepayment") || q.contains("foreclosure") || q.contains("penalty")) {
            return new LoanAssistantQueryResponse(
                    "Prepayment & Foreclosure Terms",
                    "Prepayment refers to paying extra money toward your loan principal ahead of schedule, while Foreclosure means settling the entire outstanding balance at once. Many institutional lenders charge a prepayment penalty (typically 1% to 3%) during a lock-in period (e.g. initial 6 months), while floating rate consumer loans under modern regulatory codes generally carry zero prepayment penalty.",
                    "Paying off your loan early saves interest, but verify if the lender charges an early exit penalty.",
                    Arrays.asList(
                            "Always check the lock-in window before executing a lump-sum pre-closure.",
                            "FinPath highlighted products feature zero penalty after 6 months."
                    ),
                    Arrays.asList("Lock-in Period", "Foreclosure Fee", "Floating vs Fixed Rate", "Part-Payment"),
                    Arrays.asList("Target loans with transparent prepayment terms in our Loan Comparison Matrix")
            );
        }

        if (q.contains("credit score") || q.contains("cibil") || q.contains("bureau")) {
            return new LoanAssistantQueryResponse(
                    "Credit Score Impact on Lending Spreads",
                    "Your credit score (300-850) is a statistical measure of creditworthiness derived from payment history, credit utilization (<30%), account age, and recent inquiries. A tier-1 credit score (750+) unlocks lowest risk pricing, shaving 1.5% to 3.0% off the annual percentage rate, saving thousands in total interest over typical borrowing tenures.",
                    "Higher scores get lower interest rates. A 780 score can save you $2,400 compared to a 660 score on a $30,000 personal loan.",
                    Arrays.asList(
                            "Tier 1 (750+): Prime rates (4.75% - 6.50% APR) and instant automated approval.",
                            "Tier 2 (670-749): Competitive rates (6.75% - 9.50% APR).",
                            "Tier 3 (<670): Subprime pricing (11.00%+ APR) or requirement for collateral / guarantor."
                    ),
                    Arrays.asList("Credit Utilization Ratio", "Hard Pull vs Soft Pull", "Delinquency Record", "Risk-Based Pricing"),
                    Arrays.asList("Keep credit card balances below 30% of your credit limit prior to formal application")
            );
        }

        if (q.contains("document") || q.contains("missing") || q.contains("requirement") || q.contains("checklist")) {
            return new LoanAssistantQueryResponse(
                    "Underwriting Document Verification & Checklists",
                    "Underwriting teams verify identity, address, repayment capacity, and employment stability. Salaried applicants primarily require recent 3-6 months bank statements, 3 months payslips, and tax forms (W-2/Form 16). Self-employed applicants provide 2 years audited Profit & Loss, Income Tax returns, and 12 months commercial banking statements.",
                    "Having clear digital copies of income proofs and bank statements ready accelerates underwriting decisions from days to minutes.",
                    Arrays.asList(
                            "Unclear or password-protected PDF bank statements are the #1 cause of underwriting delays.",
                            "Ensure account holder name and address match your government ID exactly."
                    ),
                    Arrays.asList("Proof of Income", "KYC Verification", "Bank Statement Analyzer", "Tax Returns"),
                    Arrays.asList("Switch to the 'Checklist' tab in the Lending Hub to download tailored document lists")
            );
        }

        // Default contextual AI response
        return new LoanAssistantQueryResponse(
                "FinPath Lending Intelligence",
                "FinPath Lending AI provides algorithmic guidance across borrowing suitability, debt obligations, amortization schedules, and transparent loan comparisons. Every recommendation is verified against institutional Debt-to-Income safety ratios to ensure long-term financial health.",
                "Ask questions about APR, debt ratios, loan tenure, application documents, or simulated approval conditions.",
                Arrays.asList(
                        "All illustrated eligibility calculations are simulated estimates to guide your decision-making.",
                        "Comparing multiple lenders side-by-side reveals hidden processing fees and prepayment locks."
                ),
                Arrays.asList("APR vs Flat Rate", "FOIR / DTI Limits", "Loan Amortization", "Credit Score Tiers", "Prepayment Policies"),
                Arrays.asList("Try asking: 'What is the difference between APR and Flat Rate?'", "Run the Loan Discovery questionnaire to see your personalized borrowing capacity")
        );
    }

    public LoanChecklistResponse generateChecklist(LoanChecklistRequest request) {
        String empType = request.getEmploymentType() != null ? request.getEmploymentType().toUpperCase() : "SALARIED";
        String loanType = request.getLoanType() != null ? request.getLoanType().toUpperCase() : "PERSONAL";

        List<LoanChecklistResponse.ChecklistItem> mandatory = new ArrayList<>();
        List<LoanChecklistResponse.ChecklistItem> optional = new ArrayList<>();

        // Universal Identity & Address
        mandatory.add(new LoanChecklistResponse.ChecklistItem(
                "GOVT_ID", "Government-Issued Photo ID",
                "Valid Passport, Driver's License, or National Identity Card with clear photo and DOB.",
                true, "High-resolution color scan (PDF or JPEG) without reflections"
        ));
        mandatory.add(new LoanChecklistResponse.ChecklistItem(
                "PROOF_OF_ADDRESS", "Proof of Residential Address",
                "Utility bill, rental agreement, or municipal tax receipt dated within the last 90 days.",
                true, "PDF e-bill or scanned paper document showing full address"
        ));

        // Income Verification based on Employment Type
        if (empType.contains("SELF") || empType.contains("BUSINESS")) {
            mandatory.add(new LoanChecklistResponse.ChecklistItem(
                    "BUSINESS_REG", "Business Registration / Incorporation Certificate",
                    "Certificate of Incorporation, Articles of Association, or Trade License verifying operational history (min 2 years).",
                    true, "Official government corporate registry copy"
            ));
            mandatory.add(new LoanChecklistResponse.ChecklistItem(
                    "BANK_STMT_12M", "12-Month Commercial Bank Statements",
                    "Complete continuous business operating account statements showing revenue turnover.",
                    true, "Original downloaded e-statement directly from bank portal (unlocked PDF)"
            ));
            mandatory.add(new LoanChecklistResponse.ChecklistItem(
                    "ITR_AUDITED_2YR", "2-Year Audited Financials & Tax Returns",
                    "Profit & Loss statements, Balance Sheet, and Tax Computation certified by a CPA / Chartered Accountant.",
                    true, "Signed financial statements and filed ITR acknowledgment"
            ));
            optional.add(new LoanChecklistResponse.ChecklistItem(
                    "GST_SALES_RETURNS", "GST / Sales Tax Quarterly Filings",
                    "Recent 4 quarters of indirect tax filings to validate run-rate revenues.",
                    false, "Government portal summary returns"
            ));
        } else {
            // Salaried
            mandatory.add(new LoanChecklistResponse.ChecklistItem(
                    "PAY_SLIPS_3M", "Latest 3 Months Salary Slips",
                    "Official employer paystubs detailing gross earnings, deductions, PF/401k, and net pay.",
                    true, "Digital PDF stubs with corporate employer seal or signature"
            ));
            mandatory.add(new LoanChecklistResponse.ChecklistItem(
                    "BANK_STMT_6M", "6-Month Salary Account Bank Statements",
                    "Bank statement displaying recurring monthly salary credit narrations and regular expenditure.",
                    true, "Original PDF downloaded from internet banking"
            ));
            mandatory.add(new LoanChecklistResponse.ChecklistItem(
                    "TAX_FORM_W2_16", "Annual Tax Statement (Form 16 / W-2)",
                    "Employer annual tax withholding certificate for the preceding financial year.",
                    true, "Part A and B certificate or standard Form W-2"
            ));
            optional.add(new LoanChecklistResponse.ChecklistItem(
                    "EMPLOYER_OFFER_ID", "Corporate Employee ID Card / Appointment Letter",
                    "Valid corporate work badge or signed confirmation of permanent employment.",
                    false, "Clear photo or scanned PDF"
            ));
        }

        // Loan Type Specific additions
        if (loanType.contains("HOME") || loanType.contains("MORTGAGE")) {
            mandatory.add(new LoanChecklistResponse.ChecklistItem(
                    "PROPERTY_TITLE", "Title Deed & Sale Agreement Draft",
                    "Chain of ownership title documents, approved municipal building plan, and draft agreement to sell.",
                    true, "Certified legal copy from registered authority"
            ));
            optional.add(new LoanChecklistResponse.ChecklistItem(
                    "ENCUMBRANCE_CERT", "Encumbrance Certificate (13-30 Years)",
                    "Verification that property is free from prior mortgages, liens, or legal encumbrances.",
                    false, "Registrar sub-office search report"
            ));
        } else if (loanType.contains("AUTO") || loanType.contains("VEHICLE")) {
            mandatory.add(new LoanChecklistResponse.ChecklistItem(
                    "VEHICLE_QUOTATION", "Dealer Pro-Forma Invoice & Booking Receipt",
                    "Authorized vehicle dealer quotation showing ex-showroom price, taxes, and registration fee breakdown.",
                    true, "Official dealer invoice on company letterhead"
            ));
        } else if (loanType.contains("CONSOLIDATION")) {
            mandatory.add(new LoanChecklistResponse.ChecklistItem(
                    "DEBT_STATEMENTS", "Existing Loan Outstanding Statements",
                    "Payoff quote or current balance statements of all credit lines to be extinguished.",
                    true, "Latest loan account statements or credit card bills"
            ));
        }

        List<String> criteria = Arrays.asList(
                "Minimum age: 21 years (max 65 at tenure completion)",
                "Minimum net monthly income: $2,500 (Salaried) or $4,000 (Self-Employed)",
                "Minimum employment vintage: 1 year with current employer (Salaried) or 2 years in business",
                "Maximum FOIR / DTI ceiling: 45.0% inclusive of proposed EMI"
        );

        List<String> tips = Arrays.asList(
                "Download bank statements directly from your bank's portal rather than scanning printed sheets to prevent OCR reading errors.",
                "Remove any passwords from uploaded PDF files so automated underwriting algorithms can process them without delay.",
                "Ensure salary credits show corporate employer narration matching your pay slips."
        );

        return new LoanChecklistResponse(loanType, empType, mandatory, optional, criteria, tips);
    }
}


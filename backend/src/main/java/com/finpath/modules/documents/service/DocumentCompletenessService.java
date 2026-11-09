package com.finpath.modules.documents.service;

import com.finpath.modules.ai.entity.DocumentUploadEntity;
import com.finpath.modules.ai.repository.DocumentUploadRepository;
import com.finpath.modules.documents.dto.DocumentRequirementItemDto;
import com.finpath.modules.documents.dto.JourneyCompletenessDto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DocumentCompletenessService {

    private final DocumentUploadRepository documentRepository;

    public DocumentCompletenessService(DocumentUploadRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public JourneyCompletenessDto evaluateCompleteness(UUID userId, String journeyType) {
        String normalizedJourney = (journeyType != null ? journeyType.toUpperCase().trim() : "PERSONAL_LOAN");
        List<DocumentUploadEntity> userDocs = documentRepository.findByUserIdOrderByCreatedAtDesc(userId);

        List<RequirementSpec> specs = getRequirementsForJourney(normalizedJourney);
        List<DocumentRequirementItemDto> items = new ArrayList<>();
        List<String> missing = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        int totalRequired = 0;
        int fulfilledRequired = 0;

        for (RequirementSpec spec : specs) {
            if (spec.required) totalRequired++;

            Optional<DocumentUploadEntity> match = userDocs.stream()
                    .filter(d -> spec.category.equalsIgnoreCase(d.getDocumentCategory()))
                    .findFirst();

            if (match.isPresent()) {
                DocumentUploadEntity doc = match.get();
                boolean isExpired = doc.getExpiryDate() != null && doc.getExpiryDate().isBefore(LocalDate.now());

                if (isExpired) {
                    warnings.add(spec.title + " has expired (" + doc.getExpiryDate() + "). Please upload an updated copy.");
                }

                if (spec.required && !isExpired) {
                    fulfilledRequired++;
                }

                items.add(new DocumentRequirementItemDto(
                        spec.key,
                        spec.title,
                        spec.description,
                        spec.category,
                        spec.required,
                        !isExpired,
                        doc.getId(),
                        doc.getFileName(),
                        isExpired,
                        isExpired ? "Document expired on " + doc.getExpiryDate() : "Verified & available in vault"
                ));
            } else {
                if (spec.required) {
                    missing.add(spec.title);
                }
                items.add(new DocumentRequirementItemDto(
                        spec.key,
                        spec.title,
                        spec.description,
                        spec.category,
                        spec.required,
                        false,
                        null,
                        null,
                        false,
                        spec.required ? "Required - Missing from vault" : "Optional"
                ));
            }
        }

        int percentage = totalRequired > 0 ? (fulfilledRequired * 100 / totalRequired) : 100;
        boolean ready = (fulfilledRequired >= totalRequired);

        String journeyTitle = switch (normalizedJourney) {
            case "HOME_LOAN" -> "Home Loan Application Checklist";
            case "PERSONAL_LOAN" -> "Personal Loan Instant Sanction Checklist";
            case "HEALTH_INSURANCE" -> "Health Insurance Policy Underwriting Checklist";
            case "TERM_LIFE" -> "Term Life Insurance Documentation";
            case "INSURANCE_CLAIM" -> "Health & Medical Claim Reimbursement Checklist";
            default -> "Financial Onboarding Verification Checklist";
        };

        return new JourneyCompletenessDto(
                normalizedJourney,
                journeyTitle,
                totalRequired,
                fulfilledRequired,
                percentage,
                ready,
                items,
                missing,
                warnings
        );
    }

    private List<RequirementSpec> getRequirementsForJourney(String journey) {
        return switch (journey) {
            case "HOME_LOAN" -> List.of(
                    new RequirementSpec("PAN_CARD", "Identity Proof (PAN Card)", "Mandatory for KYC and credit bureau verification", "IDENTITY_PROOF", true),
                    new RequirementSpec("ADDRESS_PROOF", "Current Address Proof", "Utility bill, rent agreement, or Aadhaar", "ADDRESS_PROOF", true),
                    new RequirementSpec("SALARY_SLIPS", "Recent Payslips (Last 3 Months)", "Proof of monthly income and stability", "SALARY_SLIP", true),
                    new RequirementSpec("BANK_STATEMENT", "Bank Statement (Last 6 Months)", "Bank account statement showing regular salary credits", "BANK_STATEMENT", true),
                    new RequirementSpec("FORM_16", "Form 16 / Income Tax Return", "Tax returns for previous 2 assessment years", "TAX_DOCUMENT", true),
                    new RequirementSpec("PROPERTY_DOCS", "Property Agreement / Sanction Letter", "Draft sale agreement or prior sanction note", "LOAN_AGREEMENT", false)
            );
            case "HEALTH_INSURANCE" -> List.of(
                    new RequirementSpec("IDENTITY_PROOF", "Identity Proof (Aadhaar / PAN)", "KYC requirement per IRDAI master circular", "IDENTITY_PROOF", true),
                    new RequirementSpec("ADDRESS_PROOF", "Residential Address Proof", "Electricity / water bill or passport copy", "ADDRESS_PROOF", true),
                    new RequirementSpec("EXISTING_POLICY", "Existing Policy / Prior Medical Records", "For portability or pre-existing disease declaration", "INSURANCE_POLICY", false)
            );
            case "TERM_LIFE" -> List.of(
                    new RequirementSpec("IDENTITY_PROOF", "Government Identity Proof (PAN Card)", "Mandatory tax identifier for life insurance underwriting", "IDENTITY_PROOF", true),
                    new RequirementSpec("INCOME_PROOF", "Income Proof (Form 16 / Salary Slip)", "Financial underwriting for high sum assured", "SALARY_SLIP", true),
                    new RequirementSpec("ADDRESS_PROOF", "Address Proof", "Permanent and communication address verification", "ADDRESS_PROOF", true)
            );
            case "INSURANCE_CLAIM" -> List.of(
                    new RequirementSpec("POLICY_SCHEDULE", "Policy Schedule / Health Card", "Active policy document with sum insured details", "INSURANCE_POLICY", true),
                    new RequirementSpec("IDENTITY_PROOF", "Patient & Proposer Identity Proof", "PAN or Aadhaar for identity verification", "IDENTITY_PROOF", true),
                    new RequirementSpec("HOSPITAL_BILLS", "Hospital Bills / Discharge Summary", "Itemized final bills, discharge card, and pharmacy receipts", "OTHER_FINANCIAL", true)
            );
            default -> List.of( // PERSONAL_LOAN
                    new RequirementSpec("PAN_CARD", "Identity Proof (PAN Card)", "Required for instant credit bureau check", "IDENTITY_PROOF", true),
                    new RequirementSpec("ADDRESS_PROOF", "Address Proof", "Current residential address proof", "ADDRESS_PROOF", true),
                    new RequirementSpec("SALARY_SLIPS", "Latest Payslip", "Most recent salary slip", "SALARY_SLIP", true),
                    new RequirementSpec("BANK_STATEMENT", "Bank Statement (Last 3 Months)", "Bank statement showing salary credits", "BANK_STATEMENT", true)
            );
        };
    }

    private static class RequirementSpec {
        String key;
        String title;
        String description;
        String category;
        boolean required;

        RequirementSpec(String key, String title, String description, String category, boolean required) {
            this.key = key;
            this.title = title;
            this.description = description;
            this.category = category;
            this.required = required;
        }
    }
}

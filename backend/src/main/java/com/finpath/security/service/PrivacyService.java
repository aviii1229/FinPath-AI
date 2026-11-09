package com.finpath.security.service;

import com.finpath.core.exception.ResourceNotFoundException;
import com.finpath.modules.fintech.entity.TransactionEntity;
import com.finpath.modules.fintech.repository.TransactionRepository;
import com.finpath.modules.insurance.entity.InsurancePolicyEntity;
import com.finpath.modules.insurance.repository.InsurancePolicyRepository;
import com.finpath.modules.journey.entity.UserJourneyEntity;
import com.finpath.modules.journey.repository.UserJourneyRepository;
import com.finpath.modules.lending.entity.LoanApplicationEntity;
import com.finpath.modules.lending.repository.LoanApplicationRepository;
import com.finpath.modules.notification.entity.NotificationEntity;
import com.finpath.modules.notification.repository.NotificationRepository;
import com.finpath.security.dto.DataUsageExplanationDto;
import com.finpath.security.dto.PrivacyConsentDto;
import com.finpath.security.dto.UserDataExportDto;
import com.finpath.security.dto.UserDto;
import com.finpath.security.entity.UserEntity;
import com.finpath.security.entity.UserProfileEntity;
import com.finpath.security.entity.UserPrivacyConsentEntity;
import com.finpath.security.jwt.TokenRevocationService;
import com.finpath.security.repository.RefreshTokenRepository;
import com.finpath.security.repository.UserProfileRepository;
import com.finpath.security.repository.UserPrivacyConsentRepository;
import com.finpath.security.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PrivacyService {

    private static final Logger log = LoggerFactory.getLogger(PrivacyService.class);

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserPrivacyConsentRepository privacyConsentRepository;
    private final TransactionRepository transactionRepository;
    private final InsurancePolicyRepository insurancePolicyRepository;
    private final LoanApplicationRepository loanApplicationRepository;
    private final UserJourneyRepository userJourneyRepository;
    private final NotificationRepository notificationRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenRevocationService tokenRevocationService;
    private final AuditLogService auditLogService;
    private final AuthService authService;

    public PrivacyService(UserRepository userRepository,
                          UserProfileRepository userProfileRepository,
                          UserPrivacyConsentRepository privacyConsentRepository,
                          TransactionRepository transactionRepository,
                          InsurancePolicyRepository insurancePolicyRepository,
                          LoanApplicationRepository loanApplicationRepository,
                          UserJourneyRepository userJourneyRepository,
                          NotificationRepository notificationRepository,
                          RefreshTokenRepository refreshTokenRepository,
                          TokenRevocationService tokenRevocationService,
                          AuditLogService auditLogService,
                          AuthService authService) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.privacyConsentRepository = privacyConsentRepository;
        this.transactionRepository = transactionRepository;
        this.insurancePolicyRepository = insurancePolicyRepository;
        this.loanApplicationRepository = loanApplicationRepository;
        this.userJourneyRepository = userJourneyRepository;
        this.notificationRepository = notificationRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenRevocationService = tokenRevocationService;
        this.auditLogService = auditLogService;
        this.authService = authService;
    }

    @Transactional(readOnly = true)
    public PrivacyConsentDto getConsents(UUID userId) {
        UserPrivacyConsentEntity entity = privacyConsentRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserEntity user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
                    return privacyConsentRepository.save(new UserPrivacyConsentEntity(user));
                });
        return toDto(entity);
    }

    @Transactional
    public PrivacyConsentDto updateConsents(UUID userId, PrivacyConsentDto dto, String ip, String userAgent) {
        UserPrivacyConsentEntity entity = privacyConsentRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserEntity user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
                    return new UserPrivacyConsentEntity(user);
                });

        entity.setDataCollectionConsent(dto.isDataCollectionConsent());
        entity.setAiProcessingConsent(dto.isAiProcessingConsent());
        entity.setMarketingCommunicationConsent(dto.isMarketingCommunicationConsent());
        entity.setThirdPartySharingConsent(dto.isThirdPartySharingConsent());
        entity.setDocumentOcrStorageConsent(dto.isDocumentOcrStorageConsent());
        entity.setStoreChatHistory(dto.isStoreChatHistory());
        entity.setConsentVersion(dto.getConsentVersion() != null ? dto.getConsentVersion() : "v2026.1");

        UserPrivacyConsentEntity saved = privacyConsentRepository.save(entity);

        auditLogService.logSecurityEvent(
                userId,
                "CONSENT_UPDATED",
                "PRIVACY_CONSENT",
                saved.getId().toString(),
                ip,
                userAgent,
                "Updated privacy consent options: AI=" + dto.isAiProcessingConsent() + ", OCR=" + dto.isDocumentOcrStorageConsent()
        );

        return toDto(saved);
    }

    public DataUsageExplanationDto getDataUsageExplanation() {
        Map<String, String> dataCategories = new LinkedHashMap<>();
        dataCategories.put("Identity & Contact", "Full name, phone, city, age used solely for KYC verification and profile personalization.");
        dataCategories.put("Financial Transactions", "Transaction history and monthly income used for automated categorization, budgeting limits, and cash flow forecasting.");
        dataCategories.put("Loan Applications & EMI", "Loan amount requested, tenure, and credit score used strictly for institutional lender comparison and EMI amortization schedule generation.");
        dataCategories.put("Insurance Policies & Claims", "Policy documents, coverage amounts, and claim status used to evaluate claim readiness and renewal notifications.");
        dataCategories.put("Document Vault Files", "Uploaded PDFs and salary slips processed using secure local OCR for parameter extraction with non-KYC validation disclaimers.");
        dataCategories.put("AI Chat Conversations", "Chat turn history processed for conversational intent orchestration and journey step completion.");

        List<String> rights = List.of(
                "Right to Access: Review all personal and financial data stored in FinPath AI.",
                "Right to Correction: Update or rectify outdated demographic or financial information.",
                "Right to Erasure: Request permanent account deletion and data anonymization.",
                "Right to Data Portability: Download your entire account history in machine-readable JSON format.",
                "Right to Grievance Redressal: Contact the Data Protection Officer for privacy inquiries or disputes."
        );

        return new DataUsageExplanationDto(
                "Digital Personal Data Protection (DPDP) Act, 2023 & RBI Digital Lending Guidelines",
                "FinPath AI Technologies Pvt. Ltd. (Data Fiduciary)",
                "Explicit Consent (Section 6, DPDP Act 2023) and Legitimate Financial Contract Performance",
                dataCategories,
                rights,
                "Financial transaction and audit logs retained in accordance with RBI statutory guidelines (5 years); AI conversational sessions purged upon user request.",
                "dpo@finpath.ai | Data Protection Officer, Hyderabad Tech Hub, Telangana, India"
        );
    }

    @Transactional(readOnly = true)
    public UserDataExportDto exportUserData(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        UserDto userDto = authService.getCurrentUser(userId);
        PrivacyConsentDto consents = getConsents(userId);

        List<TransactionEntity> txs = transactionRepository.findAllByUserId(userId);
        List<Map<String, Object>> txList = txs.stream().map(t -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", t.getId());
            m.put("amount", t.getAmount());
            m.put("merchant", t.getMerchantName());
            m.put("category", t.getCategory());
            m.put("date", t.getTransactionDate());
            return m;
        }).collect(Collectors.toList());

        List<InsurancePolicyEntity> policies = insurancePolicyRepository.findByUserId(userId);
        List<Map<String, Object>> policyList = policies.stream().map(p -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", p.getId());
            m.put("policyNumber", p.getPolicyNumber());
            m.put("coverageAmount", p.getCoverageAmount());
            m.put("premiumAmount", p.getPremiumAmount());
            m.put("status", p.getStatus());
            m.put("endDate", p.getEndDate());
            return m;
        }).collect(Collectors.toList());

        List<LoanApplicationEntity> loans = loanApplicationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        List<Map<String, Object>> loanList = loans.stream().map(l -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", l.getId());
            m.put("applicationNumber", l.getApplicationNumber());
            m.put("requestedAmount", l.getRequestedAmount());
            m.put("purpose", l.getLoanPurpose());
            m.put("status", l.getStatus());
            return m;
        }).collect(Collectors.toList());

        List<UserJourneyEntity> journeys = userJourneyRepository.findByUserIdOrderByUpdatedAtDesc(userId);
        List<Map<String, Object>> journeyList = journeys.stream().map(j -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", j.getId());
            m.put("title", j.getTitle());
            m.put("domain", j.getJourneyDomain());
            m.put("status", j.getStatus());
            m.put("progress", j.getProgressPercentage());
            return m;
        }).collect(Collectors.toList());

        List<NotificationEntity> notifs = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        List<Map<String, Object>> notifList = notifs.stream().map(n -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", n.getId());
            m.put("title", n.getTitle());
            m.put("category", n.getCategory());
            m.put("isRead", n.isRead());
            return m;
        }).collect(Collectors.toList());

        UserDataExportDto export = new UserDataExportDto();
        export.setExportId("EXP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        export.setExportedAt(Instant.now());
        export.setDataSubjectEmail(user.getEmail());
        export.setProfile(userDto);
        export.setTransactions(txList);
        export.setInsurancePolicies(policyList);
        export.setLoanApplications(loanList);
        export.setCustomerJourneys(journeyList);
        export.setNotifications(notifList);
        export.setPrivacyConsents(consents);
        export.setLegalNotice("Generated under Section 12 (Right to Data Portability) of the Digital Personal Data Protection Act, 2023. Contains confidential financial information.");

        auditLogService.logSecurityEvent(
                userId,
                "DATA_EXPORT_REQUESTED",
                "USER_DATA_EXPORT",
                export.getExportId(),
                "127.0.0.1",
                "Web Browser",
                "Full machine-readable financial portfolio export generated"
        );

        return export;
    }

    @Transactional
    public void requestAccountDeletion(UUID userId, String authHeader, String ip, String userAgent) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        // 1. Soft-delete and anonymize user entity
        user.setStatus("DELETED");
        user.setEmail("deleted-" + userId + "@anonymized.finpath.ai");
        user.setFirstName("Anonymized");
        user.setLastName("User");
        user.setPhoneNumber(null);
        user.setPasswordHash("$2a$10$DELETED_ACCOUNT_HASH_PLACEHOLDER_NO_ACCESS");
        userRepository.save(user);

        // 2. Anonymize user profile
        userProfileRepository.findByUserId(userId).ifPresent(p -> {
            p.setAddressLine1("REDACTED");
            p.setCity("REDACTED");
            p.setPostalCode("000000");
            userProfileRepository.save(p);
        });

        // 3. Revoke all refresh tokens and access token
        refreshTokenRepository.revokeAllForUser(userId);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            tokenRevocationService.revokeToken(authHeader.substring(7));
        }

        auditLogService.logSecurityEvent(
                userId,
                "ACCOUNT_DELETION_REQUESTED",
                "USER_ACCOUNT",
                userId.toString(),
                ip,
                userAgent,
                "Account marked DELETED and personal identifiers scrubbed under DPDP Right to Erasure"
        );

        log.info("User {} account deletion and anonymization executed successfully", userId);
    }

    private PrivacyConsentDto toDto(UserPrivacyConsentEntity entity) {
        return new PrivacyConsentDto(
                entity.isDataCollectionConsent(),
                entity.isAiProcessingConsent(),
                entity.isMarketingCommunicationConsent(),
                entity.isThirdPartySharingConsent(),
                entity.isDocumentOcrStorageConsent(),
                entity.isStoreChatHistory(),
                entity.getConsentVersion(),
                entity.getConsentTimestamp(),
                entity.getUpdatedAt()
        );
    }
}

package com.finpath.modules.notification.service;

import com.finpath.core.exception.ResourceNotFoundException;
import com.finpath.modules.fintech.entity.BudgetEntity;
import com.finpath.modules.fintech.entity.SavingsGoalEntity;
import com.finpath.modules.fintech.entity.TransactionEntity;
import com.finpath.modules.fintech.repository.BudgetRepository;
import com.finpath.modules.fintech.repository.SavingsGoalRepository;
import com.finpath.modules.fintech.repository.TransactionRepository;
import com.finpath.modules.insurance.entity.InsurancePolicyEntity;
import com.finpath.modules.insurance.repository.InsurancePolicyRepository;
import com.finpath.modules.journey.entity.UserJourneyEntity;
import com.finpath.modules.journey.repository.UserJourneyRepository;
import com.finpath.modules.lending.entity.LoanApplicationEntity;
import com.finpath.modules.lending.entity.RepaymentScheduleEntity;
import com.finpath.modules.lending.repository.LoanApplicationRepository;
import com.finpath.modules.lending.repository.RepaymentScheduleRepository;
import com.finpath.modules.notification.channel.NotificationChannelDispatcher;
import com.finpath.modules.notification.dto.EvaluateNotificationsResponseDto;
import com.finpath.modules.notification.dto.NotificationDto;
import com.finpath.modules.notification.entity.NotificationEntity;
import com.finpath.modules.notification.entity.NotificationPreferenceEntity;
import com.finpath.modules.notification.repository.NotificationPreferenceRepository;
import com.finpath.modules.notification.repository.NotificationRepository;
import com.finpath.security.entity.UserEntity;
import com.finpath.security.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class ProactiveNotificationService {

    private static final Logger log = LoggerFactory.getLogger(ProactiveNotificationService.class);
    private static final Locale LOCALE_IN = Locale.forLanguageTag("en-IN");

    private final NotificationRepository notificationRepository;
    private final NotificationPreferenceRepository preferenceRepository;
    private final UserRepository userRepository;
    private final RepaymentScheduleRepository repaymentScheduleRepository;
    private final LoanApplicationRepository loanApplicationRepository;
    private final InsurancePolicyRepository insurancePolicyRepository;
    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;
    private final SavingsGoalRepository savingsGoalRepository;
    private final UserJourneyRepository userJourneyRepository;
    private final NotificationChannelDispatcher channelDispatcher;
    private final NotificationService notificationService;

    public ProactiveNotificationService(NotificationRepository notificationRepository,
                                        NotificationPreferenceRepository preferenceRepository,
                                        UserRepository userRepository,
                                        RepaymentScheduleRepository repaymentScheduleRepository,
                                        LoanApplicationRepository loanApplicationRepository,
                                        InsurancePolicyRepository insurancePolicyRepository,
                                        BudgetRepository budgetRepository,
                                        TransactionRepository transactionRepository,
                                        SavingsGoalRepository savingsGoalRepository,
                                        UserJourneyRepository userJourneyRepository,
                                        NotificationChannelDispatcher channelDispatcher,
                                        NotificationService notificationService) {
        this.notificationRepository = notificationRepository;
        this.preferenceRepository = preferenceRepository;
        this.userRepository = userRepository;
        this.repaymentScheduleRepository = repaymentScheduleRepository;
        this.loanApplicationRepository = loanApplicationRepository;
        this.insurancePolicyRepository = insurancePolicyRepository;
        this.budgetRepository = budgetRepository;
        this.transactionRepository = transactionRepository;
        this.savingsGoalRepository = savingsGoalRepository;
        this.userJourneyRepository = userJourneyRepository;
        this.channelDispatcher = channelDispatcher;
        this.notificationService = notificationService;
    }

    /**
     * Evaluates all proactive financial rules against real user data.
     * Prevents duplicate alerts and respects user preferences and quiet hours.
     */
    @Transactional
    public EvaluateNotificationsResponseDto evaluateProactiveNotifications(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        NotificationPreferenceEntity prefs = preferenceRepository.findByUserId(userId)
                .orElseGet(() -> preferenceRepository.save(new NotificationPreferenceEntity(user)));

        List<NotificationDto> generatedDtos = new ArrayList<>();
        int evaluatedRules = 0;
        int skippedDuplicates = 0;
        LocalDate today = LocalDate.now();
        Instant now = Instant.now();

        // -------------------------------------------------------------
        // 1. LOAN EMI REMINDERS
        // -------------------------------------------------------------
        evaluatedRules++;
        if (prefs.isLoanEmiAlerts()) {
            int reminderDays = prefs.getReminderDaysBeforeEmi() > 0 ? prefs.getReminderDaysBeforeEmi() : 3;
            LocalDate emiCutoff = today.plusDays(reminderDays);

            List<LoanApplicationEntity> applications = loanApplicationRepository.findByUserIdOrderByCreatedAtDesc(userId);
            for (LoanApplicationEntity app : applications) {
                List<RepaymentScheduleEntity> schedules = repaymentScheduleRepository
                        .findByApplicationIdOrderByInstallmentNumberAsc(app.getId());

                for (RepaymentScheduleEntity schedule : schedules) {
                    if ("PENDING".equalsIgnoreCase(schedule.getStatus())
                            && !schedule.getDueDate().isBefore(today)
                            && !schedule.getDueDate().isAfter(emiCutoff)) {

                        String dedupKey = String.format("LOAN_EMI:%s:%d:%s",
                                app.getId(), schedule.getInstallmentNumber(), schedule.getDueDate());

                        if (isDuplicate(userId, dedupKey)) {
                            skippedDuplicates++;
                            continue;
                        }

                        String productName = (app.getLoanProduct() != null) ? app.getLoanProduct().getProductName() : "Loan";
                        String title = "Upcoming Loan EMI Due";
                        String description = String.format(
                                "Installment #%d of ₹%s for %s (%s) is due on %s. Please maintain sufficient balance in your linked account.",
                                schedule.getInstallmentNumber(),
                                formatInr(schedule.getTotalPaymentDue()),
                                productName,
                                app.getApplicationNumber(),
                                schedule.getDueDate()
                        );

                        NotificationEntity notification = new NotificationEntity(
                                user, title, description, "WARNING", "LOAN_EMI", "HIGH"
                        );
                        notification.setActionUrl("/lending");
                        notification.setActionLabel("Review Schedule");
                        notification.setDeduplicationKey(dedupKey);

                        String deliveryStatus = channelDispatcher.dispatch(notification, prefs, now);
                        notification.setChannelDeliveryJson(deliveryStatus);

                        NotificationEntity saved = notificationRepository.save(notification);
                        generatedDtos.add(notificationService.toDto(saved));
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // 2. INSURANCE POLICY RENEWAL REMINDERS
        // -------------------------------------------------------------
        evaluatedRules++;
        if (prefs.isInsuranceRenewalAlerts()) {
            int renewalDays = prefs.getReminderDaysBeforeRenewal() > 0 ? prefs.getReminderDaysBeforeRenewal() : 15;
            LocalDate renewalCutoff = today.plusDays(renewalDays);

            List<InsurancePolicyEntity> policies = insurancePolicyRepository.findByUserId(userId);
            for (InsurancePolicyEntity policy : policies) {
                if (("ACTIVE".equalsIgnoreCase(policy.getStatus()) || "RENEWAL_DUE".equalsIgnoreCase(policy.getStatus()))
                        && !policy.getEndDate().isBefore(today)
                        && !policy.getEndDate().isAfter(renewalCutoff)) {

                    String dedupKey = String.format("INSURANCE_RENEWAL:%s:%s",
                            policy.getPolicyNumber(), policy.getEndDate());

                    if (isDuplicate(userId, dedupKey)) {
                        skippedDuplicates++;
                        continue;
                    }

                    long daysLeft = ChronoUnit.DAYS.between(today, policy.getEndDate());
                    String priority = daysLeft <= 7 ? "HIGH" : "MEDIUM";
                    String productName = (policy.getProduct() != null) ? policy.getProduct().getProductName() : "Insurance Policy";

                    String title = "Insurance Policy Renewal Approaching";
                    String description = String.format(
                            "Your %s policy (#%s) expires on %s (%d days left). Renewal premium is ₹%s.",
                            productName,
                            policy.getPolicyNumber(),
                            policy.getEndDate(),
                            daysLeft,
                            formatInr(policy.getPremiumAmount())
                    );

                    NotificationEntity notification = new NotificationEntity(
                            user, title, description, "WARNING", "INSURANCE_RENEWAL", priority
                    );
                    notification.setActionUrl("/insurance");
                    notification.setActionLabel("Renew Policy");
                    notification.setDeduplicationKey(dedupKey);

                    String deliveryStatus = channelDispatcher.dispatch(notification, prefs, now);
                    notification.setChannelDeliveryJson(deliveryStatus);

                    NotificationEntity saved = notificationRepository.save(notification);
                    generatedDtos.add(notificationService.toDto(saved));
                }
            }
        }

        // -------------------------------------------------------------
        // 3. BUDGET THRESHOLD ALERTS
        // -------------------------------------------------------------
        evaluatedRules++;
        if (prefs.isBudgetThresholdAlerts()) {
            List<BudgetEntity> budgets = budgetRepository.findByUserId(userId);
            for (BudgetEntity b : budgets) {
                if (b.getMonthlyLimit() != null && b.getMonthlyLimit().compareTo(BigDecimal.ZERO) > 0) {
                    List<TransactionEntity> categoryTxs = transactionRepository.findByUserIdAndCategory(userId, b.getCategory());
                    BigDecimal spent = categoryTxs.stream()
                            .filter(t -> t.getTransactionDate().getYear() == today.getYear()
                                    && t.getTransactionDate().getMonth() == today.getMonth())
                            .map(TransactionEntity::getAmount)
                            .filter(a -> a.compareTo(BigDecimal.ZERO) < 0)
                            .map(BigDecimal::abs)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal percent = spent.multiply(BigDecimal.valueOf(100))
                            .divide(b.getMonthlyLimit(), 0, RoundingMode.HALF_UP);

                    int threshold = b.getAlertThresholdPercent() > 0 ? b.getAlertThresholdPercent() : 85;

                    if (percent.intValue() >= threshold) {
                        String dedupKey = String.format("BUDGET_ALERT:%s:%s:%d-%02d",
                                userId, b.getCategory(), today.getYear(), today.getMonthValue());

                        if (isDuplicate(userId, dedupKey)) {
                            skippedDuplicates++;
                            continue;
                        }

                        String priority = percent.intValue() >= 100 ? "CRITICAL" : "HIGH";
                        String title = "Budget Alert: Limit Reached";
                        String description = String.format(
                                "Your monthly %s budget has reached %d%% (₹%s spent of ₹%s limit). Review spending to stay on track.",
                                b.getCategory(),
                                percent.intValue(),
                                formatInr(spent),
                                formatInr(b.getMonthlyLimit())
                        );

                        NotificationEntity notification = new NotificationEntity(
                                user, title, description, "WARNING", "BUDGET_ALERT", priority
                        );
                        notification.setActionUrl("/fintech");
                        notification.setActionLabel("View Budget");
                        notification.setDeduplicationKey(dedupKey);

                        String deliveryStatus = channelDispatcher.dispatch(notification, prefs, now);
                        notification.setChannelDeliveryJson(deliveryStatus);

                        NotificationEntity saved = notificationRepository.save(notification);
                        generatedDtos.add(notificationService.toDto(saved));
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // 4. SAVINGS GOAL REMINDERS
        // -------------------------------------------------------------
        evaluatedRules++;
        if (prefs.isSavingsGoalAlerts()) {
            List<SavingsGoalEntity> goals = savingsGoalRepository.findByUserIdOrderByCreatedAtDesc(userId);
            for (SavingsGoalEntity goal : goals) {
                if ("IN_PROGRESS".equalsIgnoreCase(goal.getStatus())
                        && goal.getTargetAmount() != null
                        && goal.getTargetAmount().compareTo(BigDecimal.ZERO) > 0
                        && goal.getTargetDate() != null
                        && goal.getTargetDate().isAfter(today)) {

                    BigDecimal saved = goal.getCurrentSavedAmount() != null ? goal.getCurrentSavedAmount() : BigDecimal.ZERO;
                    BigDecimal target = goal.getTargetAmount();
                    BigDecimal percent = saved.multiply(BigDecimal.valueOf(100)).divide(target, 0, RoundingMode.HALF_UP);

                    // If less than 50% saved and target date is within 6 months
                    long monthsUntilTarget = ChronoUnit.MONTHS.between(today, goal.getTargetDate());
                    if (percent.intValue() < 50 && monthsUntilTarget <= 6) {
                        String dedupKey = String.format("SAVINGS_GOAL:%s:%d-%02d",
                                goal.getId(), today.getYear(), today.getMonthValue());

                        if (isDuplicate(userId, dedupKey)) {
                            skippedDuplicates++;
                            continue;
                        }

                        String title = "Savings Goal Progress Alert";
                        String description = String.format(
                                "Your savings goal '%s' is progressing slower than planned. Currently saved ₹%s of ₹%s target (%d%%) with %d months remaining.",
                                goal.getTitle(),
                                formatInr(saved),
                                formatInr(target),
                                percent.intValue(),
                                Math.max(1, monthsUntilTarget)
                        );

                        NotificationEntity notification = new NotificationEntity(
                                user, title, description, "INFO", "SAVINGS_GOAL", "MEDIUM"
                        );
                        notification.setActionUrl("/fintech");
                        notification.setActionLabel("Add Contribution");
                        notification.setDeduplicationKey(dedupKey);

                        String deliveryStatus = channelDispatcher.dispatch(notification, prefs, now);
                        notification.setChannelDeliveryJson(deliveryStatus);

                        NotificationEntity savedEntity = notificationRepository.save(notification);
                        generatedDtos.add(notificationService.toDto(savedEntity));
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // 5. INCOMPLETE FINANCIAL JOURNEY REMINDERS
        // -------------------------------------------------------------
        evaluatedRules++;
        if (prefs.isJourneyIncompleteAlerts()) {
            List<UserJourneyEntity> journeys = userJourneyRepository.findByUserIdOrderByUpdatedAtDesc(userId);
            for (UserJourneyEntity journey : journeys) {
                if ("IN_PROGRESS".equalsIgnoreCase(journey.getStatus()) || "PAUSED".equalsIgnoreCase(journey.getStatus())) {
                    String dedupKey = String.format("JOURNEY_INCOMPLETE:%s:%s",
                            journey.getId(), journey.getCurrentStepKey());

                    if (isDuplicate(userId, dedupKey)) {
                        skippedDuplicates++;
                        continue;
                    }

                    String title = "Incomplete Financial Journey";
                    String description = String.format(
                            "You have an incomplete %s at step '%s' (%d%% completed). Resume to finalize your application.",
                            journey.getTitle(),
                            journey.getCurrentStepKey(),
                            journey.getProgressPercentage()
                    );

                    NotificationEntity notification = new NotificationEntity(
                            user, title, description, "INFO", "JOURNEY_INCOMPLETE", "MEDIUM"
                    );
                    notification.setActionUrl("/journeys");
                    notification.setActionLabel("Resume Journey");
                    notification.setDeduplicationKey(dedupKey);

                    String deliveryStatus = channelDispatcher.dispatch(notification, prefs, now);
                    notification.setChannelDeliveryJson(deliveryStatus);

                    NotificationEntity savedEntity = notificationRepository.save(notification);
                    generatedDtos.add(notificationService.toDto(savedEntity));
                }
            }
        }

        // -------------------------------------------------------------
        // 6. MISSING DOCUMENT REMINDERS
        // -------------------------------------------------------------
        evaluatedRules++;
        if (prefs.isDocumentMissingAlerts()) {
            List<UserJourneyEntity> journeys = userJourneyRepository.findByUserIdOrderByUpdatedAtDesc(userId);
            for (UserJourneyEntity journey : journeys) {
                if (("IN_PROGRESS".equalsIgnoreCase(journey.getStatus()) || "PAUSED".equalsIgnoreCase(journey.getStatus()))
                        && (journey.getCurrentStepKey() != null && journey.getCurrentStepKey().toUpperCase().contains("DOCUMENT"))) {

                    String dedupKey = String.format("DOCUMENT_MISSING:%s:%s",
                            journey.getId(), journey.getCurrentStepKey());

                    if (isDuplicate(userId, dedupKey)) {
                        skippedDuplicates++;
                        continue;
                    }

                    String title = "Missing Documents Required";
                    String description = String.format(
                            "Your %s requires verification documents before advancing. Upload required files in the Document Vault.",
                            journey.getTitle()
                    );

                    NotificationEntity notification = new NotificationEntity(
                            user, title, description, "WARNING", "DOCUMENT_MISSING", "HIGH"
                    );
                    notification.setActionUrl("/documents");
                    notification.setActionLabel("Upload Documents");
                    notification.setDeduplicationKey(dedupKey);

                    String deliveryStatus = channelDispatcher.dispatch(notification, prefs, now);
                    notification.setChannelDeliveryJson(deliveryStatus);

                    NotificationEntity savedEntity = notificationRepository.save(notification);
                    generatedDtos.add(notificationService.toDto(savedEntity));
                }
            }
        }

        log.info("Proactive evaluation complete for user {}: {} rules checked, {} generated, {} skipped duplicates",
                userId, evaluatedRules, generatedDtos.size(), skippedDuplicates);

        return new EvaluateNotificationsResponseDto(
                evaluatedRules,
                generatedDtos.size(),
                skippedDuplicates,
                generatedDtos,
                now
        );
    }

    private boolean isDuplicate(UUID userId, String deduplicationKey) {
        if (deduplicationKey == null || deduplicationKey.isBlank()) {
            return false;
        }
        return notificationRepository.existsByUserIdAndDeduplicationKey(userId, deduplicationKey);
    }

    private String formatInr(BigDecimal amount) {
        if (amount == null) return "0";
        NumberFormat nf = NumberFormat.getInstance(LOCALE_IN);
        nf.setMaximumFractionDigits(0);
        nf.setMinimumFractionDigits(0);
        return nf.format(amount);
    }
}

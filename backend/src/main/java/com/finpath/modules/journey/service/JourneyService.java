package com.finpath.modules.journey.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finpath.core.exception.BadRequestException;
import com.finpath.core.exception.ResourceNotFoundException;
import com.finpath.modules.journey.dto.*;
import com.finpath.modules.journey.entity.JourneyStepRecordEntity;
import com.finpath.modules.journey.entity.UserJourneyEntity;
import com.finpath.modules.journey.repository.JourneyStepRecordRepository;
import com.finpath.modules.journey.repository.UserJourneyRepository;
import com.finpath.security.entity.UserEntity;
import com.finpath.security.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
public class JourneyService {

    private static final Logger log = LoggerFactory.getLogger(JourneyService.class);

    private final UserJourneyRepository journeyRepository;
    private final JourneyStepRecordRepository stepRecordRepository;
    private final UserRepository userRepository;
    private final JourneyTemplateRegistry templateRegistry;
    private final JourneyStateMachine stateMachine;
    private final ObjectMapper objectMapper;

    public JourneyService(
            UserJourneyRepository journeyRepository,
            JourneyStepRecordRepository stepRecordRepository,
            UserRepository userRepository,
            JourneyTemplateRegistry templateRegistry,
            JourneyStateMachine stateMachine,
            ObjectMapper objectMapper) {
        this.journeyRepository = journeyRepository;
        this.stepRecordRepository = stepRecordRepository;
        this.userRepository = userRepository;
        this.templateRegistry = templateRegistry;
        this.stateMachine = stateMachine;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public UserJourneyDto startJourney(UUID userId, StartJourneyRequest request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        JourneyTemplateDto template = templateRegistry.getRequiredTemplate(request.getJourneyType());

        JourneyStepTemplateDto firstStep = template.getSteps().get(0);
        UserJourneyEntity journey = new UserJourneyEntity(
                user,
                template.getJourneyType(),
                template.getDomain(),
                template.getTitle(),
                template.getDescription(),
                template.getTotalSteps(),
                firstStep.getStepKey()
        );

        if (request.getInitialData() != null && !request.getInitialData().isEmpty()) {
            journey.setJourneyDataJson(toJson(request.getInitialData()));
        }

        for (int i = 0; i < template.getSteps().size(); i++) {
            JourneyStepTemplateDto stepTpl = template.getSteps().get(i);
            String initialStatus = (i == 0) ? "IN_PROGRESS" : "PENDING";
            JourneyStepRecordEntity stepRecord = new JourneyStepRecordEntity(
                    journey,
                    stepTpl.getStepKey(),
                    stepTpl.getStepOrder(),
                    stepTpl.getTitle(),
                    stepTpl.getDescription(),
                    initialStatus,
                    toJson(stepTpl.getRequiredDocuments()),
                    stepTpl.getAiGuidance()
            );
            journey.addStepRecord(stepRecord);
        }

        journey.calculateProgress();
        UserJourneyEntity saved = journeyRepository.save(journey);
        return mapToDto(saved);
    }

    @Transactional
    public UserJourneyDto pauseJourney(UUID userId, UUID journeyId, Long expectedVersion) {
        UserJourneyEntity journey = getJourneyEntity(userId, journeyId);
        stateMachine.validateOptimisticLock(journey, expectedVersion);
        stateMachine.validateStatusTransition(journey.getStatus(), "PAUSED");

        journey.setStatus("PAUSED");
        journey.setUpdatedAt(Instant.now());
        UserJourneyEntity saved = journeyRepository.save(journey);
        return mapToDto(saved);
    }

    @Transactional
    public UserJourneyDto resumeJourney(UUID userId, UUID journeyId, Long expectedVersion) {
        UserJourneyEntity journey = getJourneyEntity(userId, journeyId);
        stateMachine.validateOptimisticLock(journey, expectedVersion);
        stateMachine.validateStatusTransition(journey.getStatus(), "IN_PROGRESS");

        journey.setStatus("IN_PROGRESS");
        journey.setUpdatedAt(Instant.now());
        UserJourneyEntity saved = journeyRepository.save(journey);
        return mapToDto(saved);
    }

    @Transactional
    public UserJourneyDto updateStepData(UUID userId, UUID journeyId, UpdateStepDataRequest request) {
        UserJourneyEntity journey = getJourneyEntity(userId, journeyId);
        stateMachine.validateOptimisticLock(journey, request.getExpectedVersion());

        JourneyStepRecordEntity stepRecord = findStepRecord(journey, request.getStepKey());

        // Update step data
        stepRecord.setStepDataJson(toJson(request.getStepData()));
        stepRecord.setUpdatedAt(Instant.now());

        // Merge into global journey data
        mergeStepDataIntoJourney(journey, request.getStepKey(), request.getStepData());
        journey.setUpdatedAt(Instant.now());

        UserJourneyEntity saved = journeyRepository.save(journey);
        return mapToDto(saved);
    }

    @Transactional
    public UserJourneyDto completeStep(UUID userId, UUID journeyId, CompleteStepRequest request) {
        UserJourneyEntity journey = getJourneyEntity(userId, journeyId);
        stateMachine.validateOptimisticLock(journey, request.getExpectedVersion());

        JourneyStepRecordEntity currentStep = findStepRecord(journey, request.getStepKey());
        stateMachine.validateStepCompletion(journey, request.getStepKey(), currentStep);

        // Save submitted step data
        if (request.getStepData() != null) {
            currentStep.setStepDataJson(toJson(request.getStepData()));
            mergeStepDataIntoJourney(journey, request.getStepKey(), request.getStepData());
        }

        currentStep.setStatus("COMPLETED");
        currentStep.setCompletedAt(Instant.now());
        currentStep.setUpdatedAt(Instant.now());

        // Check if there is a next step
        List<JourneyStepRecordEntity> steps = journey.getStepRecords();
        int currentOrder = currentStep.getStepOrder();
        Optional<JourneyStepRecordEntity> nextStepOpt = steps.stream()
                .filter(s -> s.getStepOrder() == currentOrder + 1)
                .findFirst();

        if (nextStepOpt.isPresent()) {
            JourneyStepRecordEntity nextStep = nextStepOpt.get();
            if ("PENDING".equalsIgnoreCase(nextStep.getStatus())) {
                nextStep.setStatus("IN_PROGRESS");
                nextStep.setUpdatedAt(Instant.now());
            }
            journey.setCurrentStepKey(nextStep.getStepKey());
            journey.setCurrentStepIndex(nextStep.getStepOrder() - 1);
        } else {
            // Final step completed -> Journey completes!
            journey.setStatus("COMPLETED");
            journey.setCompletedAt(Instant.now());
        }

        journey.calculateProgress();
        journey.setUpdatedAt(Instant.now());

        UserJourneyEntity saved = journeyRepository.save(journey);
        return mapToDto(saved);
    }

    @Transactional
    public UserJourneyDto cancelJourney(UUID userId, UUID journeyId, CancelJourneyRequest request) {
        UserJourneyEntity journey = getJourneyEntity(userId, journeyId);
        stateMachine.validateOptimisticLock(journey, request != null ? request.getExpectedVersion() : null);
        stateMachine.validateStatusTransition(journey.getStatus(), "CANCELLED");

        journey.setStatus("CANCELLED");
        journey.setCancellationReason(request != null && request.getReason() != null ? request.getReason() : "Cancelled by user");
        journey.setUpdatedAt(Instant.now());

        UserJourneyEntity saved = journeyRepository.save(journey);
        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public List<UserJourneyDto> getUserJourneys(UUID userId, String status, String domain) {
        String cleanStatus = (status != null && !status.isBlank() && !status.equalsIgnoreCase("ALL")) ? status.trim().toUpperCase() : null;
        String cleanDomain = (domain != null && !domain.isBlank() && !domain.equalsIgnoreCase("ALL")) ? domain.trim().toUpperCase() : null;

        List<UserJourneyEntity> entities = journeyRepository.searchUserJourneys(userId, cleanStatus, cleanDomain);
        return entities.stream().map(this::mapToDto).toList();
    }

    @Transactional(readOnly = true)
    public UserJourneyDto getJourney(UUID userId, UUID journeyId) {
        UserJourneyEntity entity = getJourneyEntity(userId, journeyId);
        return mapToDto(entity);
    }

    @Transactional(readOnly = true)
    public JourneyStatsDto getJourneyStats(UUID userId) {
        List<UserJourneyEntity> all = journeyRepository.findByUserIdOrderByUpdatedAtDesc(userId);

        JourneyStatsDto stats = new JourneyStatsDto();
        stats.setTotalJourneys(all.size());

        long active = 0;
        long completed = 0;
        long paused = 0;
        long cancelled = 0;
        int totalProgress = 0;
        Map<String, Long> domains = new HashMap<>();

        for (UserJourneyEntity j : all) {
            String st = j.getStatus();
            if ("IN_PROGRESS".equalsIgnoreCase(st)) active++;
            else if ("COMPLETED".equalsIgnoreCase(st)) completed++;
            else if ("PAUSED".equalsIgnoreCase(st)) paused++;
            else if ("CANCELLED".equalsIgnoreCase(st)) cancelled++;

            totalProgress += j.getProgressPercentage();

            String d = j.getJourneyDomain();
            domains.put(d, domains.getOrDefault(d, 0L) + 1);
        }

        stats.setActiveJourneys(active);
        stats.setCompletedJourneys(completed);
        stats.setPausedJourneys(paused);
        stats.setCancelledJourneys(cancelled);
        stats.setAverageProgressPercentage(all.isEmpty() ? 0 : totalProgress / all.size());
        stats.setDomainDistribution(domains);

        return stats;
    }

    @Transactional(readOnly = true)
    public List<JourneyRecommendationDto> getRecommendations(UUID userId) {
        List<UserJourneyEntity> existing = journeyRepository.findByUserIdOrderByUpdatedAtDesc(userId);
        Set<String> activeTypes = new HashSet<>();
        for (UserJourneyEntity j : existing) {
            if ("IN_PROGRESS".equalsIgnoreCase(j.getStatus()) || "PAUSED".equalsIgnoreCase(j.getStatus())) {
                activeTypes.add(j.getJourneyType());
            }
        }

        List<JourneyRecommendationDto> recs = new ArrayList<>();

        if (!activeTypes.contains("FINTECH_WEALTH_JOURNEY")) {
            recs.add(new JourneyRecommendationDto(
                    "FINTECH_WEALTH_JOURNEY", "FINTECH",
                    "Wealth Discipline & Financial Health Journey",
                    "Build a predictable monthly surplus and emergency reserve with structured 50-30-20 budget rules.",
                    12, "EXPENSE_ANALYSIS", 1
            ));
        }

        if (!activeTypes.contains("INSURANCE_APPLICATION_JOURNEY")) {
            recs.add(new JourneyRecommendationDto(
                    "INSURANCE_APPLICATION_JOURNEY", "INSURANCE",
                    "Comprehensive Insurance Discovery & Application",
                    "Secure optimal health cover and family floater protection with transparent policy comparison in INR (₹).",
                    15, "INSURANCE_DISCOVERY", 2
            ));
        }

        if (!activeTypes.contains("LENDING_APPLICATION_JOURNEY")) {
            recs.add(new JourneyRecommendationDto(
                    "LENDING_APPLICATION_JOURNEY", "LENDING",
                    "Retail Loan Discovery, Sanction & Tracking",
                    "Simulate retail loan affordability, evaluate FOIR eligibility, and complete paperless sanction preparation.",
                    20, "LOAN_DISCOVERY", 3
            ));
        }

        return recs;
    }

    @Transactional(readOnly = true)
    public List<JourneyTemplateDto> getTemplates() {
        return templateRegistry.getAllTemplates();
    }

    private UserJourneyEntity getJourneyEntity(UUID userId, UUID journeyId) {
        return journeyRepository.findByIdAndUserId(journeyId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Journey not found or access denied: " + journeyId));
    }

    private JourneyStepRecordEntity findStepRecord(UserJourneyEntity journey, String stepKey) {
        return journey.getStepRecords().stream()
                .filter(s -> s.getStepKey().equalsIgnoreCase(stepKey))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Step '" + stepKey + "' is not part of journey " + journey.getTitle()));
    }

    private void mergeStepDataIntoJourney(UserJourneyEntity journey, String stepKey, Map<String, Object> stepData) {
        if (stepData == null || stepData.isEmpty()) return;

        Map<String, Object> global = fromJson(journey.getJourneyDataJson());
        global.put(stepKey, stepData);
        journey.setJourneyDataJson(toJson(global));
    }

    private UserJourneyDto mapToDto(UserJourneyEntity entity) {
        UserJourneyDto dto = new UserJourneyDto();
        dto.setId(entity.getId());
        dto.setJourneyType(entity.getJourneyType());
        dto.setJourneyDomain(entity.getJourneyDomain());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setStatus(entity.getStatus());
        dto.setCurrentStepKey(entity.getCurrentStepKey());
        dto.setCurrentStepIndex(entity.getCurrentStepIndex());
        dto.setTotalSteps(entity.getTotalSteps());
        dto.setProgressPercentage(entity.getProgressPercentage());
        dto.setJourneyData(fromJson(entity.getJourneyDataJson()));
        dto.setCancellationReason(entity.getCancellationReason());
        dto.setSimulated(entity.isSimulated());
        dto.setVersion(entity.getVersion());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setCompletedAt(entity.getCompletedAt());

        List<JourneyStepRecordDto> stepDtos = new ArrayList<>();
        List<String> pendingDocs = new ArrayList<>();
        String nextAction = null;

        for (JourneyStepRecordEntity s : entity.getStepRecords()) {
            JourneyStepRecordDto sDto = new JourneyStepRecordDto();
            sDto.setId(s.getId());
            sDto.setStepKey(s.getStepKey());
            sDto.setStepOrder(s.getStepOrder());
            sDto.setTitle(s.getTitle());
            sDto.setDescription(s.getDescription());
            sDto.setStatus(s.getStatus());
            sDto.setStepData(fromJson(s.getStepDataJson()));
            sDto.setRequiredDocuments(fromJsonList(s.getRequiredDocumentsJson()));
            sDto.setAiGuidance(s.getAiGuidance());
            sDto.setCompletedAt(s.getCompletedAt());
            sDto.setUpdatedAt(s.getUpdatedAt());
            stepDtos.add(sDto);

            if ("IN_PROGRESS".equalsIgnoreCase(s.getStatus()) && nextAction == null) {
                nextAction = "Complete Step " + s.getStepOrder() + ": " + s.getTitle();
                if (sDto.getRequiredDocuments() != null) {
                    pendingDocs.addAll(sDto.getRequiredDocuments());
                }
            }
        }

        if (nextAction == null) {
            nextAction = "COMPLETED".equalsIgnoreCase(entity.getStatus()) ? "Journey Successfully Completed" : "Resume Journey Workflow";
        }

        dto.setSteps(stepDtos);
        dto.setNextRecommendedAction(nextAction);
        dto.setPendingRequiredDocuments(pendingDocs);
        return dto;
    }

    private String toJson(Object obj) {
        if (obj == null) return "{}";
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize to JSON: {}", e.getMessage());
            return "{}";
        }
    }

    private Map<String, Object> fromJson(String json) {
        if (json == null || json.isBlank()) return new HashMap<>();
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    private List<String> fromJsonList(String json) {
        if (json == null || json.isBlank()) return new ArrayList<>();
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}


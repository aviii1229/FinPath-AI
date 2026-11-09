package com.finpath.modules.journey.service;

import com.finpath.core.exception.BadRequestException;
import com.finpath.modules.journey.entity.JourneyStepRecordEntity;
import com.finpath.modules.journey.entity.UserJourneyEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class JourneyStateMachine {

    // Status State Transitions Map
    private static final Map<String, Set<String>> ALLOWED_STATUS_TRANSITIONS = Map.of(
            "NOT_STARTED", Set.of("IN_PROGRESS", "CANCELLED"),
            "IN_PROGRESS", Set.of("PAUSED", "COMPLETED", "CANCELLED"),
            "PAUSED", Set.of("IN_PROGRESS", "CANCELLED"),
            "COMPLETED", Set.of(), // Terminal
            "CANCELLED", Set.of()  // Terminal
    );

    public void validateStatusTransition(String currentStatus, String targetStatus) {
        if (currentStatus == null || targetStatus == null) {
            throw new BadRequestException("Current or target status cannot be null");
        }

        if (currentStatus.equalsIgnoreCase(targetStatus)) {
            return; // No-op idempotent transition
        }

        Set<String> allowed = ALLOWED_STATUS_TRANSITIONS.getOrDefault(currentStatus.toUpperCase(), Set.of());
        if (!allowed.contains(targetStatus.toUpperCase())) {
            throw new BadRequestException(String.format(
                    "Invalid journey state transition from '%s' to '%s'. Allowed transitions: %s",
                    currentStatus, targetStatus, allowed
            ));
        }
    }

    public void validateStepCompletion(UserJourneyEntity journey, String stepKey, JourneyStepRecordEntity stepRecord) {
        if (!"IN_PROGRESS".equalsIgnoreCase(journey.getStatus())) {
            throw new BadRequestException(String.format(
                    "Cannot complete step '%s' when journey is in '%s' status. Resume journey first.",
                    stepKey, journey.getStatus()
            ));
        }

        if (!stepRecord.getStepKey().equalsIgnoreCase(stepKey)) {
            throw new BadRequestException(String.format(
                    "Step record key mismatch: Expected '%s', provided '%s'",
                    stepRecord.getStepKey(), stepKey
            ));
        }

        // Validate sequential step progression
        List<JourneyStepRecordEntity> steps = journey.getStepRecords();
        int stepOrder = stepRecord.getStepOrder();

        // Ensure all prior steps (order < stepOrder) are already COMPLETED or at least initialized
        for (JourneyStepRecordEntity prior : steps) {
            if (prior.getStepOrder() < stepOrder && !"COMPLETED".equalsIgnoreCase(prior.getStatus()) && !"SKIPPED".equalsIgnoreCase(prior.getStatus())) {
                throw new BadRequestException(String.format(
                        "Cannot advance to step '%s' (Order %d): Prior step '%s' (Order %d) is still pending.",
                        stepRecord.getTitle(), stepOrder, prior.getTitle(), prior.getStepOrder()
                ));
            }
        }
    }

    public void validateOptimisticLock(UserJourneyEntity journey, Long expectedVersion) {
        if (expectedVersion != null && !expectedVersion.equals(journey.getVersion())) {
            throw new BadRequestException(String.format(
                    "Concurrent modification detected: Journey was modified by another operation. Current version is %d, but request expected version %d. Please refresh and retry.",
                    journey.getVersion(), expectedVersion
            ));
        }
    }
}

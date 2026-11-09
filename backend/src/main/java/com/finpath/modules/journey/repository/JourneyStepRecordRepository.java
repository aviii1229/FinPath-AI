package com.finpath.modules.journey.repository;

import com.finpath.modules.journey.entity.JourneyStepRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JourneyStepRecordRepository extends JpaRepository<JourneyStepRecordEntity, UUID> {

    List<JourneyStepRecordEntity> findByJourneyIdOrderByStepOrderAsc(UUID journeyId);

    Optional<JourneyStepRecordEntity> findByJourneyIdAndStepKey(UUID journeyId, String stepKey);
}

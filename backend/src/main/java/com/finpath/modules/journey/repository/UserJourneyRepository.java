package com.finpath.modules.journey.repository;

import com.finpath.modules.journey.entity.UserJourneyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserJourneyRepository extends JpaRepository<UserJourneyEntity, UUID> {

    List<UserJourneyEntity> findByUserIdOrderByUpdatedAtDesc(UUID userId);

    List<UserJourneyEntity> findByUserIdAndStatusOrderByUpdatedAtDesc(UUID userId, String status);

    List<UserJourneyEntity> findByUserIdAndJourneyDomainOrderByUpdatedAtDesc(UUID userId, String journeyDomain);

    Optional<UserJourneyEntity> findByIdAndUserId(UUID id, UUID userId);

    long countByUserId(UUID userId);

    long countByUserIdAndStatus(UUID userId, String status);

    @Query("SELECT j FROM UserJourneyEntity j WHERE j.user.id = :userId " +
           "AND (:status IS NULL OR j.status = :status) " +
           "AND (:domain IS NULL OR j.journeyDomain = :domain) " +
           "ORDER BY j.updatedAt DESC")
    List<UserJourneyEntity> searchUserJourneys(
            @Param("userId") UUID userId,
            @Param("status") String status,
            @Param("domain") String domain);
}


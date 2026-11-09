package com.finpath.modules.notification.repository;

import com.finpath.modules.notification.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, UUID> {

    List<NotificationEntity> findByUserIdOrderByCreatedAtDesc(UUID userId);

    List<NotificationEntity> findByUserIdAndIsReadOrderByCreatedAtDesc(UUID userId, boolean isRead);

    Optional<NotificationEntity> findByIdAndUserId(UUID id, UUID userId);

    long countByUserId(UUID userId);

    long countByUserIdAndIsReadFalse(UUID userId);

    long countByUserIdAndPriorityAndIsReadFalse(UUID userId, String priority);

    boolean existsByUserIdAndDeduplicationKey(UUID userId, String deduplicationKey);

    Optional<NotificationEntity> findFirstByUserIdAndDeduplicationKeyOrderByCreatedAtDesc(UUID userId, String deduplicationKey);

    @Query("SELECT n FROM NotificationEntity n WHERE n.user.id = :userId " +
           "AND (:category IS NULL OR n.category = :category) " +
           "AND (:isRead IS NULL OR n.isRead = :isRead) " +
           "AND (:priority IS NULL OR n.priority = :priority) " +
           "ORDER BY n.createdAt DESC")
    List<NotificationEntity> searchNotifications(
            @Param("userId") UUID userId,
            @Param("category") String category,
            @Param("isRead") Boolean isRead,
            @Param("priority") String priority);

    @Modifying
    @Query("UPDATE NotificationEntity n SET n.isRead = true, n.readAt = :readAt WHERE n.user.id = :userId AND n.isRead = false")
    int markAllAsReadForUser(@Param("userId") UUID userId, @Param("readAt") java.time.Instant readAt);

    @Modifying
    @Query("DELETE FROM NotificationEntity n WHERE n.user.id = :userId AND n.isRead = true")
    void deleteAllReadByUserId(@Param("userId") UUID userId);
}

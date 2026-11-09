package com.finpath.security.repository;

import com.finpath.security.entity.UserPrivacyConsentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserPrivacyConsentRepository extends JpaRepository<UserPrivacyConsentEntity, UUID> {

    Optional<UserPrivacyConsentEntity> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);
}

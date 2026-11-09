package com.finpath.security.service;

import com.finpath.core.exception.BadRequestException;
import com.finpath.core.exception.ResourceNotFoundException;
import com.finpath.security.dto.*;
import com.finpath.security.entity.RefreshTokenEntity;
import com.finpath.security.entity.RoleEntity;
import com.finpath.security.entity.UserEntity;
import com.finpath.security.entity.UserProfileEntity;
import com.finpath.security.jwt.JwtTokenProvider;
import com.finpath.security.jwt.TokenRevocationService;
import com.finpath.security.model.RoleName;
import com.finpath.security.repository.RefreshTokenRepository;
import com.finpath.security.repository.RoleRepository;
import com.finpath.security.repository.UserProfileRepository;
import com.finpath.security.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserProfileRepository userProfileRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final TokenRevocationService tokenRevocationService;
    private final LoginRateLimiter rateLimiter;
    private final AuditLogService auditLogService;

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       UserProfileRepository userProfileRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider tokenProvider,
                       TokenRevocationService tokenRevocationService,
                       LoginRateLimiter rateLimiter,
                       AuditLogService auditLogService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userProfileRepository = userProfileRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.tokenRevocationService = tokenRevocationService;
        this.rateLimiter = rateLimiter;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        validatePasswordStrength(request.getPassword());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered: " + request.getEmail());
        }

        UserEntity user = new UserEntity(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getFirstName(),
                request.getLastName(),
                request.getPhoneNumber()
        );

        RoleEntity customerRole = roleRepository.findById(RoleName.ROLE_CUSTOMER.name())
                .orElseGet(() -> roleRepository.save(new RoleEntity(RoleName.ROLE_CUSTOMER.name(), "Customer Role")));

        user.setRoles(Collections.singleton(customerRole));
        UserEntity savedUser = userRepository.save(user);

        // Create default profile
        UserProfileEntity profile = new UserProfileEntity(savedUser);
        userProfileRepository.save(profile);

        List<String> roles = savedUser.getRoles().stream()
                .map(RoleEntity::getId)
                .collect(Collectors.toList());

        String jwt = tokenProvider.generateToken(savedUser.getId(), savedUser.getEmail(), roles);
        RefreshTokenEntity refreshToken = createRefreshToken(savedUser);

        auditLogService.logSecurityEvent(
                savedUser.getId(),
                "USER_REGISTERED",
                "AUTH",
                savedUser.getId().toString(),
                "127.0.0.1",
                "Web Client",
                "New user account registered successfully"
        );

        return new AuthResponse(
                jwt,
                refreshToken.getToken(),
                86400,
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                roles
        );
    }

    public AuthResponse login(LoginRequest request) {
        return login(request, "127.0.0.1", "Web Client");
    }

    public AuthResponse login(LoginRequest request, String ipAddress, String userAgent) {
        // 1. IP Rate Limiting Check
        if (rateLimiter.isIpBlocked(ipAddress)) {
            throw new org.springframework.security.authentication.LockedException("Too many failed attempts from your IP address. Please wait 15 minutes before retrying.");
        }

        // 2. Account Lockout Check
        UserEntity user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (user != null && user.isAccountLocked()) {
            auditLogService.logSecurityEvent(
                    user.getId(),
                    "LOGIN_BLOCKED_ACCOUNT_LOCKED",
                    "AUTH",
                    user.getEmail(),
                    ipAddress,
                    userAgent,
                    "Login attempt on locked account"
            );
            long minutesRemaining = ChronoUnit.MINUTES.between(Instant.now(), user.getLockedUntil()) + 1;
            throw new org.springframework.security.authentication.LockedException(String.format("Account temporarily locked due to consecutive failed attempts. Try again in %d minutes.", Math.max(1, minutesRemaining)));
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            if (user == null) {
                user = userRepository.findByEmail(request.getEmail())
                        .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));
            }

            // Reset failed login counter upon successful authentication
            user.setFailedLoginAttempts(0);
            user.setLockedUntil(null);
            userRepository.save(user);
            rateLimiter.resetAttempts(ipAddress);

            List<String> roles = user.getRoles().stream()
                    .map(RoleEntity::getId)
                    .collect(Collectors.toList());

            String jwt = tokenProvider.generateToken(user.getId(), user.getEmail(), roles);
            RefreshTokenEntity refreshToken = createRefreshToken(user);

            auditLogService.logSecurityEvent(
                    user.getId(),
                    "LOGIN_SUCCESS",
                    "AUTH",
                    user.getId().toString(),
                    ipAddress,
                    userAgent,
                    "Successful authentication"
            );

            return new AuthResponse(
                    jwt,
                    refreshToken.getToken(),
                    86400,
                    user.getId(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    roles
            );
        } catch (BadCredentialsException ex) {
            // Increment failed attempt counter and evaluate lockout
            if (user != null) {
                user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
                if (user.getFailedLoginAttempts() >= 5) {
                    user.setLockedUntil(Instant.now().plus(15, ChronoUnit.MINUTES));
                    auditLogService.logSecurityEvent(
                            user.getId(),
                            "ACCOUNT_LOCKED",
                            "AUTH",
                            user.getEmail(),
                            ipAddress,
                            userAgent,
                            "Account locked for 15 minutes after 5 consecutive failed attempts"
                    );
                    userRepository.save(user);
                    rateLimiter.recordFailedAttempt(ipAddress);
                    throw new org.springframework.security.authentication.LockedException("Account locked after 5 consecutive failed login attempts. Please retry in 15 minutes.");
                }
                userRepository.save(user);
            }
            rateLimiter.recordFailedAttempt(ipAddress);

            auditLogService.logSecurityEvent(
                    user != null ? user.getId() : null,
                    "LOGIN_FAILED",
                    "AUTH",
                    request.getEmail(),
                    ipAddress,
                    userAgent,
                    "Invalid password entered"
            );

            throw ex;
        }
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshTokenEntity entity = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new BadRequestException("Invalid or non-existent refresh token"));

        if (entity.isRevoked() || entity.isExpired()) {
            throw new BadRequestException("Refresh token is expired or revoked. Please log in again.");
        }

        UserEntity user = entity.getUser();
        if ("DELETED".equalsIgnoreCase(user.getStatus())) {
            throw new BadRequestException("Account is no longer active.");
        }

        // Rotate refresh token
        entity.setRevoked(true);
        refreshTokenRepository.save(entity);

        RefreshTokenEntity newRefreshToken = createRefreshToken(user);

        List<String> roles = user.getRoles().stream()
                .map(RoleEntity::getId)
                .collect(Collectors.toList());

        String newJwt = tokenProvider.generateToken(user.getId(), user.getEmail(), roles);

        auditLogService.logSecurityEvent(
                user.getId(),
                "TOKEN_REFRESH",
                "AUTH",
                user.getId().toString(),
                "127.0.0.1",
                "Web Client",
                "Rotated refresh token and generated new access token"
        );

        return new AuthResponse(
                newJwt,
                newRefreshToken.getToken(),
                86400,
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                roles
        );
    }

    @Transactional
    public void logout(String bearerToken, UUID userId) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken = bearerToken.substring(7);
        }
        if (bearerToken != null && !bearerToken.isBlank()) {
            tokenRevocationService.revokeToken(bearerToken, Instant.now().plusSeconds(86400));
        }
        if (userId != null) {
            refreshTokenRepository.revokeAllForUser(userId);
            auditLogService.logSecurityEvent(
                    userId,
                    "LOGOUT",
                    "AUTH",
                    userId.toString(),
                    "127.0.0.1",
                    "Web Client",
                    "Session invalidated and tokens revoked"
            );
        }
    }

    private RefreshTokenEntity createRefreshToken(UserEntity user) {
        String tokenString = UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString();
        Instant expiry = Instant.now().plus(7, ChronoUnit.DAYS);
        RefreshTokenEntity refreshToken = new RefreshTokenEntity(user, tokenString, expiry);
        return refreshTokenRepository.save(refreshToken);
    }

    private void validatePasswordStrength(String password) {
        if (password == null || password.length() < 8) {
            throw new BadRequestException("Password must be at least 8 characters long.");
        }
        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);

        if (!hasUpper || !hasLower || !hasDigit) {
            throw new BadRequestException("Password must contain at least one uppercase letter, one lowercase letter, and one number.");
        }
    }

    @Transactional(readOnly = true)
    public UserDto getCurrentUser(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        List<String> roles = user.getRoles().stream()
                .map(RoleEntity::getId)
                .collect(Collectors.toList());

        UserDto dto = new UserDto(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                user.getStatus(),
                user.isMfaEnabled(),
                roles
        );

        userProfileRepository.findByUserId(userId).ifPresent(profile -> {
            dto.setProfile(mapToProfileDto(profile));
        });

        return dto;
    }

    @Transactional
    public UserProfileDto updateUserProfile(UUID userId, UserProfileDto dto) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        UserProfileEntity profile = userProfileRepository.findByUserId(userId)
                .orElseGet(() -> new UserProfileEntity(user));

        if (dto.getDateOfBirth() != null) profile.setDateOfBirth(dto.getDateOfBirth());
        if (dto.getEmploymentStatus() != null) profile.setEmploymentStatus(dto.getEmploymentStatus());
        if (dto.getAnnualIncome() != null) profile.setAnnualIncome(dto.getAnnualIncome());
        if (dto.getRiskTolerance() != null) profile.setRiskTolerance(dto.getRiskTolerance());
        if (dto.getAddressLine1() != null) profile.setAddressLine1(dto.getAddressLine1());
        if (dto.getCity() != null) profile.setCity(dto.getCity());
        if (dto.getStateProvince() != null) profile.setStateProvince(dto.getStateProvince());
        if (dto.getPostalCode() != null) profile.setPostalCode(dto.getPostalCode());
        if (dto.getCountryCode() != null) profile.setCountryCode(dto.getCountryCode());
        if (dto.getAge() != null) profile.setAge(dto.getAge());
        if (dto.getEmploymentCategory() != null) profile.setEmploymentCategory(dto.getEmploymentCategory());
        if (dto.getOccupation() != null) profile.setOccupation(dto.getOccupation());
        if (dto.getMonthlyIncome() != null) profile.setMonthlyIncome(dto.getMonthlyIncome());
        if (dto.getMonthlyExpenses() != null) profile.setMonthlyExpenses(dto.getMonthlyExpenses());
        if (dto.getExistingObligations() != null) profile.setExistingObligations(dto.getExistingObligations());
        if (dto.getApproximateSavings() != null) profile.setApproximateSavings(dto.getApproximateSavings());
        if (dto.getFinancialGoals() != null && !dto.getFinancialGoals().isEmpty()) profile.setFinancialGoals(dto.getFinancialGoals());
        if (dto.getPreferredAssistance() != null) profile.setPreferredAssistance(dto.getPreferredAssistance());
        if (dto.getFinancialKnowledge() != null) profile.setFinancialKnowledge(dto.getFinancialKnowledge());
        if (dto.getCommunicationStyle() != null) profile.setCommunicationStyle(dto.getCommunicationStyle());
        if (dto.getShortTermGoal() != null) profile.setShortTermGoal(dto.getShortTermGoal());
        if (dto.getLongTermGoal() != null) profile.setLongTermGoal(dto.getLongTermGoal());
        if (dto.getOnboardingStep() > 0) profile.setOnboardingStep(dto.getOnboardingStep());
        if (dto.isOnboardingCompleted()) profile.setOnboardingCompleted(true);

        UserProfileEntity saved = userProfileRepository.save(profile);
        return mapToProfileDto(saved);
    }

    public UserProfileDto mapToProfileDto(UserProfileEntity profile) {
        UserProfileDto pDto = new UserProfileDto();
        pDto.setDateOfBirth(profile.getDateOfBirth());
        pDto.setEmploymentStatus(profile.getEmploymentStatus());
        pDto.setAnnualIncome(profile.getAnnualIncome());
        pDto.setRiskTolerance(profile.getRiskTolerance());
        pDto.setAddressLine1(profile.getAddressLine1());
        pDto.setCity(profile.getCity());
        pDto.setStateProvince(profile.getStateProvince());
        pDto.setPostalCode(profile.getPostalCode());
        pDto.setCountryCode(profile.getCountryCode());
        pDto.setAge(profile.getAge());
        pDto.setEmploymentCategory(profile.getEmploymentCategory());
        pDto.setOccupation(profile.getOccupation());
        pDto.setMonthlyIncome(profile.getMonthlyIncome());
        pDto.setMonthlyExpenses(profile.getMonthlyExpenses());
        pDto.setExistingObligations(profile.getExistingObligations());
        pDto.setApproximateSavings(profile.getApproximateSavings());
        pDto.setFinancialGoals(profile.getFinancialGoals());
        pDto.setPreferredAssistance(profile.getPreferredAssistance());
        pDto.setFinancialKnowledge(profile.getFinancialKnowledge());
        pDto.setCommunicationStyle(profile.getCommunicationStyle());
        pDto.setShortTermGoal(profile.getShortTermGoal());
        pDto.setLongTermGoal(profile.getLongTermGoal());
        pDto.setOnboardingCompleted(profile.isOnboardingCompleted());
        pDto.setOnboardingStep(profile.getOnboardingStep());
        return pDto;
    }
}

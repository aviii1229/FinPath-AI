package com.finpath.security.service;

import com.finpath.core.exception.BadRequestException;
import com.finpath.core.exception.ResourceNotFoundException;
import com.finpath.security.dto.AuthResponse;
import com.finpath.security.dto.LoginRequest;
import com.finpath.security.dto.RegisterRequest;
import com.finpath.security.dto.UserDto;
import com.finpath.security.dto.UserProfileDto;
import com.finpath.security.entity.RoleEntity;
import com.finpath.security.entity.UserEntity;
import com.finpath.security.entity.UserProfileEntity;
import com.finpath.security.jwt.JwtTokenProvider;
import com.finpath.security.model.RoleName;
import com.finpath.security.repository.RoleRepository;
import com.finpath.security.repository.UserProfileRepository;
import com.finpath.security.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       UserProfileRepository userProfileRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userProfileRepository = userProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
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

        return new AuthResponse(
                jwt,
                86400,
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                roles
        );
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));

        List<String> roles = user.getRoles().stream()
                .map(RoleEntity::getId)
                .collect(Collectors.toList());

        String jwt = tokenProvider.generateToken(user.getId(), user.getEmail(), roles);

        return new AuthResponse(
                jwt,
                86400,
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                roles
        );
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


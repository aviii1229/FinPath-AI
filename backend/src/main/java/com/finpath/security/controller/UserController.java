package com.finpath.security.controller;

import com.finpath.core.dto.ApiResponse;
import com.finpath.security.dto.UserDto;
import com.finpath.security.dto.UserProfileDto;
import com.finpath.security.model.UserPrincipal;
import com.finpath.security.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "User profile and settings endpoints")
public class UserController {

    private final AuthService authService;

    public UserController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user profile")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser(@AuthenticationPrincipal UserPrincipal currentUser) {
        UserDto userDto = authService.getCurrentUser(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(userDto));
    }

    @PutMapping("/me/profile")
    @Operation(summary = "Update authenticated user profile information")
    public ResponseEntity<ApiResponse<UserProfileDto>> updateProfile(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestBody UserProfileDto profileDto) {
        UserProfileDto updated = authService.updateUserProfile(currentUser.getId(), profileDto);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updated));
    }
}


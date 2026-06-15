package com.codemarket.dto;

import com.codemarket.entity.Role;
import jakarta.validation.constraints.*;

public final class AuthDtos {
    private AuthDtos() {}

    public record RegisterRequest(@NotBlank String firstName, @NotBlank String lastName, @Email @NotBlank String email,
                                  @Size(min = 6) String password, String phone, Role role) {}
    public record LoginRequest(@Email @NotBlank String email, @NotBlank String password) {}
    public record UpdateProfileRequest(@NotBlank String firstName, @NotBlank String lastName, String phone, String gender, String profileImage) {}
    public record ChangePasswordRequest(@NotBlank String currentPassword, @Size(min = 6) String newPassword) {}
    public record AuthResponse(String token, UserResponse user) {}
    public record UserResponse(Long id, String firstName, String lastName, String email, String phone, Role role, String gender, String profileImage) {}

    public record AuthResponse(String token, UserResponse user) {}
    public record UserResponse(Long id, String firstName, String lastName, String email, String phone, Role role, String profileImage) {}

}

package com.codemarket.controller;

import com.codemarket.dto.AuthDtos.*;
import com.codemarket.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/me")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public UserResponse profile(Authentication authentication) {
        return userService.profile(authentication.getName());
    }

    @PutMapping
    public UserResponse updateProfile(@Valid @RequestBody UpdateProfileRequest request, Authentication authentication) {
        return userService.updateProfile(authentication.getName(), request);
    }

    @PutMapping("/password")
    public void changePassword(@Valid @RequestBody ChangePasswordRequest request, Authentication authentication) {
        userService.changePassword(authentication.getName(), request);
    }
}

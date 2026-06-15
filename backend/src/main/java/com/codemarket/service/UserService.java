package com.codemarket.service;

import com.codemarket.dto.AuthDtos.*;
import com.codemarket.entity.Role;
import com.codemarket.entity.User;
import com.codemarket.exception.ApiException;
import com.codemarket.repository.UserRepository;
import com.codemarket.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ApiException(HttpStatus.CONFLICT, "Email is already registered");
        }
        User user = User.builder()
                .firstName(request.firstName()).lastName(request.lastName()).email(request.email())
                .password(passwordEncoder.encode(request.password())).phone(request.phone())
                .role(request.role() == null ? Role.BUYER : request.role()).enabled(true).build();
        userRepository.save(user);
        return new AuthResponse(jwtUtil.generateToken(user.getEmail()), toResponse(user));
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        User user = findByEmail(request.email());
        return new AuthResponse(jwtUtil.generateToken(user.getEmail()), toResponse(user));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public UserResponse profile(String email) {
        return toResponse(findByEmail(email));
    }

    public UserResponse updateProfile(String email, UpdateProfileRequest request) {
        User user = findByEmail(email);
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPhone(request.phone());
        user.setGender(request.gender());
        user.setProfileImage(request.profileImage());
        return toResponse(userRepository.save(user));
    }

    public void changePassword(String email, ChangePasswordRequest request) {
        User user = findByEmail(email);
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                .password(user.getPassword()).disabled(!user.isEnabled()).roles(user.getRole().name()).build();
    }

    public static UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhone(), user.getRole(), user.getGender(), user.getProfileImage());
    }
}

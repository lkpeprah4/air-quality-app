package com.ghanaairwatch.controller;

import com.ghanaairwatch.dto.AuthResponse;
import com.ghanaairwatch.dto.LoginRequest;
import com.ghanaairwatch.dto.RegisterRequest;
import com.ghanaairwatch.entity.User;
import com.ghanaairwatch.repository.UserRepository;
import com.ghanaairwatch.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

// POST /api/auth/register and POST /api/auth/login.
// Both return a JWT token the client stores and sends on protected calls.
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        if (request.username() == null || request.username().isBlank()
                || request.email() == null || request.email().isBlank()
                || request.password() == null || request.password().length() < 8) {
            throw new IllegalArgumentException("Username and email are required and the password must be at least 8 characters.");
        }
        if (userRepository.existsByUsername(request.username().trim())) {
            throw new IllegalArgumentException("That username is already taken.");
        }
        if (userRepository.existsByEmail(request.email().trim())) {
            throw new IllegalArgumentException("That email is already registered.");
        }

        User user = new User(
                request.username().trim(),
                request.email().trim().toLowerCase(),
                passwordEncoder.encode(request.password()),
                Instant.now()
        );
        userRepository.save(user);

        return new AuthResponse(jwtService.generateToken(user), user.getId(), user.getUsername(), user.getEmail());
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password."));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid username or password.");
        }

        return new AuthResponse(jwtService.generateToken(user), user.getId(), user.getUsername(), user.getEmail());
    }
}

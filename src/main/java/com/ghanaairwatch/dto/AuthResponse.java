package com.ghanaairwatch.dto;

// What you get back after registering or logging in. The client keeps the
// token and sends it as "Authorization: Bearer <token>" on protected calls.
public record AuthResponse(
        String token,
        Long userId,
        String username,
        String email
) {}

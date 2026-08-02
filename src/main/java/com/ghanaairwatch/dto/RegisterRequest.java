package com.ghanaairwatch.dto;

// Registration request body.
public record RegisterRequest(
        String username,
        String email,
        String password
) {}

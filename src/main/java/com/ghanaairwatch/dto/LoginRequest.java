package com.ghanaairwatch.dto;

// Login request body.
public record LoginRequest(
        String username,
        String password
) {}

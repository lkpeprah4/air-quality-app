package com.ghanaairwatch.dto;

public record HealthAnalyticsResponse(
        String riskLevel,      // e.g. "Good", "Moderate", "Unhealthy"
        String colorHex,       // matches the frontend's color bands
        String advice,         // plain-language guidance
        String healthProfile   // which profile this advice was tailored for
) {}

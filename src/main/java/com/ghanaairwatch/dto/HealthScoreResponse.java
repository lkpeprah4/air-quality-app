package com.ghanaairwatch.dto;

import java.util.List;

// Daily Health Score: today's air quality + your profile reduced to one
// memorable number out of 100 (higher = better).
public record HealthScoreResponse(
        int score,
        String riskLevel,
        String colorHex,
        double aqi,
        List<String> recommendations
) {}

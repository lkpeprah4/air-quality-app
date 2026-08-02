package com.ghanaairwatch.dto;

import java.util.List;

// Output of the Health Risk Engine: a Low/Moderate/High/Very High verdict,
// a 0-100 risk score, and the plain-language reasons + advice behind it.
public record HealthRiskResponse(
        String riskLevel,
        String colorHex,
        int riskScore,
        List<String> recommendations,
        List<String> reasoning
) {}

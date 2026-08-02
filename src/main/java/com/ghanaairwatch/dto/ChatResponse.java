package com.ghanaairwatch.dto;

// The rule-based AI chat assistant's answer. It replies in plain language,
// and we also echo back the AQI + risk band that the answer was based on.
public record ChatResponse(
        String reply,
        int aqi,
        String riskLevel
) {}

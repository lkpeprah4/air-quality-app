package com.ghanaairwatch.dto;

// The health profile a user fills in. This drives the Health Risk Engine,
// the Daily Health Score and the Exposure Calculator.
// outdoorActivity: "low" | "medium" | "high"
// smoking:        "none" | "former" | "current"
public record HealthProfile(
        int age,
        String gender,
        boolean asthma,
        boolean heartDisease,
        boolean pregnancy,
        String outdoorActivity,
        String smoking
) {}

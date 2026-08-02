package com.ghanaairwatch.dto;

// Exposure Calculator: "I stayed outside for 6 hours" -> how much pollution
// did that actually cost you. exposureIndex = AQI x hours x activity factor.
public record ExposureResponse(
        double hoursOutdoors,
        double aqi,
        double exposureIndex,
        String riskCategory,
        String guidance
) {}

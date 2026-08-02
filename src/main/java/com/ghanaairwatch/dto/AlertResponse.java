package com.ghanaairwatch.dto;

// Alerts check: flags when air quality crosses the danger threshold (AQI > 150)
// so the frontend can pop a notification banner or trigger a push.
public record AlertResponse(
        long locationId,
        int aqi,
        boolean isAlert,
        String level,
        String message
) {}

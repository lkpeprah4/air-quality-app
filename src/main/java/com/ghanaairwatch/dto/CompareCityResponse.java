package com.ghanaairwatch.dto;

// One city's row in the Compare Cities screen.
public record CompareCityResponse(
        long locationId,
        String name,
        int aqi,
        double pm25,
        double pm10,
        double temperature,
        int humidity,
        long timestamp
) {}

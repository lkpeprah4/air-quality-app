package com.ghanaairwatch.dto;

// DTO = "Data Transfer Object" -- a plain shape of data sent over the API.
// This is what the dashboard gets in one shot: the pollution numbers AND the
// current weather, so the frontend only needs to call /api/air-quality.
// New fields (so2, weather) are additive, so older clients keep working.
public record AirQualityResponse(
        int aqi,
        double pm25,
        double pm10,
        double o3,
        double no2,
        double so2,
        double co,
        double temperature,
        int humidity,
        double windSpeed,
        int windDirection,
        double pressure,
        double rain,
        String weatherDescription,
        long timestamp
) {}

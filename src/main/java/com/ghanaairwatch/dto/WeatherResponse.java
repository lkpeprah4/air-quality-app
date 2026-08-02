package com.ghanaairwatch.dto;

// The weather half of the dashboard: temperature (°C), humidity, wind,
// pressure and rain come from the OpenWeatherMap /weather endpoint.
public record WeatherResponse(
        double temperature,
        int humidity,
        double windSpeed,
        int windDirection,
        double pressure,
        double rain,
        String description,
        long timestamp
) {}

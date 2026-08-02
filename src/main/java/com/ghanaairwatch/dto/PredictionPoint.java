package com.ghanaairwatch.dto;

// One point on the forecast chart: a timestamp and the predicted AQI.
public record PredictionPoint(
        long timestamp,
        double aqi
) {}

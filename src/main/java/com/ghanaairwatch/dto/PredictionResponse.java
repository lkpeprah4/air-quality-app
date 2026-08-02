package com.ghanaairwatch.dto;

import java.util.List;

// AQI Prediction output. "tomorrow" is +24 hours, "sixHours" is +6 hours and
// "oneHour" is +1 hour, all forecast with simple linear regression on the
// readings stored in the database.
public record PredictionResponse(
        int currentAqi,
        double oneHour,
        double sixHours,
        double tomorrow,
        List<PredictionPoint> series,
        String model,
        String note
) {}

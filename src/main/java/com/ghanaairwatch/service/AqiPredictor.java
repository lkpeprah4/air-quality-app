package com.ghanaairwatch.service;

import com.ghanaairwatch.dto.PredictionPoint;
import com.ghanaairwatch.dto.PredictionResponse;
import com.ghanaairwatch.entity.AirQualityReading;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// Predicts future AQI from the readings stored in the database using simple
// linear regression (least squares): fit a straight line through past AQI
// over time, then extend that line 1 hour, 6 hours and 24 hours ahead.
// It's deliberately simple -- no extra dependencies -- and plenty for the
// "1 hour later / 6 hours later / tomorrow" forecast the spec asks for.
@Service
public class AqiPredictor {

    private static final double HOUR_MS = 3_600_000.0;
    private static final int HOURS_OF_SERIES = 24;

    public PredictionResponse predict(List<AirQualityReading> history, int currentAqi) {
        List<AirQualityReading> sorted = history.stream()
                .filter(r -> r.getRecordedAt() != null && r.getAqi() != null)
                .sorted(Comparator.comparing(AirQualityReading::getRecordedAt))
                .toList();

        String note = "";
        double oneHour = currentAqi;
        double sixHours = currentAqi;
        double tomorrow = currentAqi;
        List<PredictionPoint> series = new ArrayList<>();

        if (sorted.size() < 2) {
            note = "Not enough stored history to forecast yet — the prediction falls back to the current AQI. "
                    + "Keep checking this city and the model will get smarter.";
            long now = Instant.now().toEpochMilli();
            for (int h = 0; h <= HOURS_OF_SERIES; h++) {
                series.add(new PredictionPoint(now + h * (long) HOUR_MS, currentAqi));
            }
            return new PredictionResponse(currentAqi, oneHour, sixHours, tomorrow, series, "Linear Regression", note);
        }

        double firstTime = sorted.get(0).getRecordedAt().toEpochMilli();
        double now = Instant.now().toEpochMilli();

        // x = hours since the first reading, y = AQI
        List<double[]> points = new ArrayList<>();
        for (AirQualityReading r : sorted) {
            double x = (r.getRecordedAt().toEpochMilli() - firstTime) / HOUR_MS;
            double y = r.getAqi();
            points.add(new double[]{x, y});
        }

        double currentX = (now - firstTime) / HOUR_MS;
        double slope;
        double intercept;
        try {
            double meanX = points.stream().mapToDouble(p -> p[0]).average().orElse(0);
            double meanY = points.stream().mapToDouble(p -> p[1]).average().orElse(0);
            double num = 0, den = 0;
            for (double[] p : points) {
                num += (p[0] - meanX) * (p[1] - meanY);
                den += (p[0] - meanX) * (p[0] - meanX);
            }
            if (den == 0) throw new ArithmeticException("flat history");
            slope = num / den;
            intercept = meanY - slope * meanX;
        } catch (ArithmeticException e) {
            slope = 0;
            intercept = currentAqi;
        }

        oneHour = clamp(predictAt(slope, intercept, currentX + 1));
        sixHours = clamp(predictAt(slope, intercept, currentX + 6));
        tomorrow = clamp(predictAt(slope, intercept, currentX + 24));

        for (int h = 0; h <= HOURS_OF_SERIES; h++) {
            series.add(new PredictionPoint((long) (now + h * HOUR_MS), clamp(predictAt(slope, intercept, currentX + h))));
        }

        note = "Linear regression fitted over " + points.size() + " stored readings for this city.";
        return new PredictionResponse(currentAqi, oneHour, sixHours, tomorrow, series, "Linear Regression", note);
    }

    private double predictAt(double slope, double intercept, double x) {
        return slope * x + intercept;
    }

    // AQI can't go below 0 or above 500.
    private double clamp(double value) {
        return Math.round(Math.max(0, Math.min(500, value)) * 10.0) / 10.0;
    }
}

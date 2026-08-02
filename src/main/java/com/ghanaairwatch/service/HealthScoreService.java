package com.ghanaairwatch.service;

import com.ghanaairwatch.dto.HealthProfile;
import com.ghanaairwatch.dto.HealthRiskResponse;
import com.ghanaairwatch.dto.HealthScoreResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

// Daily Health Score: reduces today's air quality + the user's profile to a
// single number out of 100 (higher = better), reusing the Health Risk Engine
// for the recommendations and verdict.
@Service
public class HealthScoreService {

    private final HealthRiskEngine riskEngine;

    public HealthScoreService(HealthRiskEngine riskEngine) {
        this.riskEngine = riskEngine;
    }

    public HealthScoreResponse score(int aqi, HealthProfile p) {
        int score = 100;

        // The worse the air, the bigger the penalty (up to 50 points).
        score -= Math.min(50, aqi / 5);

        if (p != null) {
            if (p.asthma()) score -= 10;
            if (p.heartDisease()) score -= 10;
            if (p.pregnancy()) score -= 8;
            if (p.smoking() != null && p.smoking().equals("current")) score -= 10;
            if (p.age() >= 65) score -= 8;
            if (p.age() <= 5) score -= 8;
            if (p.outdoorActivity() != null && p.outdoorActivity().equals("high")) score -= 4;
        }

        int clamped = Math.max(0, Math.min(100, score));

        HealthRiskResponse risk = riskEngine.analyze(aqi, p);
        List<String> recommendations = new ArrayList<>(risk.recommendations());
        recommendations.add("Drink more water.");
        recommendations.add("Check today's AQI again before planning outdoor time.");

        String color = clamped >= 70 ? "#4FD8A8" : clamped >= 50 ? "#F0D23C" : clamped >= 30 ? "#EF6F5B" : "#C94F4F";

        return new HealthScoreResponse(clamped, risk.riskLevel(), color, aqi, recommendations);
    }
}

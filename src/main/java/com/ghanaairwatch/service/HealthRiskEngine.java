package com.ghanaairwatch.service;

import com.ghanaairwatch.dto.HealthProfile;
import com.ghanaairwatch.dto.HealthRiskResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

// The full Health Risk Engine: takes today's AQI plus the user's personal
// details (age, asthma, heart disease, pregnancy, outdoor activity, smoking)
// and turns them into a Low / Moderate / High / Very High verdict plus the
// specific safety recommendations for THAT person.
@Service
public class HealthRiskEngine {

    public HealthRiskResponse analyze(int aqi, HealthProfile p) {
        Set<String> recommendations = new LinkedHashSet<>();
        List<String> reasoning = new ArrayList<>();

        // Base risk from the air itself (0-50 of the score).
        int riskScore = Math.min(50, aqi / 3);

        if (aqi >= 150) {
            riskScore += 20;
            recommendations.add("Avoid outdoor exercise.");
            recommendations.add("Wear an N95 mask when going outside.");
            recommendations.add("Keep windows and doors closed.");
            recommendations.add("Stay indoors as much as possible.");
        } else if (aqi >= 100) {
            riskScore += 10;
            recommendations.add("Reduce prolonged or heavy outdoor exertion.");
            recommendations.add("Keep outdoor sessions short, especially near roads.");
        } else if (aqi >= 50) {
            riskScore += 5;
            recommendations.add("Air is acceptable for most people — watch for symptoms if you are sensitive.");
        }

        if (p != null) {
            if (p.asthma()) {
                riskScore += 15;
                reasoning.add("Asthma");
                recommendations.add("Keep your reliever inhaler nearby.");
                recommendations.add("Avoid outdoor activities when AQI is high.");
            }
            if (p.heartDisease()) {
                riskScore += 15;
                reasoning.add("Heart disease");
                recommendations.add("Avoid strenuous exertion.");
                recommendations.add("Keep any prescribed medication accessible.");
            }
            if (p.pregnancy()) {
                riskScore += 12;
                reasoning.add("Pregnancy");
                recommendations.add("Avoid strenuous outdoor activity and rest indoors.");
            }
            if (p.smoking() != null && p.smoking().equals("current")) {
                riskScore += 12;
                reasoning.add("Current smoker");
                recommendations.add("Smoking worsens the effects of air pollution — consider quitting.");
            }
            if (p.age() >= 65) {
                riskScore += 8;
                reasoning.add("Age 65 or over");
                recommendations.add("Limit time outdoors and rest during peak haze hours.");
            }
            if (p.age() <= 5) {
                riskScore += 8;
                reasoning.add("Young child");
                recommendations.add("Keep young children indoors and avoid busy roads.");
            }
            if (p.outdoorActivity() != null && p.outdoorActivity().equals("high")) {
                riskScore += 8;
                reasoning.add("High outdoor activity");
                recommendations.add("Shorten outdoor sessions during peak pollution hours.");
            }
        }

        int clamped = Math.max(0, Math.min(100, riskScore));
        String level = levelFor(clamped);
        String color = colorFor(level);

        if (reasoning.isEmpty()) {
            reasoning.add("No personal risk factors entered.");
        }

        return new HealthRiskResponse(level, color, clamped, List.copyOf(recommendations), reasoning);
    }

    private String levelFor(int score) {
        if (score < 25) return "Low";
        if (score < 50) return "Moderate";
        if (score < 75) return "High";
        return "Very High";
    }

    private String colorFor(String level) {
        return switch (level) {
            case "Low" -> "#4FD8A8";
            case "Moderate" -> "#F0D23C";
            case "High" -> "#EF6F5B";
            default -> "#C94F4F";
        };
    }
}

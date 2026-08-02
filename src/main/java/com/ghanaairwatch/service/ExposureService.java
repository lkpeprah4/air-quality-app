package com.ghanaairwatch.service;

import com.ghanaairwatch.dto.ExposureResponse;
import org.springframework.stereotype.Service;

// Exposure Calculator: "I stayed outside for 6 hours" -> an exposure index
// = AQI x hours x an activity factor (jogging pulls more polluted air than
// sitting still), mapped to a Low/Moderate/High/Very High verdict.
@Service
public class ExposureService {

    public ExposureResponse calculate(double aqi, double hours, String activity) {
        double factor = switch (activity == null ? "walking" : activity.toLowerCase()) {
            case "resting" -> 0.5;
            case "jogging", "running" -> 1.6;
            case "sports" -> 2.0;
            default -> 1.0; // walking, or any unknown activity
        };

        double index = aqi * hours * factor;
        double rounded = Math.round(index * 10.0) / 10.0;

        String category;
        String guidance;
        if (rounded < 200) {
            category = "Low";
            guidance = "Exposure is low. Normal outdoor activity is fine, but take it easy if you feel symptoms.";
        } else if (rounded < 400) {
            category = "Moderate";
            guidance = "Moderate exposure. Take breaks indoors and watch for coughing or throat irritation.";
        } else if (rounded < 700) {
            category = "High";
            guidance = "High exposure. Reduce time outdoors, wear an N95 mask and keep activity light.";
        } else {
            category = "Very High";
            guidance = "Very high exposure. Stay indoors, keep windows closed and get to fresh air if you feel unwell.";
        }

        return new ExposureResponse(hours, aqi, rounded, category, guidance);
    }
}

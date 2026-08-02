package com.ghanaairwatch.service;

import com.ghanaairwatch.dto.HealthAnalyticsResponse;
import org.springframework.stereotype.Service;

import java.util.Map;

// This is the "give health analytics" part of the app -- it turns a raw AQI
// number into plain-language guidance tailored to a health profile.
// Mirrors the logic already in the frontend's aqiHelpers.js, but living here
// means it can't be tampered with in the browser, and any client (web, future
// mobile app, etc.) gets the same answer from one source of truth.
@Service
public class HealthRiskCalculator {

    private record Band(String label, String colorHex, int max) {}

    private static final Band[] BANDS = {
            new Band("Good", "#4FD8A8", 50),
            new Band("Moderate", "#F0D23C", 100),
            new Band("Unhealthy for sensitive groups", "#F0A23C", 150),
            new Band("Unhealthy", "#EF6F5B", 200),
            new Band("Very unhealthy", "#C94F4F", Integer.MAX_VALUE),
    };

    private static final Map<String, Map<String, String>> ADVICE = Map.of(
            "general", Map.of(
                    "Good", "Air is fine for any outdoor activity today.",
                    "Moderate", "Fine for most people. Watch for symptoms if unusually sensitive.",
                    "Unhealthy for sensitive groups", "General public unaffected. Keep outdoor sessions shorter during peak hours.",
                    "Unhealthy", "Reduce prolonged or heavy outdoor exertion.",
                    "Very unhealthy", "Avoid outdoor exertion. Keep windows closed."
            ),
            "asthma", Map.of(
                    "Good", "No restrictions expected.",
                    "Moderate", "Keep a reliever inhaler on hand if outdoors for long periods.",
                    "Unhealthy for sensitive groups", "Limit outdoor exertion, watch for chest tightness.",
                    "Unhealthy", "Stay indoors. Avoid outdoor exercise today.",
                    "Very unhealthy", "Remain indoors with windows closed. Seek care if symptoms appear."
            ),
            "child", Map.of(
                    "Good", "Safe for normal outdoor play.",
                    "Moderate", "Fine for play, avoid long periods near busy roads.",
                    "Unhealthy for sensitive groups", "Shorten outdoor play, especially near traffic.",
                    "Unhealthy", "Keep play brief and supervised, or move indoors.",
                    "Very unhealthy", "Keep children indoors for the day."
            ),
            "elderly", Map.of(
                    "Good", "No restrictions expected.",
                    "Moderate", "Fine for light activity such as walking.",
                    "Unhealthy for sensitive groups", "Limit time outdoors, particularly with heart or lung conditions.",
                    "Unhealthy", "Avoid outdoor exertion. Rest indoors during peak haze hours.",
                    "Very unhealthy", "Stay indoors. Contact a doctor if breathless or dizzy."
            )
    );

    public HealthAnalyticsResponse analyze(int aqi, String profileId) {
        Band band = bandFor(aqi);
        String safeProfile = ADVICE.containsKey(profileId) ? profileId : "general";
        String advice = ADVICE.get(safeProfile).get(band.label());
        return new HealthAnalyticsResponse(band.label(), band.colorHex(), advice, safeProfile);
    }

    // Public helpers so other features (chat, heatmap) reuse the same bands
    // instead of duplicating the thresholds.
    public String levelFor(int aqi) {
        return bandFor(aqi).label();
    }

    public String colorFor(int aqi) {
        return bandFor(aqi).colorHex();
    }

    private Band bandFor(int aqi) {
        for (Band b : BANDS) {
            if (aqi <= b.max()) return b;
        }
        return BANDS[BANDS.length - 1];
    }
}

package com.ghanaairwatch.controller;

import com.ghanaairwatch.dto.AirQualityResponse;
import com.ghanaairwatch.dto.HealthAnalyticsResponse;
import com.ghanaairwatch.entity.Location;
import com.ghanaairwatch.repository.LocationRepository;
import com.ghanaairwatch.service.AirQualityService;
import com.ghanaairwatch.service.HealthRiskCalculator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// GET /api/health-analytics?locationId=1&profile=asthma
// Combines a live reading with the health-risk rules to answer:
// "given this air quality and this person's health profile, what should they do?"
@RestController
@RequestMapping("/api/health-analytics")
public class HealthAnalyticsController {

    private final AirQualityService airQualityService;
    private final HealthRiskCalculator healthRiskCalculator;
    private final LocationRepository locationRepository;

    public HealthAnalyticsController(AirQualityService airQualityService,
                                      HealthRiskCalculator healthRiskCalculator,
                                      LocationRepository locationRepository) {
        this.airQualityService = airQualityService;
        this.healthRiskCalculator = healthRiskCalculator;
        this.locationRepository = locationRepository;
    }

    @GetMapping
    public HealthAnalyticsResponse getAnalytics(
            @RequestParam Long locationId,
            @RequestParam(defaultValue = "general") String profile
    ) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown location id: " + locationId));

        AirQualityResponse reading = airQualityService.fetchCurrent(location.getLat(), location.getLon());
        return healthRiskCalculator.analyze(reading.aqi(), profile);
    }
}

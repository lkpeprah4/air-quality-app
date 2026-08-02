package com.ghanaairwatch.service;

import com.ghanaairwatch.dto.AirQualityResponse;
import com.ghanaairwatch.dto.AlertResponse;
import com.ghanaairwatch.entity.Location;
import com.ghanaairwatch.repository.LocationRepository;
import org.springframework.stereotype.Service;

// Alerts check: returns whether air quality is at a level that should trigger
// a notification (AQI > 150 = "dangerous", the spec's alert threshold).
@Service
public class AlertService {

    private final LocationRepository locationRepository;
    private final AirQualityService airQualityService;
    private final HealthRiskCalculator bands;

    public AlertService(LocationRepository locationRepository,
                        AirQualityService airQualityService,
                        HealthRiskCalculator bands) {
        this.locationRepository = locationRepository;
        this.airQualityService = airQualityService;
        this.bands = bands;
    }

    public AlertResponse check(Long locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown location id: " + locationId));

        AirQualityResponse r = airQualityService.fetchCurrent(location.getLat(), location.getLon());
        int aqi = r.aqi();
        boolean isAlert = aqi > 150;

        String level;
        String message;
        if (aqi > 300) {
            level = "Hazardous";
            message = "Hazardous air quality (AQI " + aqi + ")! Remain indoors, keep windows closed and avoid all exertion.";
        } else if (aqi > 200) {
            level = "Very unhealthy";
            message = "Dangerous air quality (AQI " + aqi + ")! Avoid outdoor exercise and keep windows closed.";
        } else if (aqi > 150) {
            level = "Unhealthy";
            message = "Dangerous air quality (AQI " + aqi + "). Avoid outdoor exercise, wear an N95 mask outside.";
        } else if (aqi > 100) {
            level = "Unhealthy for sensitive groups";
            message = "Air quality is poor (AQI " + aqi + "). Sensitive groups should limit outdoor activity.";
        } else {
            level = bands.levelFor(aqi);
            message = "Air quality is acceptable (AQI " + aqi + "). No alert.";
        }

        return new AlertResponse(location.getId(), aqi, isAlert, level, message);
    }
}

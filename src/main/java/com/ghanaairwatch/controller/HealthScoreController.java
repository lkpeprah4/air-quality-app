package com.ghanaairwatch.controller;

import com.ghanaairwatch.dto.AirQualityResponse;
import com.ghanaairwatch.dto.HealthProfile;
import com.ghanaairwatch.dto.HealthScoreResponse;
import com.ghanaairwatch.entity.Location;
import com.ghanaairwatch.repository.LocationRepository;
import com.ghanaairwatch.service.AirQualityService;
import com.ghanaairwatch.service.HealthScoreService;
import com.ghanaairwatch.service.HistoryService;
import org.springframework.web.bind.annotation.*;

// POST /api/health-score?locationId=1
// Body: same HealthProfile as /api/health-risk.
// Returns today's score out of 100 (higher = better) plus advice.
@RestController
@RequestMapping("/api/health-score")
public class HealthScoreController {

    private final AirQualityService airQualityService;
    private final HistoryService historyService;
    private final HealthScoreService healthScoreService;
    private final LocationRepository locationRepository;

    public HealthScoreController(AirQualityService airQualityService,
                                 HistoryService historyService,
                                 HealthScoreService healthScoreService,
                                 LocationRepository locationRepository) {
        this.airQualityService = airQualityService;
        this.historyService = historyService;
        this.healthScoreService = healthScoreService;
        this.locationRepository = locationRepository;
    }

    @PostMapping
    public HealthScoreResponse score(@RequestParam Long locationId, @RequestBody HealthProfile profile) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown location id: " + locationId));

        AirQualityResponse reading = airQualityService.fetchCurrent(location.getLat(), location.getLon());
        historyService.recordReading(location, reading);

        return healthScoreService.score(reading.aqi(), profile);
    }
}

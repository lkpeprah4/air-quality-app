package com.ghanaairwatch.controller;

import com.ghanaairwatch.dto.AirQualityResponse;
import com.ghanaairwatch.dto.HealthProfile;
import com.ghanaairwatch.dto.HealthRiskResponse;
import com.ghanaairwatch.entity.Location;
import com.ghanaairwatch.repository.LocationRepository;
import com.ghanaairwatch.service.AirQualityService;
import com.ghanaairwatch.service.HealthRiskEngine;
import com.ghanaairwatch.service.HistoryService;
import org.springframework.web.bind.annotation.*;

// POST /api/health-risk?locationId=1
// Body: { "age": 32, "gender": "female", "asthma": true, "heartDisease": false,
//         "pregnancy": false, "outdoorActivity": "medium", "smoking": "none" }
// Returns a Low/Moderate/High/Very High verdict with personalised advice.
@RestController
@RequestMapping("/api/health-risk")
public class HealthRiskController {

    private final AirQualityService airQualityService;
    private final HistoryService historyService;
    private final HealthRiskEngine riskEngine;
    private final LocationRepository locationRepository;

    public HealthRiskController(AirQualityService airQualityService,
                                HistoryService historyService,
                                HealthRiskEngine riskEngine,
                                LocationRepository locationRepository) {
        this.airQualityService = airQualityService;
        this.historyService = historyService;
        this.riskEngine = riskEngine;
        this.locationRepository = locationRepository;
    }

    @PostMapping
    public HealthRiskResponse analyze(@RequestParam Long locationId, @RequestBody HealthProfile profile) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown location id: " + locationId));

        AirQualityResponse reading = airQualityService.fetchCurrent(location.getLat(), location.getLon());
        historyService.recordReading(location, reading);

        return riskEngine.analyze(reading.aqi(), profile);
    }
}

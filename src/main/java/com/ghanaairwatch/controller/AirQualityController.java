package com.ghanaairwatch.controller;

import com.ghanaairwatch.dto.AirQualityResponse;
import com.ghanaairwatch.entity.AirQualityReading;
import com.ghanaairwatch.entity.Location;
import com.ghanaairwatch.repository.LocationRepository;
import com.ghanaairwatch.service.AirQualityService;
import com.ghanaairwatch.service.HistoryService;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// This is the endpoint the frontend calls instead of hitting OpenWeatherMap
// directly. Two benefits: the API key never reaches the browser, and every
// call gets quietly logged to the database, building real history over time.
@RestController
@RequestMapping("/api/air-quality")
public class AirQualityController {

    private final AirQualityService airQualityService;
    private final LocationRepository locationRepository;
    private final HistoryService historyService;

    public AirQualityController(AirQualityService airQualityService,
                                 LocationRepository locationRepository,
                                 HistoryService historyService) {
        this.airQualityService = airQualityService;
        this.locationRepository = locationRepository;
        this.historyService = historyService;
    }

    // GET /api/air-quality?locationId=1
    @GetMapping
    public AirQualityResponse getCurrent(@RequestParam @NotNull Long locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown location id: " + locationId));

        AirQualityResponse reading = airQualityService.fetchCurrent(location.getLat(), location.getLon());
        historyService.recordReading(location, reading);
        return reading;
    }

    // GET /api/air-quality/history?locationId=1&days=7
    @GetMapping("/history")
    public List<AirQualityReading> getHistory(
            @RequestParam @NotNull Long locationId,
            @RequestParam(defaultValue = "7") int days
    ) {
        return historyService.getHistory(locationId, days);
    }
}

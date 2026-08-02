package com.ghanaairwatch.controller;

import com.ghanaairwatch.dto.AirQualityResponse;
import com.ghanaairwatch.dto.PredictionResponse;
import com.ghanaairwatch.entity.AirQualityReading;
import com.ghanaairwatch.entity.Location;
import com.ghanaairwatch.repository.LocationRepository;
import com.ghanaairwatch.service.AirQualityService;
import com.ghanaairwatch.service.AqiPredictor;
import com.ghanaairwatch.service.HistoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// GET /api/predictions?locationId=1
// Forecasts AQI +1 hour, +6 hours and tomorrow by fitting a straight line
// through the readings this city has accumulated in the database.
@RestController
@RequestMapping("/api/predictions")
public class PredictionController {

    private final AirQualityService airQualityService;
    private final HistoryService historyService;
    private final AqiPredictor predictor;
    private final LocationRepository locationRepository;

    public PredictionController(AirQualityService airQualityService,
                                HistoryService historyService,
                                AqiPredictor predictor,
                                LocationRepository locationRepository) {
        this.airQualityService = airQualityService;
        this.historyService = historyService;
        this.predictor = predictor;
        this.locationRepository = locationRepository;
    }

    @GetMapping
    public PredictionResponse predict(@RequestParam Long locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown location id: " + locationId));

        AirQualityResponse current = airQualityService.fetchCurrent(location.getLat(), location.getLon());
        historyService.recordReading(location, current);

        List<AirQualityReading> history = historyService.getHistory(locationId, 7);
        return predictor.predict(history, current.aqi());
    }
}

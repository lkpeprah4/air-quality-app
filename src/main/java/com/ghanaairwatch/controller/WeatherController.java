package com.ghanaairwatch.controller;

import com.ghanaairwatch.dto.WeatherResponse;
import com.ghanaairwatch.entity.Location;
import com.ghanaairwatch.repository.LocationRepository;
import com.ghanaairwatch.service.WeatherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// GET /api/weather?locationId=1
// Live temperature, humidity, wind, pressure and rain for a city.
// (The /api/air-quality endpoint already includes all of this too.)
@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    private final WeatherService weatherService;
    private final LocationRepository locationRepository;

    public WeatherController(WeatherService weatherService, LocationRepository locationRepository) {
        this.weatherService = weatherService;
        this.locationRepository = locationRepository;
    }

    @GetMapping
    public WeatherResponse getWeather(@RequestParam Long locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown location id: " + locationId));
        return weatherService.fetch(location.getLat(), location.getLon());
    }
}

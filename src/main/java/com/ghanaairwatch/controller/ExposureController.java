package com.ghanaairwatch.controller;

import com.ghanaairwatch.dto.ExposureResponse;
import com.ghanaairwatch.entity.Location;
import com.ghanaairwatch.repository.LocationRepository;
import com.ghanaairwatch.service.AirQualityService;
import com.ghanaairwatch.service.ExposureService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// GET /api/exposure?locationId=1&hours=6&activity=jogging
// "I stayed outside for 6 hours" -> how much pollution that cost you.
// activity: resting | walking | jogging | sports
@RestController
@RequestMapping("/api/exposure")
public class ExposureController {

    private final AirQualityService airQualityService;
    private final ExposureService exposureService;
    private final LocationRepository locationRepository;

    public ExposureController(AirQualityService airQualityService,
                              ExposureService exposureService,
                              LocationRepository locationRepository) {
        this.airQualityService = airQualityService;
        this.exposureService = exposureService;
        this.locationRepository = locationRepository;
    }

    @GetMapping
    public ExposureResponse calculate(
            @RequestParam Long locationId,
            @RequestParam double hours,
            @RequestParam(defaultValue = "walking") String activity) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown location id: " + locationId));

        int aqi = airQualityService.fetchCurrent(location.getLat(), location.getLon()).aqi();
        return exposureService.calculate(aqi, hours, activity);
    }
}

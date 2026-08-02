package com.ghanaairwatch.controller;

import com.ghanaairwatch.entity.Location;
import com.ghanaairwatch.repository.LocationRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// GET http://localhost:8080/api/locations
// Returns the list of Ghanaian cities the app supports.
@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private final LocationRepository locationRepository;

    public LocationController(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @GetMapping
    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }
}

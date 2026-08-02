package com.ghanaairwatch.controller;

import com.ghanaairwatch.dto.CompareCityResponse;
import com.ghanaairwatch.service.CompareService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// GET /api/compare?locationIds=1,2,3
// Compares AQI, PM2.5, PM10, temperature and humidity across cities.
@RestController
@RequestMapping("/api/compare")
public class CompareController {

    private final CompareService compareService;

    public CompareController(CompareService compareService) {
        this.compareService = compareService;
    }

    @GetMapping
    public List<CompareCityResponse> compare(@RequestParam List<Long> locationIds) {
        return compareService.compare(locationIds);
    }
}

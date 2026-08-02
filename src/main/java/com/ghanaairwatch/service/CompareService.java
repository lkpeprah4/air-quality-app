package com.ghanaairwatch.service;

import com.ghanaairwatch.dto.AirQualityResponse;
import com.ghanaairwatch.dto.CompareCityResponse;
import com.ghanaairwatch.entity.Location;
import com.ghanaairwatch.repository.LocationRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

// Compare Cities: fetches the current reading for each requested city and
// lines them up so the frontend can draw "Accra vs Kumasi" side by side.
@Service
public class CompareService {

    private final LocationRepository locationRepository;
    private final AirQualityService airQualityService;

    public CompareService(LocationRepository locationRepository, AirQualityService airQualityService) {
        this.locationRepository = locationRepository;
        this.airQualityService = airQualityService;
    }

    public List<CompareCityResponse> compare(List<Long> locationIds) {
        if (locationIds == null || locationIds.isEmpty()) {
            throw new IllegalArgumentException("Pass at least one locationId, e.g. ?locationIds=1,2");
        }

        List<CompareCityResponse> result = new ArrayList<>();
        for (Long id : locationIds) {
            Location location = locationRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Unknown location id: " + id));
            AirQualityResponse r = airQualityService.fetchCurrent(location.getLat(), location.getLon());
            result.add(new CompareCityResponse(
                    location.getId(),
                    location.getName(),
                    r.aqi(),
                    r.pm25(),
                    r.pm10(),
                    r.temperature(),
                    r.humidity(),
                    r.timestamp()
            ));
        }
        return result;
    }
}

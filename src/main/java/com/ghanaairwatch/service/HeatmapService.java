package com.ghanaairwatch.service;

import com.ghanaairwatch.dto.AirQualityResponse;
import com.ghanaairwatch.dto.HeatmapPoint;
import com.ghanaairwatch.entity.Location;
import com.ghanaairwatch.repository.LocationRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

// Pollution Heatmap data: generates a grid of points covering Ghana and
// estimates the AQI at each cell by inverse-distance weighting from the
// known monitoring locations. The frontend colours each point by AQI to
// draw the green -> yellow -> orange -> red -> purple map.
@Service
public class HeatmapService {

    private static final double MIN_LAT = 4.5, MAX_LAT = 11.2;
    private static final double MIN_LON = -3.5, MAX_LON = 1.5;
    private static final double LAT_STEP = 0.28, LON_STEP = 0.28;

    private final LocationRepository locationRepository;
    private final AirQualityService airQualityService;
    private final HealthRiskCalculator bands;

    public HeatmapService(LocationRepository locationRepository,
                          AirQualityService airQualityService,
                          HealthRiskCalculator bands) {
        this.locationRepository = locationRepository;
        this.airQualityService = airQualityService;
        this.bands = bands;
    }

    public List<HeatmapPoint> generate() {
        List<Location> stations = locationRepository.findAll();
        // A station with no reading is simply ignored by the interpolation.
        List<StationReading> readings = stations.stream()
                .map(this::readStation)
                .filter(s -> s.aqi() >= 0)
                .toList();

        List<HeatmapPoint> grid = new ArrayList<>();
        for (double lat = MIN_LAT; lat <= MAX_LAT; lat += LAT_STEP) {
            for (double lon = MIN_LON; lon <= MAX_LON; lon += LON_STEP) {
                double aqi = interpolate(lat, lon, readings);
                grid.add(new HeatmapPoint(round(lat), round(lon), aqi, bands.colorFor((int) Math.round(aqi))));
            }
        }
        return grid;
    }

    private StationReading readStation(Location l) {
        try {
            AirQualityResponse r = airQualityService.fetchCurrent(l.getLat(), l.getLon());
            return new StationReading(l.getLat(), l.getLon(), r.aqi());
        } catch (Exception e) {
            return new StationReading(l.getLat(), l.getLon(), -1); // skip this one
        }
    }

    // Inverse-distance weighting: closer stations influence a cell more.
    private double interpolate(double lat, double lon, List<StationReading> stations) {
        if (stations.isEmpty()) return 0;

        double weightSum = 0, valueSum = 0;
        for (StationReading s : stations) {
            double d = Math.sqrt(Math.pow(s.lat() - lat, 2) + Math.pow(s.lon() - lon, 2));
            double w = 1.0 / (d + 0.1);
            weightSum += w;
            valueSum += w * s.aqi();
        }
        double aqi = valueSum / weightSum;
        return Math.max(0, Math.min(500, aqi));
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    private record StationReading(double lat, double lon, double aqi) {}
}

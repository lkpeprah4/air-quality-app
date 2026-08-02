package com.ghanaairwatch.service;

import com.ghanaairwatch.dto.AirQualityResponse;
import com.ghanaairwatch.entity.AirQualityReading;
import com.ghanaairwatch.entity.Location;
import com.ghanaairwatch.repository.AirQualityReadingRepository;
import com.ghanaairwatch.repository.LocationRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

// Handles saving readings to the database and pulling back real history --
// this is what replaces the frontend's randomly generated mock trend data.
@Service
public class HistoryService {

    private final AirQualityReadingRepository readingRepository;
    private final LocationRepository locationRepository;

    public HistoryService(AirQualityReadingRepository readingRepository, LocationRepository locationRepository) {
        this.readingRepository = readingRepository;
        this.locationRepository = locationRepository;
    }

    public void recordReading(Location location, AirQualityResponse reading) {
        AirQualityReading entity = new AirQualityReading(
                location,
                reading.aqi(),
                reading.pm25(),
                reading.pm10(),
                reading.o3(),
                reading.no2(),
                reading.so2(),
                reading.co(),
                reading.temperature(),
                reading.humidity(),
                reading.windSpeed(),
                reading.windDirection(),
                reading.pressure(),
                reading.rain(),
                reading.weatherDescription(),
                Instant.now()
        );
        readingRepository.save(entity);
    }

    public List<AirQualityReading> getHistory(Long locationId, int days) {
        Instant since = Instant.now().minus(days, ChronoUnit.DAYS);
        return readingRepository.findByLocationIdAndRecordedAtAfterOrderByRecordedAtAsc(locationId, since);
    }
}

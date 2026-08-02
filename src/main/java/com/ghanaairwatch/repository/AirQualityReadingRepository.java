package com.ghanaairwatch.repository;

import com.ghanaairwatch.entity.AirQualityReading;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface AirQualityReadingRepository extends JpaRepository<AirQualityReading, Long> {

    // Spring Boot reads this method name and builds the query automatically:
    // "find all readings for this location, recorded after this time,
    //  ordered oldest to newest" -- no SQL written by hand.
    List<AirQualityReading> findByLocationIdAndRecordedAtAfterOrderByRecordedAtAsc(
            Long locationId, Instant after);
}

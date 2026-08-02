package com.ghanaairwatch.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

// One row = one air quality reading for one location at one point in time.
// This is what makes real historical trends possible, instead of the random
// mock numbers the frontend currently generates.
@Entity
@Table(name = "air_quality_readings")
@Getter
@Setter
@NoArgsConstructor
public class AirQualityReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(nullable = false)
    private Integer aqi;

    private Double pm25;
    private Double pm10;
    private Double o3;
    private Double no2;
    private Double so2;
    private Double co;

    private Double temperature;
    private Integer humidity;
    private Double windSpeed;
    private Integer windDirection;
    private Double pressure;
    private Double rain;
    private String weatherDescription;

    @Column(nullable = false)
    private Instant recordedAt;

    public AirQualityReading(Location location, Integer aqi, Double pm25, Double pm10,
                              Double o3, Double no2, Double so2, Double co,
                              Double temperature, Integer humidity, Double windSpeed,
                              Integer windDirection, Double pressure, Double rain,
                              String weatherDescription, Instant recordedAt) {
        this.location = location;
        this.aqi = aqi;
        this.pm25 = pm25;
        this.pm10 = pm10;
        this.o3 = o3;
        this.no2 = no2;
        this.so2 = so2;
        this.co = co;
        this.temperature = temperature;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.pressure = pressure;
        this.rain = rain;
        this.weatherDescription = weatherDescription;
        this.recordedAt = recordedAt;
    }
}

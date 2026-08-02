package com.ghanaairwatch.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Represents a city/location someone can check air quality for.
// Matches the GHANA_LOCATIONS list already in the frontend.
@Entity
@Table(name = "locations")
@Getter
@Setter
@NoArgsConstructor
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String region;

    @Column(nullable = false)
    private Double lat;

    @Column(nullable = false)
    private Double lon;

    public Location(String name, String region, Double lat, Double lon) {
        this.name = name;
        this.region = region;
        this.lat = lat;
        this.lon = lon;
    }
}

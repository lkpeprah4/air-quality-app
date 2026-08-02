package com.ghanaairwatch.config;

import com.ghanaairwatch.entity.Location;
import com.ghanaairwatch.repository.LocationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// Runs once automatically when the app starts. If the locations table is
// empty (e.g. first run ever), it fills in the same 10 Ghanaian cities the
// frontend already knows about, so the two sides stay in sync.
@Component
public class DataSeeder implements CommandLineRunner {

    private final LocationRepository locationRepository;

    public DataSeeder(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public void run(String... args) {
        if (locationRepository.count() > 0) return;

        locationRepository.saveAll(java.util.List.of(
                new Location("Accra", "Greater Accra", 5.6037, -0.187),
                new Location("Kumasi", "Ashanti", 6.6885, -1.6244),
                new Location("Tamale", "Northern", 9.4008, -0.8393),
                new Location("Takoradi", "Western", 4.8845, -1.7554),
                new Location("Tema", "Greater Accra", 5.6698, -0.0166),
                new Location("Cape Coast", "Central", 5.1053, -1.2466),
                new Location("Ho", "Volta", 6.611, 0.4713),
                new Location("Sunyani", "Bono", 7.3399, -2.3268),
                new Location("Koforidua", "Eastern", 6.0941, -0.2591),
                new Location("Bolgatanga", "Upper East", 10.7856, -0.8514)
        ));
    }
}

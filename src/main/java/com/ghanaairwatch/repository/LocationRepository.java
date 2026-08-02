package com.ghanaairwatch.repository;

import com.ghanaairwatch.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Spring Boot automatically implements this interface at startup - you never
// write the SQL yourself. JpaRepository already gives you save(), findAll(),
// findById(), delete(), etc. for free. We just add the extra lookup we need.
public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByNameIgnoreCase(String name);
}

package com.ghanaairwatch.repository;

import com.ghanaairwatch.entity.FavoriteLocation;
import com.ghanaairwatch.entity.Location;
import com.ghanaairwatch.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteLocationRepository extends JpaRepository<FavoriteLocation, Long> {
    List<FavoriteLocation> findByUserOrderBySavedAtDesc(User user);
    Optional<FavoriteLocation> findByIdAndUser(Long id, User user);
    boolean existsByUserAndLocation(User user, Location location);
}

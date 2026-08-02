package com.ghanaairwatch.controller;

import com.ghanaairwatch.dto.FavoriteResponse;
import com.ghanaairwatch.entity.FavoriteLocation;
import com.ghanaairwatch.entity.Location;
import com.ghanaairwatch.entity.User;
import com.ghanaairwatch.repository.FavoriteLocationRepository;
import com.ghanaairwatch.repository.LocationRepository;
import com.ghanaairwatch.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

// Protected endpoints (need a valid JWT): save and list favourite cities.
//   GET    /api/users/me/favorites          -> my saved places
//   POST   /api/users/me/favorites?locationId=1  -> save one
//   DELETE /api/users/me/favorites/{id}     -> remove one
@RestController
@RequestMapping("/api/users/me/favorites")
public class FavoriteController {

    private final FavoriteLocationRepository favoriteRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;

    public FavoriteController(FavoriteLocationRepository favoriteRepository,
                              LocationRepository locationRepository,
                              UserRepository userRepository) {
        this.favoriteRepository = favoriteRepository;
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<FavoriteResponse> list() {
        return favoriteRepository.findByUserOrderBySavedAtDesc(currentUser()).stream()
                .map(f -> new FavoriteResponse(
                        f.getId(),
                        f.getLocation().getId(),
                        f.getLocation().getName(),
                        f.getSavedAt()))
                .toList();
    }

    @PostMapping
    public FavoriteResponse add(@RequestParam Long locationId) {
        User user = currentUser();
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown location id: " + locationId));

        if (favoriteRepository.existsByUserAndLocation(user, location)) {
            throw new IllegalArgumentException("You already saved this location.");
        }

        FavoriteLocation saved = favoriteRepository.save(new FavoriteLocation(user, location, Instant.now()));
        return new FavoriteResponse(saved.getId(), location.getId(), location.getName(), saved.getSavedAt());
    }

    @DeleteMapping("/{id}")
    public void remove(@PathVariable Long id) {
        FavoriteLocation favorite = favoriteRepository.findByIdAndUser(id, currentUser())
                .orElseThrow(() -> new IllegalArgumentException("Favorite not found."));
        favoriteRepository.delete(favorite);
    }

    private User currentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username).orElseThrow();
    }
}

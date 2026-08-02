package com.ghanaairwatch.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

// One row = one location a user has "saved" so it shows in their My Places
// list and can trigger their notifications.
@Entity
@Table(name = "favorite_locations")
@Getter
@Setter
@NoArgsConstructor
public class FavoriteLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(nullable = false)
    private Instant savedAt;

    public FavoriteLocation(User user, Location location, Instant savedAt) {
        this.user = user;
        this.location = location;
        this.savedAt = savedAt;
    }
}

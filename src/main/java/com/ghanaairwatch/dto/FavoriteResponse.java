package com.ghanaairwatch.dto;

import java.time.Instant;

// One saved favorite location shown in the "My Places" list.
public record FavoriteResponse(
        Long id,
        Long locationId,
        String name,
        Instant savedAt
) {}

package com.ghanaairwatch.dto;

// One cell of the pollution heatmap grid. The frontend draws these as colored
// dots/polygons on a Leaflet/Google map. colorHex matches the AQI band so the
// map turns green -> yellow -> orange -> red -> purple with rising AQI.
public record HeatmapPoint(
        double lat,
        double lon,
        double aqi,
        String colorHex
) {}

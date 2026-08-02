package com.ghanaairwatch.controller;

import com.ghanaairwatch.dto.HeatmapPoint;
import com.ghanaairwatch.service.HeatmapService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// GET /api/heatmap
// A grid of interpolated AQI points across Ghana for the pollution map.
@RestController
@RequestMapping("/api/heatmap")
public class HeatmapController {

    private final HeatmapService heatmapService;

    public HeatmapController(HeatmapService heatmapService) {
        this.heatmapService = heatmapService;
    }

    @GetMapping
    public List<HeatmapPoint> heatmap() {
        return heatmapService.generate();
    }
}

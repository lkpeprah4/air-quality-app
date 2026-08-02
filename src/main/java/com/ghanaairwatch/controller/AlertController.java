package com.ghanaairwatch.controller;

import com.ghanaairwatch.dto.AlertResponse;
import com.ghanaairwatch.service.AlertService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// GET /api/alerts/check?locationId=1
// Tells the frontend whether to show a notification banner (AQI > 150).
@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping("/check")
    public AlertResponse check(@RequestParam Long locationId) {
        return alertService.check(locationId);
    }
}

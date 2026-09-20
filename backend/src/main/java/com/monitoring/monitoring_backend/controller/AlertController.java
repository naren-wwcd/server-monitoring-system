package com.monitoring.monitoring_backend.controller;

import com.monitoring.monitoring_backend.dto.AlertResponseDto;
import com.monitoring.monitoring_backend.service.AlertService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping
    public List<AlertResponseDto> getActiveAlerts() {
        return alertService.getActiveAlerts();
    }
}
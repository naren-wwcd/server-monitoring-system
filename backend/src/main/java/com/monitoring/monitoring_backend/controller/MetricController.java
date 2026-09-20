package com.monitoring.monitoring_backend.controller;

import com.monitoring.monitoring_backend.dto.MetricRequestDto;
import com.monitoring.monitoring_backend.entity.Metric;
import com.monitoring.monitoring_backend.service.MetricService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/metrics")
public class MetricController {

    private final MetricService metricService;

    public MetricController(MetricService metricService) {
        this.metricService = metricService;
    }

    @PostMapping
    public ResponseEntity<Metric> receiveMetric(@RequestBody MetricRequestDto dto) {
        Metric savedMetric = metricService.save(dto);
        return new ResponseEntity<>(savedMetric, HttpStatus.CREATED);
    }

}
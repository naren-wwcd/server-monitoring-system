package com.monitoring.monitoring_backend.controller;

import com.monitoring.monitoring_backend.dto.MetricRequestDto;
import com.monitoring.monitoring_backend.dto.MetricResponseDto;
import com.monitoring.monitoring_backend.dto.ServerDto;
import com.monitoring.monitoring_backend.entity.Metric;
import com.monitoring.monitoring_backend.exception.MetricNotFoundException;
import com.monitoring.monitoring_backend.service.MetricService;
import com.monitoring.monitoring_backend.service.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servers")
public class ServerController {

    private final ServerService serverService;
    private final MetricService metricService;

    public ServerController(ServerService serverService, MetricService metricService)
    {
        this.serverService = serverService;
        this.metricService = metricService;
    }

    @GetMapping
    public List<ServerDto> getAllServers()
    {
        return serverService.getAllServers();
    }

    @GetMapping("/{id}")
    public ServerDto getServer(@PathVariable Long id)
    {
        return serverService.getServer(id);
    }

    @GetMapping("/{id}/metrics")
    public ResponseEntity<Metric> getLatestMetricForServer(@PathVariable Long id) {
        Metric metric = metricService.getLatestMetric(id).orElseThrow(() -> new MetricNotFoundException("No metrics found for server with id " + id));
        return new ResponseEntity<>(metric, HttpStatus.OK);
    }

    @GetMapping("/{id}/metrics/history")
    public List<MetricResponseDto> getMetricHistory(@PathVariable Long id, @RequestParam(defaultValue = "24h") String range) {
        return metricService.getMetricHistory(id, range);
    }
}
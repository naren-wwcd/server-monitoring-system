package com.monitoring.monitoring_backend.service;

import com.monitoring.monitoring_backend.dto.AlertResponseDto;
import com.monitoring.monitoring_backend.entity.Alert;
import com.monitoring.monitoring_backend.entity.Metric;
import com.monitoring.monitoring_backend.entity.Server;
import com.monitoring.monitoring_backend.repository.AlertRepository;
import com.monitoring.monitoring_backend.repository.ServerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AlertService {

    private static final Logger logger = LoggerFactory.getLogger(AlertService.class);

    private static final double CPU_THRESHOLD = 90.0;
    private static final double RAM_THRESHOLD = 90.0;
    private static final double DISK_THRESHOLD = 90.0;

    private final ServerRepository serverRepository;
    private final ServerService serverService;
    private final MetricService metricService;
    private final AlertRepository alertRepository;

    public AlertService(ServerRepository serverRepository, ServerService serverService,
                        MetricService metricService, AlertRepository alertRepository) {
        this.serverRepository = serverRepository;
        this.serverService = serverService;
        this.metricService = metricService;
        this.alertRepository = alertRepository;
    }

    @Scheduled(fixedRate = 15000)
    public void evaluateAlerts() {
        List<Server> servers = serverRepository.findAll();
        for (Server server : servers) {
            checkHeartbeat(server);
            checkThresholds(server);
        }
    }

    private void checkHeartbeat(Server server) {
        boolean alive = serverService.isAlive(server.getId());
        evaluateCondition(server, "SERVER_DOWN", !alive,
                server.getHostname() + " is not responding");
    }

    private void checkThresholds(Server server) {
        Optional<Metric> latest = metricService.getLatestMetric(server.getId());
        if (latest.isEmpty()) {
            return;
        }
        Metric metric = latest.get();

        evaluateCondition(server, "CPU_HIGH", metric.getCpuPercent() > CPU_THRESHOLD,
                "CPU usage at " + metric.getCpuPercent() + "% on " + server.getHostname());

        evaluateCondition(server, "RAM_HIGH", metric.getRamPercent() > RAM_THRESHOLD,
                "RAM usage at " + metric.getRamPercent() + "% on " + server.getHostname());

        evaluateCondition(server, "DISK_HIGH", metric.getDiskPercent() > DISK_THRESHOLD,
                "Disk usage at " + metric.getDiskPercent() + "% on " + server.getHostname());
    }

    private void evaluateCondition(Server server, String type, boolean breached, String message) {
        Optional<Alert> existing = alertRepository.findByServerIdAndTypeAndStatus(server.getId(), type, "ACTIVE");

        if (breached) {
            if (existing.isEmpty()) {
                Alert alert = new Alert();
                alert.setServer(server);
                alert.setType(type);
                alert.setMessage(message);
                alert.setStatus("ACTIVE");
                alert.setTriggeredAt(LocalDateTime.now());
                alertRepository.save(alert);
                logger.warn("Alert triggered: {} for server {}", type, server.getHostname());
            }
        } else {
            if (existing.isPresent()) {
                Alert alert = existing.get();
                alert.setStatus("RESOLVED");
                alert.setResolvedAt(LocalDateTime.now());
                alertRepository.save(alert);
                logger.info("Alert resolved: {} for server {}", type, server.getHostname());
            }
        }
    }
    public List<AlertResponseDto> getActiveAlerts() {
        List<Alert> alerts = alertRepository.findByStatus("ACTIVE");
        return alerts.stream().map(this::convertToDto).toList();
    }

    private AlertResponseDto convertToDto(Alert alert) {
        return new AlertResponseDto(
                alert.getId(),
                alert.getServer().getHostname(),
                alert.getType(),
                alert.getMessage(),
                alert.getStatus(),
                alert.getTriggeredAt(),
                alert.getResolvedAt()
        );
    }
}
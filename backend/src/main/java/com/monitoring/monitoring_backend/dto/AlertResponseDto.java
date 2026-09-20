package com.monitoring.monitoring_backend.dto;

import java.time.LocalDateTime;

public class AlertResponseDto {

    private Long id;
    private String hostname;
    private String type;
    private String message;
    private String status;
    private LocalDateTime triggeredAt;
    private LocalDateTime resolvedAt;

    public AlertResponseDto() {}

    public AlertResponseDto(Long id, String hostname, String type, String message, String status,
                            LocalDateTime triggeredAt, LocalDateTime resolvedAt) {
        this.id = id;
        this.hostname = hostname;
        this.type = type;
        this.message = message;
        this.status = status;
        this.triggeredAt = triggeredAt;
        this.resolvedAt = resolvedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getHostname() { return hostname; }
    public void setHostname(String hostname) { this.hostname = hostname; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getTriggeredAt() { return triggeredAt; }
    public void setTriggeredAt(LocalDateTime triggeredAt) { this.triggeredAt = triggeredAt; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
}
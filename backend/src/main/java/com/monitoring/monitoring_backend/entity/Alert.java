package com.monitoring.monitoring_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alerts")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "server_id", nullable = false)
    private Server server;

    private String type;        // e.g. "CPU_HIGH", "RAM_HIGH", "DISK_HIGH", "SERVER_DOWN"
    private String message;
    private String status;      // "ACTIVE" or "RESOLVED"

    private LocalDateTime triggeredAt;
    private LocalDateTime resolvedAt;

    public Alert(){}

    public Alert(Long id, String message, Server server, String type, String status, LocalDateTime triggeredAt, LocalDateTime resolvedAt) {
        this.id = id;
        this.message = message;
        this.server = server;
        this.type = type;
        this.status = status;
        this.triggeredAt = triggeredAt;
        this.resolvedAt = resolvedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getTriggeredAt() {
        return triggeredAt;
    }

    public void setTriggeredAt(LocalDateTime triggeredAt) {
        this.triggeredAt = triggeredAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }


    // getters and setters — I'll leave you to write these, same pattern as your other entities
}
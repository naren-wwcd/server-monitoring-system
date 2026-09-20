package com.monitoring.monitoring_backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "servers")
public class Server {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String hostname;

    private String status;

    public Server() {}

    public Server(String hostname, String status) {
        this.hostname = hostname;
        this.status = status;
    }

    public Long getId() { return id; }
    public String getHostname() { return hostname; }
    public void setHostname(String hostname) { this.hostname = hostname; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public void setId(Long id) { this.id = id; }
}
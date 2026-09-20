package com.monitoring.monitoring_backend.entity;

import jakarta.persistence.*;
import org.springframework.context.annotation.Primary;

import java.time.LocalDateTime;

@Entity
@Table(name = "metrics")
public class Metric {

    public Metric(){}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "server_id", nullable = false)
    private Server server;

    private Double cpuPercent;
    private Double ramPercent ;
    private Double diskPercent ;
    private Double networkSentMb ;
    private Double networkReceivedMb ;
    private Double uptimeHours;
    private int processCount;
    private LocalDateTime timestamp ;
    private Double ramTotalGb;
    private Double ramUsedGb;
    private Double diskTotalGb;
    private Double diskUsedGb ;
    private Double diskFreeGb ;

    public Metric(Long id, Server server, Double cpuPercent, Double ramPercent, Double diskPercent, Double networkSentMb, Double uptimeHours, Double networkReceivedMb, int processCount, LocalDateTime timestamp,Double ramTotalGb,Double ramUsedGb,Double diskTotalGb,Double diskUsedGb,Double diskFreeGb) {
        this.id = id;
        this.server = server;
        this.cpuPercent = cpuPercent;
        this.ramPercent = ramPercent;
        this.diskPercent = diskPercent;
        this.networkSentMb = networkSentMb;
        this.uptimeHours = uptimeHours;
        this.networkReceivedMb = networkReceivedMb;
        this.processCount = processCount;
        this.timestamp = timestamp;
        this.ramTotalGb=ramTotalGb;
        this.ramUsedGb=ramUsedGb;
        this.diskTotalGb=diskTotalGb;
        this.diskUsedGb=diskUsedGb;
        this.diskFreeGb=diskFreeGb;
    }

    public Double getRamTotalGb() {
        return ramTotalGb;
    }

    public void setRamTotalGb(Double ramTotalGb) {
        this.ramTotalGb = ramTotalGb;
    }

    public Double getDiskUsedGb() {
        return diskUsedGb;
    }

    public void setDiskUsedGb(Double diskUsedGb) {
        this.diskUsedGb = diskUsedGb;
    }

    public Double getRamUsedGb() {
        return ramUsedGb;
    }

    public void setRamUsedGb(Double ramUsedGb) {
        this.ramUsedGb = ramUsedGb;
    }

    public Double getDiskTotalGb() {
        return diskTotalGb;
    }

    public void setDiskTotalGb(Double diskTotalGb) {
        this.diskTotalGb = diskTotalGb;
    }

    public Double getDiskFreeGb() {
        return diskFreeGb;
    }

    public void setDiskFreeGb(Double diskFreeGb) {
        this.diskFreeGb = diskFreeGb;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getCpuPercent() {
        return cpuPercent;
    }

    public void setCpuPercent(Double cpuPercent) {
        this.cpuPercent = cpuPercent;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public Double getRamPercent() {
        return ramPercent;
    }

    public void setRamPercent(Double ramPercent) {
        this.ramPercent = ramPercent;
    }

    public Double getNetworkSentMb() {
        return networkSentMb;
    }

    public void setNetworkSentMb(Double networkSentMb) {
        this.networkSentMb = networkSentMb;
    }

    public Double getUptimeHours() {
        return uptimeHours;
    }

    public void setUptimeHours(Double uptimeHours) {
        this.uptimeHours = uptimeHours;
    }

    public Double getDiskPercent() {
        return diskPercent;
    }

    public void setDiskPercent(Double diskPercent) {
        this.diskPercent = diskPercent;
    }

    public Double getNetworkReceivedMb() {
        return networkReceivedMb;
    }

    public void setNetworkReceivedMb(Double networkReceivedMb) {
        this.networkReceivedMb = networkReceivedMb;
    }

    public int getProcessCount() {
        return processCount;
    }

    public void setProcessCount(int processCount) {
        this.processCount = processCount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

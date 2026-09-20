package com.monitoring.monitoring_backend.dto;

import java.time.LocalDateTime;

public class MetricResponseDto {

    private LocalDateTime timestamp;
    private Double cpuPercent;
    private Double ramPercent;
    private Double diskPercent;
    private Double networkSentMb;
    private Double networkReceivedMb;

    public MetricResponseDto() {}

    public MetricResponseDto(LocalDateTime timestamp, Double cpuPercent, Double ramPercent, Double diskPercent,Double networkSentMb,Double networkReceivedMb) {
        this.timestamp = timestamp;
        this.cpuPercent = cpuPercent;
        this.ramPercent = ramPercent;
        this.diskPercent = diskPercent;
        this.networkReceivedMb=networkReceivedMb;
        this.networkSentMb=networkSentMb;
    }

    public Double getNetworkSentMb() {
        return networkSentMb;
    }

    public void setNetworkSentMb(Double networkSentMb) {
        this.networkSentMb = networkSentMb;
    }

    public Double getNetworkReceivedMb() {
        return networkReceivedMb;
    }

    public void setNetworkReceivedMb(Double networkReceivedMb) {
        this.networkReceivedMb = networkReceivedMb;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Double getCpuPercent() {
        return cpuPercent;
    }

    public void setCpuPercent(Double cpuPercent) {
        this.cpuPercent = cpuPercent;
    }

    public Double getRamPercent() {
        return ramPercent;
    }

    public void setRamPercent(Double ramPercent) {
        this.ramPercent = ramPercent;
    }

    public Double getDiskPercent() {
        return diskPercent;
    }

    public void setDiskPercent(Double diskPercent) {
        this.diskPercent = diskPercent;
    }
}
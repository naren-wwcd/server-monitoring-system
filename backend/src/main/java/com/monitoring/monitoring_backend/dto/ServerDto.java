package com.monitoring.monitoring_backend.dto;

public class ServerDto {
    ServerDto(){}

    private String hostname;
    private String status;
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ServerDto(String hostname, String status,Long id) {
        this.hostname = hostname;
        this.status = status;
        this.id=id;
    }
}

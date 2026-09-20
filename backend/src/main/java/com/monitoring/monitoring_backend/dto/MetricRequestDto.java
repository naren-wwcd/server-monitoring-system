package com.monitoring.monitoring_backend.dto;

public class MetricRequestDto {
    private String hostname;
    private String timestamp;
    private Double cpuPercent;
    private Double uptimeHours;
    private Integer processCount;
    private RamInfo ram;
    private DiskInfo disk;
    private NetworkInfo network;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Double getCpuPercent() {
        return cpuPercent;
    }

    public void setCpuPercent(Double cpuPercent) {
        this.cpuPercent = cpuPercent;
    }

    public Double getUptimeHours() {
        return uptimeHours;
    }

    public void setUptimeHours(Double uptimeHours) {
        this.uptimeHours = uptimeHours;
    }

    public Integer getProcessCount() {
        return processCount;
    }

    public void setProcessCount(Integer processCount) {
        this.processCount = processCount;
    }

    public DiskInfo getDisk() {
        return disk;
    }

    public void setDisk(DiskInfo disk) {
        this.disk = disk;
    }

    public RamInfo getRam() {
        return ram;
    }

    public void setRam(RamInfo ram) {
        this.ram = ram;
    }

    public NetworkInfo getNetwork() {
        return network;
    }

    public void setNetwork(NetworkInfo network) {
        this.network = network;
    }

    public static class RamInfo
    {
        private Double percent;
        private Double totalGb;
        private Double usedGb;

        public Double getPercent() {
            return percent;
        }

        public void setPercent(Double percent) {
            this.percent = percent;
        }

        public Double getUsedGb() {
            return usedGb;
        }

        public void setUsedGb(Double usedGb) {
            this.usedGb = usedGb;
        }

        public Double getTotalGb() {
            return totalGb;
        }

        public void setTotalGb(Double totalGb) {
            this.totalGb = totalGb;
        }
    }
    public static class DiskInfo
    {
        private Double percent;
        private Double totalGb;
        private Double usedGb;
        private Double freeGb;

        public Double getPercent() {
            return percent;
        }

        public void setPercent(Double percent) {
            this.percent = percent;
        }

        public Double getTotalGb() {
            return totalGb;
        }

        public void setTotalGb(Double totalGb) {
            this.totalGb = totalGb;
        }

        public Double getUsedGb() {
            return usedGb;
        }

        public void setUsedGb(Double usedGb) {
            this.usedGb = usedGb;
        }

        public Double getFreeGb() {
            return freeGb;
        }

        public void setFreeGb(Double freeGb) {
            this.freeGb = freeGb;
        }
    }
    public static class NetworkInfo {
        private Double sentMb;
        private Double receivedMb;

        public Double getSentMb() {
            return sentMb;
        }

        public void setSentMb(Double sentMb) {
            this.sentMb = sentMb;
        }

        public Double getReceivedMb() {
            return receivedMb;
        }

        public void setReceivedMb(Double receivedMb) {
            this.receivedMb = receivedMb;
        }
    }

}

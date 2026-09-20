package com.monitoring.monitoring_backend.service;

import com.monitoring.monitoring_backend.dto.MetricRequestDto;
import com.monitoring.monitoring_backend.dto.MetricResponseDto;
import com.monitoring.monitoring_backend.entity.Metric;
import com.monitoring.monitoring_backend.entity.Server;
import com.monitoring.monitoring_backend.exception.RateLimitExceededException;
import com.monitoring.monitoring_backend.repository.MetricRepository;
import com.monitoring.monitoring_backend.repository.ServerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MetricService {

    private final ServerRepository serverRepository;

    private final JsonMapper jsonMapper;

    private final MetricRepository metricRepository;

    private final RedisTemplate<String, String> redisTemplate;

    public MetricService(ServerRepository serverRepository, MetricRepository metricRepository, RedisTemplate<String, String> redisTemplate, JsonMapper jsonMapper)
    {
        this.serverRepository=serverRepository;
        this.metricRepository=metricRepository;
        this.redisTemplate=redisTemplate;
        this.jsonMapper = jsonMapper;
    }

    public Optional<Metric> getLatestMetric(Long serverId) {
        String cacheKey = "latest_metric:" + serverId;

        try {
            String cachedJson = redisTemplate.opsForValue().get(cacheKey);
            if (cachedJson != null) {
                logger.info("Cache hit for server {}", serverId);
                Metric metric = jsonMapper.readValue(cachedJson, Metric.class);
                return Optional.of(metric);
            }
        } catch (Exception e) {
            logger.error("Failed to read metric from Redis for server {}: {}", serverId, e.getMessage(), e);
        }

        logger.info("Cache miss for server {}, querying database", serverId);
        return metricRepository.findFirstByServerIdOrderByTimestampDesc(serverId);
    }



    private static final Logger logger = LoggerFactory.getLogger(MetricService.class);

    public Metric save(MetricRequestDto metricRequestDto)
    {

        String rateLimitKey = "rate_limit:" + metricRequestDto.getHostname();
        Long count = redisTemplate.opsForValue().increment(rateLimitKey);

        if (count != null && count == 1L) {
            redisTemplate.expire(rateLimitKey, Duration.ofSeconds(10));
        }

        if (count != null && count > 2L) {
            logger.warn("Rate limit exceeded for hostname: {}", metricRequestDto.getHostname());
            throw new RateLimitExceededException("Too many requests from " + metricRequestDto.getHostname());
        }
        Server server = serverRepository.findByHostname(metricRequestDto.getHostname())
                .orElseGet(() -> {
                    Server server1=new Server();
                    server1.setHostname(metricRequestDto.getHostname());
                    server1.setStatus("UP");
                    return serverRepository.save(server1);
                });

        Metric metric=new Metric();
        metric.setServer(server);
        metric.setCpuPercent(metricRequestDto.getCpuPercent());
        metric.setRamPercent(metricRequestDto.getRam().getPercent());
        metric.setDiskPercent(metricRequestDto.getDisk().getPercent());
        metric.setNetworkSentMb(metricRequestDto.getNetwork().getSentMb());
        metric.setNetworkReceivedMb(metricRequestDto.getNetwork().getReceivedMb());
        metric.setUptimeHours(metricRequestDto.getUptimeHours());
        metric.setProcessCount(metricRequestDto.getProcessCount());
        metric.setTimestamp(LocalDateTime.parse(metricRequestDto.getTimestamp()));
        metric.setRamTotalGb(metricRequestDto.getRam().getTotalGb());
        metric.setRamUsedGb(metricRequestDto.getRam().getUsedGb());
        metric.setDiskTotalGb(metricRequestDto.getDisk().getTotalGb());
        metric.setDiskUsedGb(metricRequestDto.getDisk().getUsedGb());
        metric.setDiskFreeGb(metricRequestDto.getDisk().getFreeGb());

        logger.info("Saving metric for server: {}", server.getHostname());
        Metric savedMetric = metricRepository.save(metric);

        try {
            String json = jsonMapper.writeValueAsString(savedMetric);
            redisTemplate.opsForValue().set("latest_metric:" + server.getId(), json);

            redisTemplate.opsForValue().set(
                    "heartbeat:" + server.getId(),
                    "alive",
                    Duration.ofSeconds(30)
            );
        } catch (Exception e) {
            logger.error("Failed to cache metric in Redis for server {}: {}", server.getId(), e.getMessage(), e);
        }

        return savedMetric;

    }
    public List<MetricResponseDto> getMetricHistory(Long serverId) {
        logger.info("Fetching metric history for server: {}", serverId);
        List<Metric> metrics = metricRepository.findByServerIdOrderByTimestampAsc(serverId);
        return metrics.stream().map(this::convertToDto).toList();
    }

    private MetricResponseDto convertToDto(Metric metric) {
        return new MetricResponseDto(
                metric.getTimestamp(),
                metric.getCpuPercent(),
                metric.getRamPercent(),
                metric.getDiskPercent(),
                metric.getNetworkSentMb(),
                metric.getNetworkReceivedMb()

        );
    }
    private LocalDateTime resolveCutoff(String range) {
        if (range == null) {
            return LocalDateTime.now().minusHours(24);
        }

        switch (range) {
            case "24h":
                return LocalDateTime.now().minusHours(24);
            case "7d":
                return LocalDateTime.now().minusDays(7);
            case "30d":
                return LocalDateTime.now().minusDays(30);
            default:
                logger.warn("Unrecognized range '{}', defaulting to 24h", range);
                return LocalDateTime.now().minusHours(24);
        }
    }
    public List<MetricResponseDto> getMetricHistory(Long serverId, String range) {
        logger.info("Fetching metric history for server: {} with range: {}", serverId, range);
        LocalDateTime cutoff = resolveCutoff(range);
        List<Metric> metrics = metricRepository.findByServerIdAndTimestampAfterOrderByTimestampAsc(serverId, cutoff);
        return metrics.stream().map(this::convertToDto).toList();
    }

}
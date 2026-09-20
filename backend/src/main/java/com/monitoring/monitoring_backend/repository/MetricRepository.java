package com.monitoring.monitoring_backend.repository;

import com.monitoring.monitoring_backend.entity.Metric;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MetricRepository extends JpaRepository<Metric,Long> {

    Optional<Metric> findFirstByServerIdOrderByTimestampDesc(Long serverId);
    List<Metric> findByServerIdOrderByTimestampAsc(Long serverId);
    List<Metric> findByServerIdAndTimestampAfterOrderByTimestampAsc(Long serverId, LocalDateTime cutoff);
}
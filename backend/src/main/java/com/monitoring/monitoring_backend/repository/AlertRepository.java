package com.monitoring.monitoring_backend.repository;

import com.monitoring.monitoring_backend.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlertRepository extends JpaRepository<Alert, Long> {

    Optional<Alert> findByServerIdAndTypeAndStatus(Long serverId, String type, String status);

    List<Alert> findByStatus(String status);
}
package com.monitoring.monitoring_backend.repository;

import com.monitoring.monitoring_backend.entity.Server;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ServerRepository extends JpaRepository<Server, Long> {
    Optional<Server> findByHostname(String hostname);
}
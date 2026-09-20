package com.monitoring.monitoring_backend.service;

import com.monitoring.monitoring_backend.entity.Alert;
import com.monitoring.monitoring_backend.entity.Metric;
import com.monitoring.monitoring_backend.entity.Server;
import com.monitoring.monitoring_backend.repository.AlertRepository;
import com.monitoring.monitoring_backend.repository.ServerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    @Mock
    private ServerRepository serverRepository;

    @Mock
    private ServerService serverService;

    @Mock
    private MetricService metricService;

    @Mock
    private AlertRepository alertRepository;

    @InjectMocks
    private AlertService alertService;

    private Server testServer;

    @BeforeEach
    void setUp() {
        testServer = new Server();
        testServer.setId(1L);
        testServer.setHostname("test-server");
        testServer.setStatus("UP");
    }

    @Test
    void whenServerIsDown_andNoActiveAlertExists_createsNewAlert() {
        when(serverRepository.findAll()).thenReturn(List.of(testServer));
        when(serverService.isAlive(1L)).thenReturn(false);
        when(alertRepository.findByServerIdAndTypeAndStatus(1L, "SERVER_DOWN", "ACTIVE"))
                .thenReturn(Optional.empty());
        when(metricService.getLatestMetric(1L)).thenReturn(Optional.empty());

        alertService.evaluateAlerts();

        verify(alertRepository, times(1)).save(argThat(alert ->
                alert.getType().equals("SERVER_DOWN") &&
                        alert.getStatus().equals("ACTIVE") &&
                        alert.getServer().equals(testServer)
        ));
    }

    @Test
    void whenServerIsDown_andActiveAlertAlreadyExists_doesNotCreateDuplicate() {
        Alert existingAlert = new Alert();
        existingAlert.setType("SERVER_DOWN");
        existingAlert.setStatus("ACTIVE");

        when(serverRepository.findAll()).thenReturn(List.of(testServer));
        when(serverService.isAlive(1L)).thenReturn(false);
        when(alertRepository.findByServerIdAndTypeAndStatus(1L, "SERVER_DOWN", "ACTIVE"))
                .thenReturn(Optional.of(existingAlert));
        when(metricService.getLatestMetric(1L)).thenReturn(Optional.empty());

        alertService.evaluateAlerts();

        verify(alertRepository, never()).save(any(Alert.class));
    }

    @Test
    void whenServerRecovers_resolvesExistingActiveAlert() {
        Alert existingAlert = new Alert();
        existingAlert.setType("SERVER_DOWN");
        existingAlert.setStatus("ACTIVE");

        when(serverRepository.findAll()).thenReturn(List.of(testServer));
        when(serverService.isAlive(1L)).thenReturn(true);
        when(alertRepository.findByServerIdAndTypeAndStatus(1L, "SERVER_DOWN", "ACTIVE"))
                .thenReturn(Optional.of(existingAlert));
        when(metricService.getLatestMetric(1L)).thenReturn(Optional.empty());

        alertService.evaluateAlerts();

        verify(alertRepository, times(1)).save(argThat(alert ->
                alert.getStatus().equals("RESOLVED") &&
                        alert.getResolvedAt() != null
        ));
    }

    @Test
    void whenCpuExceedsThreshold_createsCpuHighAlert() {
        Metric highCpuMetric = new Metric();
        highCpuMetric.setCpuPercent(95.0);
        highCpuMetric.setRamPercent(50.0);
        highCpuMetric.setDiskPercent(50.0);

        when(serverRepository.findAll()).thenReturn(List.of(testServer));
        when(serverService.isAlive(1L)).thenReturn(true);
        when(metricService.getLatestMetric(1L)).thenReturn(Optional.of(highCpuMetric));
        when(alertRepository.findByServerIdAndTypeAndStatus(eq(1L), anyString(), eq("ACTIVE")))
                .thenReturn(Optional.empty());

        alertService.evaluateAlerts();

        verify(alertRepository, times(1)).save(argThat(alert ->
                alert.getType().equals("CPU_HIGH")
        ));
    }
}
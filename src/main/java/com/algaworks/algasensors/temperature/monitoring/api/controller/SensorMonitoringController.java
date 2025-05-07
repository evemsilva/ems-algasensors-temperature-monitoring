package com.algaworks.algasensors.temperature.monitoring.api.controller;

import com.algaworks.algasensors.temperature.monitoring.api.model.SensorMonitoringOutput;
import com.algaworks.algasensors.temperature.monitoring.domain.model.SensorId;
import com.algaworks.algasensors.temperature.monitoring.domain.model.SensorMonitoring;
import com.algaworks.algasensors.temperature.monitoring.domain.repository.SensorMonitoringRepository;
import io.hypersistence.tsid.TSID;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@AllArgsConstructor
@RequestMapping("/api/sensors/{sensorId}/monitoring")
@RestController
public class SensorMonitoringController {

    private final SensorMonitoringRepository sensorMonitoringRepository;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public SensorMonitoringOutput findDetail(@PathVariable TSID sensorId) {
        SensorMonitoring sensorMonitoring = findOrReturnDefault(sensorId);

        return SensorMonitoringOutput.builder()
                .id(sensorMonitoring.getId().getValue())
                .lastTemperature(sensorMonitoring.getLastTemperature())
                .updatedAt(sensorMonitoring.getUpdatedAt())
                .enabled(sensorMonitoring.getEnabled())
                .build();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/enable")
    public void enable(@PathVariable TSID sensorId) {
        SensorMonitoring sensorMonitoring = findOrReturnDefault(sensorId);

        if (Boolean.TRUE.equals(sensorMonitoring.getEnabled())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        sensorMonitoring.setEnabled(true);
        sensorMonitoringRepository.saveAndFlush(sensorMonitoring);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/enable")
    public void disable(@PathVariable TSID sensorId) {
        SensorMonitoring sensorMonitoring = findOrReturnDefault(sensorId);
        sensorMonitoring.setEnabled(false);
        sensorMonitoringRepository.saveAndFlush(sensorMonitoring);
    }

    private SensorMonitoring findOrReturnDefault(TSID sensorId) {
        return sensorMonitoringRepository.findById(new SensorId(sensorId))
                .orElse(SensorMonitoring.builder()
                        .id(new SensorId(sensorId))
                        .enabled(false)
                        .build());
    }
}

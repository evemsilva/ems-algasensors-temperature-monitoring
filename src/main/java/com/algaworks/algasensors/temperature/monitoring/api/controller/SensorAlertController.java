package com.algaworks.algasensors.temperature.monitoring.api.controller;

import com.algaworks.algasensors.temperature.monitoring.api.model.SensorAlertInput;
import com.algaworks.algasensors.temperature.monitoring.api.model.SensorAlertOutput;
import com.algaworks.algasensors.temperature.monitoring.domain.model.SensorAlert;
import com.algaworks.algasensors.temperature.monitoring.domain.model.SensorId;
import com.algaworks.algasensors.temperature.monitoring.domain.repository.SensorAlertRepository;
import io.hypersistence.tsid.TSID;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@AllArgsConstructor
@RequestMapping("/api/sensors/{sensorId}/alert")
@RestController
public class SensorAlertController {

    private final SensorAlertRepository sensorAlertRepository;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public SensorAlertOutput find(@PathVariable TSID sensorId) {
        SensorAlert sensorAlert = findSensorAlertById(sensorId);

        return SensorAlertOutput.builder()
                .id(sensorAlert.getId().getValue())
                .minTemperature(sensorAlert.getMinTemperature())
                .maxTemperature(sensorAlert.getMaxTemperature())
                .build();
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public SensorAlertOutput save(@PathVariable TSID sensorId, @RequestBody SensorAlertInput input) {

        SensorAlert sensorAlert = SensorAlert.builder()
                .id(new SensorId(sensorId))
                .minTemperature(input.getMinTemperature())
                .maxTemperature(input.getMaxTemperature())
                .build();

        sensorAlertRepository.saveAndFlush(sensorAlert);

        return SensorAlertOutput.builder()
                .id(sensorAlert.getId().getValue())
                .minTemperature(sensorAlert.getMinTemperature())
                .maxTemperature(sensorAlert.getMaxTemperature())
                .build();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping
    public void remove(@PathVariable TSID sensorId) {
        SensorAlert sensorAlert = findSensorAlertById(sensorId);
        sensorAlertRepository.delete(sensorAlert);
    }

    private SensorAlert findSensorAlertById(TSID sensorId) {
        return sensorAlertRepository.findById(new SensorId(sensorId)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}

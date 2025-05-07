package com.algaworks.algasensors.temperature.monitoring.api.controller;

import com.algaworks.algasensors.temperature.monitoring.api.model.TemperatureLogData;
import com.algaworks.algasensors.temperature.monitoring.domain.model.SensorId;
import com.algaworks.algasensors.temperature.monitoring.domain.model.TemperatureLog;
import com.algaworks.algasensors.temperature.monitoring.domain.repository.TemperatureLogRepository;
import io.hypersistence.tsid.TSID;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RequestMapping("/api/sensors/{sensorId}/temperatures")
@RestController
public class TemperatureLogController {

    private final TemperatureLogRepository temperatureLogRepository;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<TemperatureLogData> search(@PathVariable TSID sensorId, @PageableDefault Pageable page) {
        Page<TemperatureLog> temperatureLogs = temperatureLogRepository.findAllBySensorId(new SensorId(sensorId), page);
        return temperatureLogs.map(s ->
                                 TemperatureLogData.builder()
                                .id(s.getId().getValue())
                                .sensorId(s.getSensorId().getValue())
                                .registeredAt(s.getRegisteredAt())
                                .value(s.getValue())
                                .build()
                );
    }

}

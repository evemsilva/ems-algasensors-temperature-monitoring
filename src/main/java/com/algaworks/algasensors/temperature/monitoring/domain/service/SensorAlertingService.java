package com.algaworks.algasensors.temperature.monitoring.domain.service;

import com.algaworks.algasensors.temperature.monitoring.api.model.TemperatureLogData;
import com.algaworks.algasensors.temperature.monitoring.domain.model.SensorAlert;
import com.algaworks.algasensors.temperature.monitoring.domain.model.SensorId;
import com.algaworks.algasensors.temperature.monitoring.domain.repository.SensorAlertRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class SensorAlertingService {

    private final SensorAlertRepository sensorAlertRepository;

    @Transactional
    public void handleAlert(TemperatureLogData temperatureLogData) {
        sensorAlertRepository.findById(new SensorId(temperatureLogData.getSensorId()))
                .ifPresentOrElse(sensor -> handle(temperatureLogData, sensor), () -> logIgnoredSensor(temperatureLogData));

    }

    private void handle(TemperatureLogData temperatureLogData, SensorAlert sensorAlert) {
        if (temperatureLogData.getValue().compareTo(sensorAlert.getMinTemperature())  <= 0) {
            log.info("Alert min temperature, SensorID: {}, {}°C", temperatureLogData.getSensorId().toString(), temperatureLogData.getValue());
        } else if (temperatureLogData.getValue().compareTo(sensorAlert.getMaxTemperature())  >= 0) {
            log.info("Alert max temperature, SensorID: {}, {}°C", temperatureLogData.getSensorId().toString(), temperatureLogData.getValue());
        } else {
            logIgnoredSensor(temperatureLogData);
        }
    }

    private void logIgnoredSensor(TemperatureLogData temperatureLogData) {
        log.info("SensorID {}, {}°C ignored", temperatureLogData.getSensorId(), temperatureLogData.getValue());
    }

}

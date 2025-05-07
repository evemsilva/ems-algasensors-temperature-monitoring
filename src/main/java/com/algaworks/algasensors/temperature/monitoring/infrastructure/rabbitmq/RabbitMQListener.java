package com.algaworks.algasensors.temperature.monitoring.infrastructure.rabbitmq;

import com.algaworks.algasensors.temperature.monitoring.api.model.TemperatureLogData;
import com.algaworks.algasensors.temperature.monitoring.domain.model.SensorId;
import com.algaworks.algasensors.temperature.monitoring.domain.model.SensorMonitoring;
import com.algaworks.algasensors.temperature.monitoring.domain.model.TemperatureLog;
import com.algaworks.algasensors.temperature.monitoring.domain.model.TemperatureLogId;
import com.algaworks.algasensors.temperature.monitoring.domain.repository.SensorMonitoringRepository;
import com.algaworks.algasensors.temperature.monitoring.domain.repository.TemperatureLogRepository;
import com.algaworks.algasensors.temperature.monitoring.domain.service.SensorAlertingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;


@Slf4j
@RequiredArgsConstructor
@Component
public class RabbitMQListener {

    private final SensorMonitoringRepository sensorMonitoringRepository;
    private final TemperatureLogRepository temperatureLogRepository;
    private final SensorAlertingService sensorAlertingService;

    @RabbitListener(queues = RabbitMQConfiguration.QUEUE_PROCESS_TEMPERATURE, concurrency = "2-3")
    public void handleProcessTemperature(TemperatureLogData temperatureLogData) {
        sensorMonitoringRepository.findById(new SensorId(temperatureLogData.getSensorId()))
                .ifPresentOrElse(sensor -> handleSensorMonitoring(temperatureLogData, sensor), () -> log.warn("Sensor {} not found!", temperatureLogData.getSensorId().toString()));
    }

    @RabbitListener(queues = RabbitMQConfiguration.QUEUE_ALERTING, concurrency = "2-3")
    public void handleAlerting(TemperatureLogData temperatureLogData) {
        sensorAlertingService.handleAlert(temperatureLogData);
    }

    private void handleSensorMonitoring(TemperatureLogData temperatureLogData, SensorMonitoring sensor) {
        if (sensor.isEnabled()) {
            sensor.setLastTemperature(temperatureLogData.getValue().longValue());
            sensor.setUpdatedAt(OffsetDateTime.now());
            sensorMonitoringRepository.saveAndFlush(sensor);

            TemperatureLog temperatureLog = TemperatureLog.builder()
                    .id(new TemperatureLogId(temperatureLogData.getId()))
                    .sensorId(new SensorId(temperatureLogData.getSensorId()))
                    .value(temperatureLogData.getValue())
                    .registeredAt(temperatureLogData.getRegisteredAt())
                    .build();

            temperatureLogRepository.saveAndFlush(temperatureLog);

            log.info("Current temperature updated, SensorID={}", temperatureLog.getSensorId().toString());

        } else {
            log.warn("Sensor {} is disabled!", sensor.getId().toString());
        }
    }
}

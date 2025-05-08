package com.algaworks.algasensors.temperature.monitoring.infrastructure.rabbitmq;

import com.algaworks.algasensors.temperature.monitoring.api.model.TemperatureLogData;
import com.algaworks.algasensors.temperature.monitoring.domain.service.SensorAlertingService;
import com.algaworks.algasensors.temperature.monitoring.domain.service.TemperatureMonitoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class RabbitMQListener {

    private final SensorAlertingService sensorAlertingService;
    private final TemperatureMonitoringService temperatureMonitoringService;

    @RabbitListener(queues = RabbitMQConfiguration.QUEUE_PROCESS_TEMPERATURE, concurrency = "2-3")
    public void handleProcessTemperature(TemperatureLogData temperatureLogData) {
        temperatureMonitoringService.processTemperatureReading(temperatureLogData);
    }

    @RabbitListener(queues = RabbitMQConfiguration.QUEUE_ALERTING, concurrency = "2-3")
    public void handleAlerting(TemperatureLogData temperatureLogData) {
        sensorAlertingService.handleAlert(temperatureLogData);
    }
}

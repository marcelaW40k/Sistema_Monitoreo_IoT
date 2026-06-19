package com.sistemaMonitoreo.backend.service;

import com.sistemaMonitoreo.backend.model.Alert;
import com.sistemaMonitoreo.backend.model.Sensor;
import com.sistemaMonitoreo.backend.model.Vehicle;
import com.sistemaMonitoreo.backend.repository.AlertRepository;
import com.sistemaMonitoreo.backend.repository.SensorRepository;
import com.sistemaMonitoreo.backend.repository.VehicleRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FleetService {

    private final VehicleRepository vehicleRepository;
    private final SensorRepository sensorRepository;
    private final AlertRepository alertRepository;
    // Componente de Spring para enviar datos selectivos a través de WebSockets
    private final SimpMessagingTemplate messagingTemplate;

    public FleetService(VehicleRepository vehicleRepository,
                        SensorRepository sensorRepository,
                        AlertRepository alertRepository,
                        SimpMessagingTemplate messagingTemplate) {
        this.vehicleRepository = vehicleRepository;
        this.sensorRepository = sensorRepository;
        this.alertRepository = alertRepository;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Procesa la ingesta de telemetría IoT de un vehículo.
     */
    @Transactional
    public Sensor processTelemetry(String deviceId, Sensor incomingData) {
        // 1. Validar que el vehículo exista en el sistema
        Vehicle vehicle = vehicleRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new RuntimeException("Dispositivo IoT no registrado en la flota."));

        incomingData.setVehicle(vehicle);
        incomingData.setTimestamp(LocalDateTime.now());

        //Guardar el registro histórico en PostgreSQL
        Sensor savedData = sensorRepository.save(incomingData);
        checkFuelPrediction(vehicle, savedData);
        messagingTemplate.convertAndSend("/topic/telemetry/" + vehicle.getId(), savedData);
        messagingTemplate.convertAndSend("/topic/fleet-status", savedData);

        return savedData;
    }

    /**
     * Algoritmo Predictivo: Evalúa si el vehículo se quedará sin combustible en menos de 1 hora.
     */
    private void checkFuelPrediction(Vehicle vehicle, Sensor currentData) {
        if (currentData.getCurrentConsumption() > 0) {
            double hoursRemaining = currentData.getFuelLevel() / currentData.getCurrentConsumption();

            if (hoursRemaining < 1.0) { // Menos de 1 hora de autonomía
                String alertMessage = String.format(
                        "ALERTA PREDICTIVA: Al vehículo con placa %s le quedan %.2f minutos de autonomia.",
                        vehicle.getPlate(), (hoursRemaining * 60)
                );

                // Crear y persistir la alerta en la base de datos
                Alert alert = Alert.builder()
                        .vehicle(vehicle)
                        .type("PREDICTIVE_FUEL")
                        .message(alertMessage)
                        .timestamp(LocalDateTime.now())
                        .isResolved(false)
                        .build();

                alertRepository.save(alert);

                // Notificar de inmediato por WebSocket al canal exclusivo de alertas
                messagingTemplate.convertAndSend("/topic/alerts", alert);
            }
        }
    }

    public List<Alert> getActiveAlerts() {
        return alertRepository.findByIsResolvedFalseOrderByTimestampDesc();
    }
}

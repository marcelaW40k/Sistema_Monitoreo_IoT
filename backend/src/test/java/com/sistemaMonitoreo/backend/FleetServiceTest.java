package com.sistemaMonitoreo.backend;

import com.sistemaMonitoreo.backend.model.Alert;
import com.sistemaMonitoreo.backend.model.Sensor;
import com.sistemaMonitoreo.backend.model.Vehicle;
import com.sistemaMonitoreo.backend.repository.AlertRepository;
import com.sistemaMonitoreo.backend.repository.SensorRepository;
import com.sistemaMonitoreo.backend.repository.VehicleRepository;
import com.sistemaMonitoreo.backend.service.FleetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class FleetServiceTest {

    private VehicleRepository vehicleRepository;
    private SensorRepository sensorRepository;
    private AlertRepository alertRepository;
    private SimpMessagingTemplate messagingTemplate;
    private FleetService fleetService;

    @BeforeEach
    void setUp() {
        // Inicializamos los mocks simulados de los repositorios
        vehicleRepository = mock(VehicleRepository.class);
        sensorRepository = mock(SensorRepository.class);
        alertRepository = mock(AlertRepository.class);
        messagingTemplate = mock(SimpMessagingTemplate.class);

        fleetService = new FleetService(vehicleRepository, sensorRepository, alertRepository, messagingTemplate);
    }

    @Test
    void cuandoCombustibleBajaDeUnaHora_DebeCrearYEnviarAlerta() {
        String mockDeviceId = "DEV-1234-XC54";
        Vehicle mockVehicle = Vehicle.builder().id(1L).plate("ABC-123").deviceId(mockDeviceId).build();

        // Nivel: 10% de combustible, Consumo: 15% por hora -> Autonomía = 10/15 = 0.66 horas (Menor a 1 hora)
        Sensor incomingTelemetry = Sensor.builder()
                .fuelLevel(10.0)
                .currentConsumption(15.0)
                .latitude(4.6097)
                .longitude(-74.0817)
                .speed(45.0)
                .temperature(90.0)
                .build();

        when(vehicleRepository.findByDeviceId(mockDeviceId)).thenReturn(Optional.of(mockVehicle));
        when(sensorRepository.save(any(Sensor.class))).thenReturn(incomingTelemetry);

        // EJECUCIÓN (When)
        fleetService.processTelemetry(mockDeviceId, incomingTelemetry);

        // VERIFICACIÓN (Then)
        // Comprobar que se guardó una alerta en la DB porque la autonomía era crítica
        verify(alertRepository, times(1)).save(any(Alert.class));

        // Comprobar que se intentó notificar por el WebSocket a los administradores
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/alerts"), any(Alert.class));
    }
}
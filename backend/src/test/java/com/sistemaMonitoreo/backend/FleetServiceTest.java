package com.sistemaMonitoreo.backend;

import com.sistemaMonitoreo.backend.model.Alert;
import com.sistemaMonitoreo.backend.model.Sensor;
import com.sistemaMonitoreo.backend.model.Vehicle;
import com.sistemaMonitoreo.backend.repository.AlertRepository;
import com.sistemaMonitoreo.backend.repository.SensorRepository;
import com.sistemaMonitoreo.backend.repository.VehicleRepository;
import com.sistemaMonitoreo.backend.service.FleetService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Inicializa los @Mock automáticamente y limpia la memoria
public class FleetServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private SensorRepository sensorRepository;

    @Mock
    private AlertRepository alertRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks // Crea automáticamente la instancia de FleetService inyectando los mocks de arriba
    private FleetService fleetService;

    @Test
    @DisplayName("Debería generar y transmitir alerta WebSocket cuando la autonomía calculada es menor a una hora")
    void cuandoCombustibleBajaDeUnaHora_DebeCrearYEnviarAlerta() {
        String mockDeviceId = "DEV-1234-XC54";
        Vehicle mockVehicle = Vehicle.builder().id(1L).plate("ABC-123").deviceId(mockDeviceId).build();

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

        // EJECUCIÓN
        fleetService.processTelemetry(mockDeviceId, incomingTelemetry);

        // VERIFICACIÓN
        verify(alertRepository, times(1)).save(any(Alert.class));
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/alerts"), any(Alert.class));
    }
}
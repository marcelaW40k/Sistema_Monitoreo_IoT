    package com.sistemaMonitoreo.backend.controller;

    import com.sistemaMonitoreo.backend.DTO.SensorResponseDTO;
    import com.sistemaMonitoreo.backend.DTO.VehicleResponseDTO;
    import com.sistemaMonitoreo.backend.model.Alert;
    import com.sistemaMonitoreo.backend.model.Sensor;
    import com.sistemaMonitoreo.backend.model.Vehicle;
    import com.sistemaMonitoreo.backend.repository.SensorRepository;
    import com.sistemaMonitoreo.backend.repository.VehicleRepository;
    import com.sistemaMonitoreo.backend.service.FleetService;
    import jakarta.servlet.http.HttpServletRequest;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;
    import java.util.stream.Collectors;

    @RestController
    @RequestMapping("/api/fleet")
    public class FleetController {

        private final FleetService fleetService;
        private final VehicleRepository vehicleRepository;
        private final SensorRepository sensorRepository;

        public FleetController(FleetService fleetService, VehicleRepository vehicleRepository, SensorRepository sensorRepository) {
            this.fleetService = fleetService;
            this.vehicleRepository = vehicleRepository;
            this.sensorRepository = sensorRepository;
        }

        /**
         * Endpoint para simular o recibir datos de sensores desde los dispositivos IoT.
         * Acceso: Cualquier usuario o dispositivo autenticado.
         */
        @PostMapping("/telemetry/{deviceId}")
        public ResponseEntity<Sensor> receiveTelemetry(
                @PathVariable String deviceId,
                @RequestBody Sensor data) {
            return ResponseEntity.ok(fleetService.processTelemetry(deviceId, data));
        }

        /**
         * Obtiene la lista de vehículos aplicando el por Rol.
         */
        @GetMapping("/vehicles")
        public ResponseEntity<List<VehicleResponseDTO>> getAllVehicles(HttpServletRequest request) {
            // Recuperamos el rol inyectado previamente por nuestro JwtInterceptor
            String userRole = (String) request.getAttribute("userRole");

            List<Vehicle> vehicles = vehicleRepository.findAll();

            // Mapeamos a DTO evaluando la privacidad de cada registro
            List<VehicleResponseDTO> dtoList = vehicles.stream()
                    .map(v -> VehicleResponseDTO.fromEntity(v, userRole))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(dtoList);
        }

        /**
         * Endpoint exclusivo para alertas visibles.
         * Nota: Nuestro JwtInterceptor bloqueará automáticamente el acceso a esta ruta
         * si la URI contiene '/admin/' y el rol no es 'ADMIN'.
         */
        @GetMapping("/admin/alerts")
        public ResponseEntity<List<Alert>> getActiveAlerts() {
            return ResponseEntity.ok(fleetService.getActiveAlerts());
        }

        /**
         * MAPA EN VIVO: Obtiene la última lectura de sensor de cada vehículo.
         * Proporciona las coordenadas en tiempo real para pintar los marcadores.
         */
        @GetMapping("/telemetry/live-locations")
        public ResponseEntity<List<SensorResponseDTO>> getLiveLocations(HttpServletRequest request) {
            String userRole = (String) request.getAttribute("userRole");
            List<SensorResponseDTO> secureLocations = fleetService.getLiveLocations(userRole);

            return ResponseEntity.ok(secureLocations);
        }

        /**
         * HISTÓRICO EXCLUSIVO PARA GRÁFICAS: Obtiene los reportes previos por el ID de BD del Vehículo.
         * Retorna una lista de sensores que contiene velocidad, combustible y timestamps.
         * Acceso: USER y ADMIN.
         */
        @GetMapping("/vehicles/{vehicleId}/history")
        public ResponseEntity<List<Sensor>> getVehicleHistory(@PathVariable Long vehicleId) {
            List<Sensor> history = fleetService.getVehicleHistory(vehicleId);
            return ResponseEntity.ok(history);
        }

    }
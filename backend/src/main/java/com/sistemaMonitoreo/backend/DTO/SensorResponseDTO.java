package com.sistemaMonitoreo.backend.DTO;


import com.sistemaMonitoreo.backend.model.Sensor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SensorResponseDTO {
    private Long id;
    private Double latitude;
    private Double longitude;
    private Double fuelLevel;
    private Double speed;
    private Double temperature;
    private LocalDateTime timestamp;
    private VehicleResponseDTO vehicle;

    public static SensorResponseDTO fromEntity(Sensor sensor, String userRole) {
        SensorResponseDTO dto = new SensorResponseDTO();
        dto.setId(sensor.getId());
        dto.setLatitude(sensor.getLatitude());
        dto.setLongitude(sensor.getLongitude());
        dto.setFuelLevel(sensor.getFuelLevel());
        dto.setSpeed(sensor.getSpeed());
        dto.setTemperature(sensor.getTemperature());
        dto.setTimestamp(sensor.getTimestamp());

        if (sensor.getVehicle() != null) {
            dto.setVehicle(VehicleResponseDTO.fromEntity(sensor.getVehicle(), userRole));
        }

        return dto;
    }
}
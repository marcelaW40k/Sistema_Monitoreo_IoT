package com.sistemaMonitoreo.backend.DTO;

import com.sistemaMonitoreo.backend.model.Vehicle;
import lombok.Data;

@Data
public class VehicleResponseDTO {
    private Long id;
    private String plate;
    private String model;
    private String deviceId;

    public static VehicleResponseDTO fromEntity(Vehicle vehicle, String userRole) {
        VehicleResponseDTO dto = new VehicleResponseDTO();
        dto.setId(vehicle.getId());
        dto.setPlate(vehicle.getPlate());
        dto.setModel(vehicle.getModel());

        if ("ADMIN".equalsIgnoreCase(userRole)) {
            dto.setDeviceId(vehicle.getDeviceId());
        } else {
            dto.setDeviceId(maskDeviceId(vehicle.getDeviceId()));
        }
        return dto;
    }

    private static String maskDeviceId(String originalId) {
        if (originalId == null || originalId.length() < 8) {
            return "DEV-****-XXXX";
        }
        // Transforma un ID tipo "DEV-1234-XC54" a "DEV-****-XC54"
        return originalId.substring(0, 4) + "****" + originalId.substring(originalId.length() - 5);
    }
}

package com.sistemaMonitoreo.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String plate; // Patente/Placa del vehículo

    @Column(nullable = false)
    private String model;

    // Un vehículo contiene un dispositivo IoT. Aquí guardamos su ID de hardware.
    @Column(name = "device_id", unique = true, nullable = false)
    private String deviceId;
}
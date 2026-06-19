package com.sistemaMonitoreo.backend.repository;


import com.sistemaMonitoreo.backend.model.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long> {
    Optional<Sensor> findFirstByVehicleIdOrderByTimestampDesc(Long vehicleId);
}
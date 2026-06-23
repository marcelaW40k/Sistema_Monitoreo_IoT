package com.sistemaMonitoreo.backend.repository;


import com.sistemaMonitoreo.backend.model.Sensor;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long> {
    // Esta consulta busca el último sensor registrado para cada vehículo
    @Query("SELECT s FROM Sensor s WHERE s.timestamp = " +
            "(SELECT MAX(s2.timestamp) FROM Sensor s2 WHERE s2.vehicle = s.vehicle)")
    List<Sensor> findLatestTelemetryPerVehicle();

    // Sigue retornando List<Sensor>, por lo que tendrás velocidad y combustible perfectos en React
    @Query(value = "SELECT * FROM sensores WHERE vehicle_id = :vehicleId ORDER BY timestamp DESC LIMIT 20", nativeQuery = true)
    List<Sensor> findTop20ByVehicleIdOrderByTimestampDesc(@Param("vehicleId") Long vehicleId);
}
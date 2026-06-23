package com.sistemaMonitoreo.backend.repository;

import com.sistemaMonitoreo.backend.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface  AlertRepository extends JpaRepository<Alert, Long> {
    // Devuelve las alertas activas (no resueltas)
    List<Alert> findByIsResolvedFalseOrderByTimestampDesc();
}
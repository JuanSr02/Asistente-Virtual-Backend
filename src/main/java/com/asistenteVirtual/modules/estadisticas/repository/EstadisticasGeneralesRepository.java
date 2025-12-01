package com.asistenteVirtual.modules.estadisticas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.asistenteVirtual.modules.estadisticas.model.EstadisticasGenerales;

import java.util.Optional;

@Repository
public interface EstadisticasGeneralesRepository extends JpaRepository<EstadisticasGenerales, Long> {
    // Obtiene las últimas estadísticas generales
    Optional<EstadisticasGenerales> findFirstByOrderByFechaUltimaActualizacionDesc();
}

package com.asistenteVirtual.repository;

import com.asistenteVirtual.model.EstadisticasGenerales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstadisticasGeneralesRepository extends JpaRepository<EstadisticasGenerales, Long> {
    // Obtiene las últimas estadísticas generales
    Optional<EstadisticasGenerales> findFirstByOrderByFechaUltimaActualizacionDesc();
}

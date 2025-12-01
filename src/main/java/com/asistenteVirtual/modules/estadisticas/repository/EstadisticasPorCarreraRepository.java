package com.asistenteVirtual.modules.estadisticas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.asistenteVirtual.modules.estadisticas.model.EstadisticasPorCarrera;

import java.util.Optional;

public interface EstadisticasPorCarreraRepository extends JpaRepository<EstadisticasPorCarrera, Long> {
    Optional<EstadisticasPorCarrera> findByCodigoPlanAndPeriodo(
            String codigoPlan, String periodo);

    void deleteByCodigoPlanAndPeriodo(String codigoPlan, String periodo);
}
package com.asistenteVirtual.repository;

import com.asistenteVirtual.model.EstadisticasPorCarrera;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstadisticasPorCarreraRepository extends JpaRepository<EstadisticasPorCarrera, Long> {
    Optional<EstadisticasPorCarrera> findByCodigoPlanAndPeriodo(
            String codigoPlan, String periodo);

    void deleteByCodigoPlanAndPeriodo(String codigoPlan, String periodo);
}
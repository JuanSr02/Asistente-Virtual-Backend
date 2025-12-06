package com.asistenteVirtual.modules.estadisticas.repository;

import com.asistenteVirtual.modules.estadisticas.model.EstadisticasMateria;
import com.asistenteVirtual.modules.estadisticas.model.EstadisticasMateriaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstadisticasMateriaRepository extends JpaRepository<EstadisticasMateria, EstadisticasMateriaId> {

    // Metodo helper para buscar usando los tipos nativos
    Optional<EstadisticasMateria> findByCodigoMateriaAndPeriodo(String codigoMateria, String periodo);

    List<EstadisticasMateria> findByPeriodo(String periodo);
}
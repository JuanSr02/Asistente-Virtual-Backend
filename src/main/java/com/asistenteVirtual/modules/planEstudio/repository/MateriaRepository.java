package com.asistenteVirtual.modules.planEstudio.repository;

import com.asistenteVirtual.modules.planEstudio.model.Materia;
import com.asistenteVirtual.modules.planEstudio.model.PlanDeEstudio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MateriaRepository extends JpaRepository<Materia, String> {

    // ✅ ESTE ES EL QUE FALTABA: Búsqueda por códigos (Strings) directa
    // Spring Data interpreta el "_" como navegación a la propiedad 'codigo' del objeto 'planDeEstudio'
    Optional<Materia> findByCodigoAndPlanDeEstudio_Codigo(String codigo, String planCodigo);

    // Métodos existentes que conservamos por compatibilidad o uso interno
    Optional<Materia> findByCodigoAndPlanDeEstudio(String codigo, PlanDeEstudio planDeEstudio);

    List<Materia> findByCodigo(String codigo);

    @Query("SELECT COUNT(m) FROM Materia m WHERE m.planDeEstudio.codigo = :codigoPlan")
    Long ContarByPlanCodigo(@Param("codigoPlan") String codigoPlan);

    List<Materia> findByPlanDeEstudio_Codigo(String codigoPlan);

    // Helper para obtener nombre rápido (usado en estadísticas)
    @Query("SELECT m.nombre FROM Materia m WHERE m.codigo = :codigo ORDER BY m.nombre ASC LIMIT 1")
    String findFirstNombreByCodigo(@Param("codigo") String codigo);
}
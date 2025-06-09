package com.recommendationSys.Sistema_Recomendador_Finales.repository;

import com.recommendationSys.Sistema_Recomendador_Finales.model.Materia;
import com.recommendationSys.Sistema_Recomendador_Finales.model.PlanDeEstudio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MateriaRepository extends JpaRepository<Materia, String> {

    Optional<Materia> findByNombreAndPlanDeEstudio(String nombre, PlanDeEstudio plan);
    Optional<Materia> findByCodigoAndPlanDeEstudio(String codigo, PlanDeEstudio planDeEstudio);
    Optional<Materia> findFirstByCodigo(String codigo);
    List<Materia> findByCodigo(String codigo);
    @Query("""
    SELECT m.nombre
    FROM Materia m
    WHERE m.codigo = :codigo
    ORDER BY m.nombre ASC
    LIMIT 1
""")
    String findFirstNombreByCodigo(@Param("codigo") String codigo);
    @Query("SELECT COUNT(m) FROM Materia m WHERE m.planDeEstudio.codigo = :codigoPlan")
    Long ContarByPlanCodigo(@Param("codigoPlan") String codigoPlan);

    List<Materia> findByPlanDeEstudio_Codigo(String codigoPlan);

}


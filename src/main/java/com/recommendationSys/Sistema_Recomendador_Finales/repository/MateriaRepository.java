package com.recommendationSys.Sistema_Recomendador_Finales.repository;

import com.recommendationSys.Sistema_Recomendador_Finales.model.Materia;
import com.recommendationSys.Sistema_Recomendador_Finales.model.PlanDeEstudio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MateriaRepository extends JpaRepository<Materia, String> {

    Optional<Materia> findByNombreAndPlanDeEstudio(String nombre, PlanDeEstudio plan);
    @Query("SELECT m FROM Materia m WHERE m.codigo = :codigo")
    Optional<Materia> findByCodigo(@Param ("codigo") String codigo);
    Optional<Materia> findByCodigoAndPlanDeEstudio(String codigo, PlanDeEstudio planDeEstudio);

}


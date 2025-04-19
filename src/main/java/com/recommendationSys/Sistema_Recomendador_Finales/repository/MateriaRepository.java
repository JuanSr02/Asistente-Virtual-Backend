package com.recommendationSys.Sistema_Recomendador_Finales.repository;

import com.recommendationSys.Sistema_Recomendador_Finales.model.Materia;
import com.recommendationSys.Sistema_Recomendador_Finales.model.PlanDeEstudio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MateriaRepository extends JpaRepository<Materia, String> {

    // Find by name containing
    List<Materia> findByNombreContainingIgnoreCase(String nombre);

    // Find by study plan
    List<Materia> findByPlanDeEstudio(PlanDeEstudio planDeEstudio);

    // Find by study plan code
    List<Materia> findByPlanDeEstudioCodigo(String codigoPlan);

    // Find subjects with correlatives
    List<Materia> findByCorrelativasIsNotEmpty();

    // Find subjects that are correlatives of others
    List<Materia> findByEsCorrelavitaDeIsNotEmpty();

    // Find subjects with registrations
    List<Materia> findByInscripcionesIsNotEmpty();

    // Find subjects with academic records
    List<Materia> findByRenglonesIsNotEmpty();

    // Find subjects without correlatives
    List<Materia> findByCorrelativasIsEmpty();

    // Count subjects by study plan
    Long countByPlanDeEstudio(PlanDeEstudio planDeEstudio);

    // Find subjects that have a minimum number of correlatives
    @Query("SELECT m FROM Materia m WHERE SIZE(m.correlativas) >= :count")
    List<Materia> findByCorrelativasCountGreaterThanEqual(@Param("count") Integer count);

    // Find subjects that are correlatives for a minimum number of other subjects
    @Query("SELECT m FROM Materia m WHERE SIZE(m.esCorrelavitaDe) >= :count")
    List<Materia> findByEsCorrelavitaDeCountGreaterThanEqual(@Param("count") Integer count);
}


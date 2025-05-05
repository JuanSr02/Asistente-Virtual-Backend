package com.recommendationSys.Sistema_Recomendador_Finales.repository;

import com.recommendationSys.Sistema_Recomendador_Finales.model.Materia;
import com.recommendationSys.Sistema_Recomendador_Finales.model.PlanDeEstudio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MateriaRepository extends JpaRepository<Materia, String> {

    //find by ID
    Materia findByCodigo(String codigo);

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

    Optional<Materia> findByNombreAndPlanDeEstudio(String nombre, PlanDeEstudio plan);
}


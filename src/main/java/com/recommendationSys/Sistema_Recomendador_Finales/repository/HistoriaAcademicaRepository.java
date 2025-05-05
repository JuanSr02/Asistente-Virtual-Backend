package com.recommendationSys.Sistema_Recomendador_Finales.repository;

import com.recommendationSys.Sistema_Recomendador_Finales.model.Estudiante;
import com.recommendationSys.Sistema_Recomendador_Finales.model.HistoriaAcademica;
import com.recommendationSys.Sistema_Recomendador_Finales.model.PlanDeEstudio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistoriaAcademicaRepository extends JpaRepository<HistoriaAcademica, Long> {

    // Find by student
    Optional<HistoriaAcademica> findByEstudiante(Estudiante estudiante);

    // Find by study plan
    List<HistoriaAcademica> findByPlanDeEstudio(PlanDeEstudio planDeEstudio);

    // Find by student and study plan
    Optional<HistoriaAcademica> findByEstudianteAndPlanDeEstudio(Estudiante estudiante, PlanDeEstudio planDeEstudio);

    // Find academic histories with no records (renglones)
    List<HistoriaAcademica> findByRenglonesIsEmpty();

    // Count academic histories by study plan
    Long countByPlanDeEstudio(PlanDeEstudio planDeEstudio);

}

package com.recommendationSys.Sistema_Recomendador_Finales.repository;

import com.recommendationSys.Sistema_Recomendador_Finales.model.Estudiante;
import com.recommendationSys.Sistema_Recomendador_Finales.model.HistoriaAcademica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HistoriaAcademicaRepository extends JpaRepository<HistoriaAcademica, Long> {

    // Find by student
    Optional<HistoriaAcademica> findByEstudiante(Estudiante estudiante);

    boolean existsByEstudiante(Estudiante estudiante);

}

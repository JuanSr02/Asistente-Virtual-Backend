package com.asistenteVirtual.repository;

import com.asistenteVirtual.model.HistoriaAcademica;
import com.asistenteVirtual.modules.estudiante.model.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistoriaAcademicaRepository extends JpaRepository<HistoriaAcademica, Long> {

    // Find by student
    Optional<HistoriaAcademica> findByEstudiante(Estudiante estudiante);

    boolean existsByEstudiante(Estudiante estudiante);

    List<HistoriaAcademica> findByPlanDeEstudio_Codigo(String codigoPlan);


}

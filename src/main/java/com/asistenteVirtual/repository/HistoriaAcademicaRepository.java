package com.asistenteVirtual.repository;

import com.asistenteVirtual.model.Estudiante;
import com.asistenteVirtual.model.HistoriaAcademica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HistoriaAcademicaRepository extends JpaRepository<HistoriaAcademica, Long> {

    // Find by student
    Optional<HistoriaAcademica> findByEstudiante(Estudiante estudiante);

    boolean existsByEstudiante(Estudiante estudiante);

}

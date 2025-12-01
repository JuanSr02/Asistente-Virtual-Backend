package com.asistenteVirtual.modules.historiaAcademica.repository;

import com.asistenteVirtual.modules.estudiante.model.Estudiante;
import com.asistenteVirtual.modules.historiaAcademica.model.HistoriaAcademica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistoriaAcademicaRepository extends JpaRepository<HistoriaAcademica, Long> {

    Optional<HistoriaAcademica> findByEstudiante(Estudiante estudiante);

    Optional<HistoriaAcademica> findByEstudiante_Id(Long estudianteId);

    Optional<HistoriaAcademica> findByEstudiante_IdAndEstado(Long estudianteId, String estado);

    boolean existsByEstudiante(Estudiante estudiante);

    List<HistoriaAcademica> findByPlanDeEstudio_Codigo(String codigoPlan);
}
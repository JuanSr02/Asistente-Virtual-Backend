package com.asistenteVirtual.modules.experiencia.repository;

import com.asistenteVirtual.modules.experiencia.model.Experiencia;
import com.asistenteVirtual.modules.planEstudio.model.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExperienciaRepository extends JpaRepository<Experiencia, Long> {

    boolean existsByExamen_Id(Long examenId);

    // Consultas básicas con Fetch para evitar N+1 en listados
    @Query("""
                SELECT e FROM Experiencia e
                JOIN FETCH e.examen ex
                JOIN FETCH ex.renglon r
                JOIN FETCH r.materia m
                WHERE m.codigo = :codigoMateria
            """)
    List<Experiencia> findAllByCodigoMateria(@Param("codigoMateria") String codigoMateria);

    @Query("""
                SELECT e FROM Experiencia e
                JOIN FETCH e.examen ex
                JOIN FETCH ex.renglon r
                JOIN FETCH r.materia
                WHERE r.historiaAcademica.estudiante.id = :estudianteId
            """)
    List<Experiencia> findAllByEstudianteId(@Param("estudianteId") Long estudianteId);

    // ✅ LOS QUE FALTABAN (Usados en EstadisticasMateriaPeriodoService)

    @Query("""
                SELECT e FROM Experiencia e
                JOIN FETCH e.examen ex
                JOIN FETCH ex.renglon r
                WHERE r.materia = :materia
                AND ex.fecha >= :fechaLimite
            """)
    List<Experiencia> findByMateriaAndFechaAfter(@Param("materia") Materia materia,
                                                 @Param("fechaLimite") LocalDate fechaLimite);

    @Query("""
                SELECT e FROM Experiencia e
                JOIN FETCH e.examen ex
                JOIN FETCH ex.renglon r
                WHERE r.materia = :materia
            """)
    List<Experiencia> findByMateriaWithJoins(@Param("materia") Materia materia);
}
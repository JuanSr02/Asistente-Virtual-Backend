package com.asistenteVirtual.modules.experiencia.repository;

import com.asistenteVirtual.modules.experiencia.model.Experiencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExperienciaRepository extends JpaRepository<Experiencia, Long> {

    boolean existsByExamen_Id(Long examenId);

    // Optimizamos con JOIN FETCH para traer datos relacionados de una sola vez
    @Query("""
                SELECT e FROM Experiencia e
                JOIN FETCH e.examen ex
                JOIN FETCH ex.renglon r
                JOIN FETCH r.materia m
                JOIN FETCH r.historiaAcademica h
                JOIN FETCH h.estudiante
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
}
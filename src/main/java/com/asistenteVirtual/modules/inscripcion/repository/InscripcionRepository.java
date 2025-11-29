package com.asistenteVirtual.modules.inscripcion.repository;

import com.asistenteVirtual.modules.inscripcion.model.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {

    @Query("""
                SELECT i FROM Inscripcion i
                JOIN FETCH i.estudiante e
                JOIN FETCH i.materia m
                WHERE m.codigo = :codigo
                AND i.anio = :anio
                AND i.turno = :turno
            """)
    List<Inscripcion> findCompaneros(@Param("codigo") String codigo,
                                     @Param("anio") Integer anio,
                                     @Param("turno") String turno);

    // Verificación eficiente de existencia (Count es más rápido que traer la entidad)
    @Query("""
                SELECT COUNT(i) > 0 FROM Inscripcion i
                WHERE i.materia.codigo = :materiaCodigo
                AND i.materia.planDeEstudio.codigo = :planCodigo
                AND i.estudiante.id = :estudianteId
                AND i.anio = :anio
                AND i.turno = :turno
            """)
    boolean existeInscripcion(@Param("materiaCodigo") String materiaCodigo,
                              @Param("planCodigo") String planCodigo,
                              @Param("estudianteId") Long estudianteId,
                              @Param("anio") Integer anio,
                              @Param("turno") String turno);
}
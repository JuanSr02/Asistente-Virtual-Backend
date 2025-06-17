package com.asistenteVirtual.repository;

import com.asistenteVirtual.model.Examen;
import com.asistenteVirtual.model.Experiencia;
import com.asistenteVirtual.model.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExperienciaRepository extends JpaRepository<Experiencia, Long> {

    // Find by examen
    Optional<Experiencia> findByExamen(Examen examen);

    @Query("SELECT e FROM Experiencia e " +
            "JOIN e.examen ex " +
            "JOIN ex.renglon r " +
            "WHERE r.materia = :materia")
    List<Experiencia> findByMateriaWithJoins(@Param("materia") Materia materia);
    @Query("""
        SELECT e FROM Experiencia e
        WHERE e.examen.renglon.materia.codigo = :codigoMateria
    """)
    List<Experiencia> findAllByCodigoMateria(@Param("codigoMateria") String codigoMateria);

    @Query("""
    SELECT e FROM Experiencia e
    WHERE e.examen.renglon.historiaAcademica.estudiante.id = :estudianteId
""")
    List<Experiencia> findAllByEstudianteId(@Param("estudianteId") Long estudianteId);


    boolean existsByExamen(Examen examen);
}

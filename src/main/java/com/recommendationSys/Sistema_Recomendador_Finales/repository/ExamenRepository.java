package com.recommendationSys.Sistema_Recomendador_Finales.repository;

import com.recommendationSys.Sistema_Recomendador_Finales.model.Examen;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Materia;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Renglon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExamenRepository extends JpaRepository<Examen, Long> {

    List<Examen> findByMateria(Materia materia);

    // Find by renglon
    Optional<Examen> findByRenglon(Renglon renglon);

    // Find exams with a specific grade or higher
    List<Examen> findByNotaGreaterThanEqual(BigDecimal nota);

    // Find exams with a specific grade or lower
    List<Examen> findByNotaLessThanEqual(BigDecimal nota);

    // Find exams with experience recorded
    List<Examen> findByExperienciaIsNotNull();

    // Find exams without experience recorded
    List<Examen> findByExperienciaIsNull();

    @Query("SELECT e.materia FROM Examen e GROUP BY e.materia")
    List<Materia> findDistinctMaterias();

    @Query("SELECT new com.tuapp.EstadisticasPorModalidadDTO("
            + "e.modalidad, COUNT(e), AVG(e.nota)) "
            + "FROM Examen e WHERE e.materia.codigo = :codigoMateria "
            + "GROUP BY e.modalidad")
    List<EstadisticasPorModalidadDTO> findEstadisticasPorModalidad(String codigoMateria);

    @Query(value = """
        SELECT m.codigo, m.nombre, 
               COUNT(*) as total,
               SUM(CASE WHEN e.nota >= 4 THEN 1 ELSE 0 END) as aprobados
        FROM materias m
        JOIN examenes e ON m.codigo = e.materia_codigo
        GROUP BY m.codigo, m.nombre
        ORDER BY (aprobados*1.0/total) DESC
        LIMIT 5
        """, nativeQuery = true)
    List<Object[]> findTop5MateriasAprobadas();

}

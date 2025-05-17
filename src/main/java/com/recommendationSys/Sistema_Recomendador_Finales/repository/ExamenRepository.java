package com.recommendationSys.Sistema_Recomendador_Finales.repository;

import com.recommendationSys.Sistema_Recomendador_Finales.model.Examen;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Materia;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Renglon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExamenRepository extends JpaRepository<Examen, Long> {

    @Query("SELECT ex FROM Examen ex " +
            "JOIN ex.renglon r " +
            "WHERE r.materia = :materia")
    List<Examen> findByMateriaWithJoins(@Param("materia") Materia materia);

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

    @Query("SELECT DISTINCT r.materia FROM Examen e JOIN e.renglon r")
    List<Materia> findDistinctMaterias();

    @Query(value = """
    SELECT m.codigo, m.nombre, 
           COUNT(*) as total,
           SUM(CASE WHEN e.nota >= 4 THEN 1 ELSE 0 END) as aprobados
    FROM materia m
    JOIN renglon r ON m.codigo = r.materia_codigo
    JOIN examen e ON r.id = e.renglon_id
    GROUP BY m.codigo, m.nombre
    ORDER BY (SUM(CASE WHEN e.nota >= 4 THEN 1 ELSE 0 END)*1.0/COUNT(*)) DESC
    LIMIT 5
    """, nativeQuery = true)
    List<Object[]> findTop5MateriasAprobadas();

    @Query(value = """
    SELECT m.codigo, m.nombre, 
           COUNT(*) as total,
           SUM(CASE WHEN e.nota < 4 THEN 1 ELSE 0 END) as reprobados
    FROM materia m
    JOIN renglon r ON m.codigo = r.materia_codigo
    JOIN examen e ON r.id = e.renglon_id
    GROUP BY m.codigo, m.nombre
    ORDER BY (SUM(CASE WHEN e.nota < 4 THEN 1 ELSE 0 END)*1.0/COUNT(*)) DESC
    LIMIT 5
    """, nativeQuery = true)
    List<Object[]> findTop5MateriasReprobadas();

    long countByNotaGreaterThanEqual(double nota);
}

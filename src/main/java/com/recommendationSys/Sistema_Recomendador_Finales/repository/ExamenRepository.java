package com.recommendationSys.Sistema_Recomendador_Finales.repository;

import com.recommendationSys.Sistema_Recomendador_Finales.model.Examen;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamenRepository extends JpaRepository<Examen, Long> {

    @Query("SELECT ex FROM Examen ex " +
            "JOIN ex.renglon r " +
            "WHERE r.materia = :materia")
    List<Examen> findByMateriaWithJoins(@Param("materia") Materia materia);


    @Query(
            value = """
        SELECT m.codigo
        FROM materia m
        JOIN renglon r ON m.codigo = r.materia_codigo AND m.plan_de_estudio_codigo = r.materia_plan_codigo
        JOIN examen e ON r.id = e.renglon_id
        GROUP BY m.codigo
        """,
            nativeQuery = true
    )
    List<String> findDistinctMateriasPorCodigo();

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

    @Query("""
    SELECT r.materia.codigo
    FROM Examen e
    JOIN e.renglon r
    GROUP BY r.materia.codigo
    ORDER BY COUNT(e) DESC
    LIMIT 1
""")
    String findCodigoMateriaMasRendida();

    @Query("""
    SELECT COUNT(e)
    FROM Examen e
    JOIN e.renglon r
    WHERE r.materia.codigo = :codigoMateria
    AND e.nota >= 4
""")
    long countExamenesAprobadosByCodigoMateria(@Param("codigoMateria") String codigoMateria);

    @Query("""
    SELECT COUNT(e)
    FROM Examen e
    JOIN e.renglon r
    WHERE r.materia.codigo = :codigoMateria
""")
    long countExamenesByCodigoMateria(@Param("codigoMateria") String codigoMateria);


}

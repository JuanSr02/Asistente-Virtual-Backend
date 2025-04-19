package com.recommendationSys.Sistema_Recomendador_Finales.repository;

import com.recommendationSys.Sistema_Recomendador_Finales.model.HistoriaAcademica;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Materia;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Renglon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface RenglonRepository extends JpaRepository<Renglon, Long> {

    // Find by academic history
    List<Renglon> findByHistoriaAcademica(HistoriaAcademica historiaAcademica);

    // Find by subject
    List<Renglon> findByMateria(Materia materia);

    // Find by date
    List<Renglon> findByFecha(String fecha);

    // Find by type
    List<Renglon> findByTipo(String tipo);

    // Find by result
    List<Renglon> findByResultado(String resultado);

    // Find by grade greater than or equal to
    List<Renglon> findByNotaGreaterThanEqual(BigDecimal nota);

    // Find by grade less than or equal to
    List<Renglon> findByNotaLessThanEqual(BigDecimal nota);

    // Find by academic history and subject
    List<Renglon> findByHistoriaAcademicaAndMateria(HistoriaAcademica historiaAcademica, Materia materia);

    // Find by academic history and result
    List<Renglon> findByHistoriaAcademicaAndResultado(HistoriaAcademica historiaAcademica, String resultado);

    // Find records with exams
    List<Renglon> findByExamenIsNotNull();

    // Find records without exams
    List<Renglon> findByExamenIsNull();

    // Find average grade by subject
    @Query("SELECT r.materia, AVG(r.nota) FROM Renglon r WHERE r.nota IS NOT NULL GROUP BY r.materia")
    List<Object[]> findAverageGradeBySubject();

    // Find pass rate by subject
    @Query("SELECT r.materia, " +
            "SUM(CASE WHEN r.resultado = 'Aprobado' THEN 1 ELSE 0 END) * 100.0 / COUNT(r) " +
            "FROM Renglon r GROUP BY r.materia")
    List<Object[]> findPassRateBySubject();

    // Find records for a student
    @Query("SELECT r FROM Renglon r WHERE r.historiaAcademica.estudiante.id = :estudianteId")
    List<Renglon> findByEstudianteId(@Param("estudianteId") Long estudianteId);
}

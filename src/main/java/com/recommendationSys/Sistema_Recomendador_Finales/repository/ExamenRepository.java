package com.recommendationSys.Sistema_Recomendador_Finales.repository;

import com.recommendationSys.Sistema_Recomendador_Finales.model.Examen;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Renglon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExamenRepository extends JpaRepository<Examen, Long> {

    // Find by renglon
    Optional<Examen> findByRenglon(Renglon renglon);

    // Find exams by date
    List<Examen> findByFecha(Date fecha);

    // Find exams in date range
    List<Examen> findByFechaBetween(Date startDate, Date endDate);

    // Find exams with a specific grade or higher
    List<Examen> findByNotaGreaterThanEqual(BigDecimal nota);

    // Find exams with a specific grade or lower
    List<Examen> findByNotaLessThanEqual(BigDecimal nota);

    // Find exams with experience recorded
    List<Examen> findByExperienciaIsNotNull();

    // Find exams without experience recorded
    List<Examen> findByExperienciaIsNull();

    // Find average grade for all exams
    @Query("SELECT AVG(e.nota) FROM Examen e")
    BigDecimal findAverageGrade();
}

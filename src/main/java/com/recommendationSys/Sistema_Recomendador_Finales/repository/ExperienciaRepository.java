package com.recommendationSys.Sistema_Recomendador_Finales.repository;

import com.recommendationSys.Sistema_Recomendador_Finales.model.Examen;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Experiencia;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExperienciaRepository extends JpaRepository<Experiencia, Long> {

    // Find by examen
    Optional<Experiencia> findByExamen(Examen examen);

    List<Experiencia> findByMateria(Materia materia);

    // Find by difficulty level
    List<Experiencia> findByDificultad(Integer dificultad);

    // Find by study days greater than or equal to
    List<Experiencia> findByDiasEstudioGreaterThanEqual(Integer diasEstudio);

    // Find by daily study hours greater than or equal to
    List<Experiencia> findByHorasDiariasGreaterThanEqual(Integer horasDiarias);

    // Find by previous attempts
    List<Experiencia> findByIntentosPrevios(Integer intentosPrevios);

    // Find by modality
    List<Experiencia> findByModalidad(String modalidad);

    // Find by resources containing specific text
    List<Experiencia> findByRecursosContaining(String recurso);

    // Find by motivation containing specific text
    List<Experiencia> findByMotivacionContaining(String motivacion);

    // Find average difficulty
    @Query("SELECT AVG(e.dificultad) FROM Experiencia e")
    Double findAverageDifficulty();

    // Find average study days
    @Query("SELECT AVG(e.diasEstudio) FROM Experiencia e")
    Double findAverageStudyDays();

    boolean existsByExamen(Examen examen);
}

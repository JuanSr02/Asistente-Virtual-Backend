package com.recommendationSys.Sistema_Recomendador_Finales.repository;

import com.recommendationSys.Sistema_Recomendador_Finales.model.Examen;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Experiencia;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Materia;
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

    boolean existsByExamen(Examen examen);
}

package repository;

import model.Estudiante;
import model.HistoriaAcademica;
import model.PlanDeEstudio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistoriaAcademicaRepository extends JpaRepository<HistoriaAcademica, Long> {

    // Find by student
    Optional<HistoriaAcademica> findByEstudiante(Estudiante estudiante);

    // Find by study plan
    List<HistoriaAcademica> findByPlanDeEstudio(PlanDeEstudio planDeEstudio);

    // Find by student and study plan
    Optional<HistoriaAcademica> findByEstudianteAndPlanDeEstudio(Estudiante estudiante, PlanDeEstudio planDeEstudio);

    // Find academic histories with no records (renglones)
    List<HistoriaAcademica> findByRenglonesIsEmpty();

    // Count academic histories by study plan
    Long countByPlanDeEstudio(PlanDeEstudio planDeEstudio);

    // Find academic histories with a specific number of records or more
    @Query("SELECT ha FROM HistoriaAcademica ha WHERE SIZE(ha.renglones) >= :count")
    List<HistoriaAcademica> findByRenglonesCountGreaterThanEqual(@Param("count") Integer count);
}

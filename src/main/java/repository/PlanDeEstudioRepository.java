package repository;

import model.PlanDeEstudio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanDeEstudioRepository extends JpaRepository<PlanDeEstudio, String> {

    // Find by proposal name
    List<PlanDeEstudio> findByPropuestaContainingIgnoreCase(String propuesta);

    // Find study plans with subjects
    List<PlanDeEstudio> findByMateriasIsNotEmpty();

    // Find study plans with academic histories
    List<PlanDeEstudio> findByHistoriasAcademicasIsNotEmpty();

    // Find study plans with correlatives
    List<PlanDeEstudio> findByCorrelativasIsNotEmpty();

    // Find study plans without subjects
    List<PlanDeEstudio> findByMateriasIsEmpty();

    // Count study plans by proposal
    Long countByPropuesta(String propuesta);

    // Find study plans with a minimum number of subjects
    @Query("SELECT p FROM PlanDeEstudio p WHERE SIZE(p.materias) >= :count")
    List<PlanDeEstudio> findByMateriasCountGreaterThanEqual(@Param("count") Integer count);

    // Find study plans with a minimum number of academic histories
    @Query("SELECT p FROM PlanDeEstudio p WHERE SIZE(p.historiasAcademicas) >= :count")
    List<PlanDeEstudio> findByHistoriasAcademicasCountGreaterThanEqual(@Param("count") Integer count);

    // Find study plans with a minimum number of correlatives
    @Query("SELECT p FROM PlanDeEstudio p WHERE SIZE(p.correlativas) >= :count")
    List<PlanDeEstudio> findByCorrelativasCountGreaterThanEqual(@Param("count") Integer count);
}


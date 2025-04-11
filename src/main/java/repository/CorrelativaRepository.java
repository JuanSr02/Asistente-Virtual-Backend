package repository;

import model.Correlativa;
import model.CorrelativaId;
import model.Materia;
import model.PlanDeEstudio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CorrelativaRepository extends JpaRepository<Correlativa, CorrelativaId> {

    // Find all correlatives for a specific subject
    List<Correlativa> findByMateria(Materia materia);

    // Find all correlatives for a specific study plan
    List<Correlativa> findByPlanDeEstudio(PlanDeEstudio planDeEstudio);

    // Find all correlatives where a subject is a correlative of others
    List<Correlativa> findByCorrelativa(Materia correlativa);

    // Find all correlatives for a specific subject in a specific study plan
    List<Correlativa> findByMateriaAndPlanDeEstudio(Materia materia, PlanDeEstudio planDeEstudio);

    // Check if a specific correlative relationship exists
    boolean existsByMateriaAndCorrelativaAndPlanDeEstudio(
            Materia materia,
            Materia correlativa,
            PlanDeEstudio planDeEstudio
    );
}

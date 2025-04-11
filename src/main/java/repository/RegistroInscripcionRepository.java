package repository;

import model.Estudiante;
import model.Materia;
import model.RegistroInscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistroInscripcionRepository extends JpaRepository<RegistroInscripcion, Long> {

    // Find by student
    List<RegistroInscripcion> findByEstudiante(Estudiante estudiante);

    // Find by subject
    List<RegistroInscripcion> findByMateria(Materia materia);

    // Find by year
    List<RegistroInscripcion> findByAnio(Integer anio);

    // Find by shift
    List<RegistroInscripcion> findByTurno(String turno);

    // Find by student and year
    List<RegistroInscripcion> findByEstudianteAndAnio(Estudiante estudiante, Integer anio);

    // Find by student and subject
    List<RegistroInscripcion> findByEstudianteAndMateria(Estudiante estudiante, Materia materia);

    // Find by subject and year
    List<RegistroInscripcion> findByMateriaAndAnio(Materia materia, Integer anio);

    // Count registrations by subject
    Long countByMateria(Materia materia);

    // Count registrations by student
    Long countByEstudiante(Estudiante estudiante);

    // Count registrations by year
    Long countByAnio(Integer anio);

    // Find most popular subjects by registration count
    @Query("SELECT r.materia, COUNT(r) as count FROM RegistroInscripcion r GROUP BY r.materia ORDER BY count DESC")
    List<Object[]> findMostPopularSubjects();

    // Find students registered for a specific subject in a specific year
    @Query("SELECT r.estudiante FROM RegistroInscripcion r WHERE r.materia = :materia AND r.anio = :anio")
    List<Estudiante> findEstudiantesByMateriaAndAnio(@Param("materia") Materia materia, @Param("anio") Integer anio);
}

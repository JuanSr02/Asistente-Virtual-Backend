package repository;

import model.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    // Find by registration number (nroRegistro)
    Optional<Estudiante> findByNroRegistro(Integer nroRegistro);

    // Find by username (usuario)
    Optional<Estudiante> findByUsuario(String usuario);

    // Find by DNI
    Optional<Estudiante> findByDni(Integer dni);

    // Find by email
    Optional<Estudiante> findByMail(String mail);

    // Find students without academic history
    List<Estudiante> findByHistoriaAcademicaIsNull();

    // Find students with inscriptions
    List<Estudiante> findByInscripcionesIsNotEmpty();
}


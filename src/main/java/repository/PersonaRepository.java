package repository;

import model.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

import java.util.Optional;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long> {

    // Find by username (usuario)
    Optional<Persona> findByUsuario(String usuario);

    // Find by DNI
    Optional<Persona> findByDni(Integer dni);

    // Find by email
    Optional<Persona> findByMail(String mail);

    // Find by type (tipo)
    List<Persona> findByTipo(String tipo);

    // Check if username exists
    boolean existsByUsuario(String usuario);

    // Check if email exists
    boolean existsByMail(String mail);
}

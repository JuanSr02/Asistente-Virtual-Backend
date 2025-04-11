package repository;

import model.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdministradorRepository extends JpaRepository<Administrador, Long> {

    // Find by token
    Optional<Administrador> findByToken(Integer token);

    // Find by username (usuario)
    Optional<Administrador> findByUsuario(String usuario);

    // Find by DNI
    Optional<Administrador> findByDni(Integer dni);
}


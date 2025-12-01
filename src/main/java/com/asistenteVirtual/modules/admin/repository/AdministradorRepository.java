package com.asistenteVirtual.modules.admin.repository;

import com.asistenteVirtual.modules.admin.model.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdministradorRepository extends JpaRepository<Administrador, Long> {
    // Métodos extra si son necesarios
}
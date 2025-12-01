package com.asistenteVirtual.common.repository;

import com.asistenteVirtual.common.model.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long> {
    boolean existsByMail(String mail);

}

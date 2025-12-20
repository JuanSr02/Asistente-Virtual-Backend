package com.asistenteVirtual.common.repository;

import com.asistenteVirtual.common.model.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long> {
    boolean existsByMail(String mail);

    // Buscar por el ID de autenticación de Supabase
    Optional<Persona> findBySupabaseUserId(String supabaseUserId);

    // Buscar por email (heredado de Persona)
    Optional<Persona> findByMail(String mail);

}

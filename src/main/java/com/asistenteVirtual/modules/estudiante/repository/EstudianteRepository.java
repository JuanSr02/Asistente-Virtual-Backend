package com.asistenteVirtual.modules.estudiante.repository;

import com.asistenteVirtual.modules.estudiante.model.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    // Buscar por el ID de autenticación de Supabase
    Optional<Estudiante> findBySupabaseUserId(String supabaseUserId);

    // Buscar por email (heredado de Persona)
    Optional<Estudiante> findByMail(String mail);

}


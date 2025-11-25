package com.asistenteVirtual.modules.estudiante.repository;

import com.asistenteVirtual.modules.estudiante.model.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

}


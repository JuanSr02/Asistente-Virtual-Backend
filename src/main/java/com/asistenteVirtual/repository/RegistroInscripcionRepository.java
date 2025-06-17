package com.asistenteVirtual.repository;

import com.asistenteVirtual.model.Estudiante;
import com.asistenteVirtual.model.Materia;
import com.asistenteVirtual.model.RegistroInscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistroInscripcionRepository extends JpaRepository<RegistroInscripcion, Long> {

    // Find by materia,año y turno
    List<RegistroInscripcion> findByMateria_CodigoAndAnioAndTurno(String codigo, Integer anio, String turno);
    // Exists by Materia and Estudiante
    boolean existsByMateriaAndEstudianteAndAnioAndTurno(Materia materia, Estudiante estudiante, Integer anio,String turno);

    }

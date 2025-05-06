package com.recommendationSys.Sistema_Recomendador_Finales.services;


import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.ExperienciaDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.exceptions.ResourceNotFoundException;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Examen;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Experiencia;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.ExamenRepository;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.ExperienciaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ExperienciaService {

    private final ExperienciaRepository experienciaRepository;
    private final ExamenRepository examenRepository;

    public ExperienciaService(ExperienciaRepository experienciaRepository,
                              ExamenRepository examenRepository) {
        this.experienciaRepository = experienciaRepository;
        this.examenRepository = examenRepository;
    }

    // Create
    public Experiencia crearExperiencia(ExperienciaDTO experienciaDTO) {
        Examen examen = examenRepository.findById(experienciaDTO.getExamenId())
                .orElseThrow(() -> new ResourceNotFoundException("Examen no encontrado"));

        if (experienciaRepository.existsByExamen(examen)) {
            throw new IllegalStateException("Ya existe una experiencia para este examen");
        }

        Experiencia experiencia = new Experiencia();
        experiencia.setExamen(examen);
        experiencia.setDificultad(experienciaDTO.getDificultad());
        experiencia.setDiasEstudio(experienciaDTO.getDiasEstudio());
        experiencia.setHorasDiarias(experienciaDTO.getHorasDiarias());
        experiencia.setIntentosPrevios(experienciaDTO.getIntentosPrevios());
        experiencia.setModalidad(experienciaDTO.getModalidad());
        experiencia.setRecursos(experienciaDTO.getRecursos());
        experiencia.setMotivacion(experienciaDTO.getMotivacion());
        experiencia.setCondiciones(experienciaDTO.getCondiciones());

        return experienciaRepository.save(experiencia);
    }

    // Read (Single)
    public Experiencia obtenerExperienciaPorId(Long id) {
        return experienciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Experiencia no encontrada"));
    }

    // Read (All)
    public List<Experiencia> obtenerTodasLasExperiencias() {
        return experienciaRepository.findAll();
    }

    // Read by Examen
    public Experiencia obtenerExperienciaPorExamen(Long examenId) {
        Examen examen = examenRepository.findById(examenId)
                .orElseThrow(() -> new ResourceNotFoundException("Examen no encontrado"));

        return experienciaRepository.findByExamen(examen)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró experiencia para este examen"));
    }

    // Update
    public Experiencia actualizarExperiencia(Long id, ExperienciaDTO experienciaDTO) {
        Experiencia experiencia = experienciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Experiencia no encontrada"));

        if (experienciaDTO.getDificultad() != null) {
            experiencia.setDificultad(experienciaDTO.getDificultad());
        }
        if (experienciaDTO.getDiasEstudio() != null) {
            experiencia.setDiasEstudio(experienciaDTO.getDiasEstudio());
        }
        if (experienciaDTO.getHorasDiarias() != null) {
            experiencia.setHorasDiarias(experienciaDTO.getHorasDiarias());
        }
        if (experienciaDTO.getIntentosPrevios() != null) {
            experiencia.setIntentosPrevios(experienciaDTO.getIntentosPrevios());
        }
        if (experienciaDTO.getModalidad() != null) {
            experiencia.setModalidad(experienciaDTO.getModalidad());
        }
        if (experienciaDTO.getRecursos() != null) {
            experiencia.setRecursos(experienciaDTO.getRecursos());
        }
        if (experienciaDTO.getMotivacion() != null) {
            experiencia.setMotivacion(experienciaDTO.getMotivacion());
        }
        if (experienciaDTO.getCondiciones() != null) {
            experiencia.setCondiciones(experienciaDTO.getCondiciones());
        }

        return experienciaRepository.save(experiencia);
    }

    // Delete
    public void eliminarExperiencia(Long id) {
        if (!experienciaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Experiencia no encontrada");
        }
        experienciaRepository.deleteById(id);
    }
}

package com.recommendationSys.Sistema_Recomendador_Finales.services;


import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.ActualizarExperienciaDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.ExperienciaDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.exceptions.ResourceNotFoundException;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Examen;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Experiencia;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.ExamenRepository;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.ExperienciaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

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
    public Experiencia actualizarExperiencia(Long id, ActualizarExperienciaDTO dto) {
        Experiencia experiencia = experienciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Experiencia no encontrada"));

        // Validar que al menos un campo viene en el DTO
        if (Stream.of(
                dto.getDificultad(),
                dto.getDiasEstudio(),
                dto.getHorasDiarias(),
                dto.getIntentosPrevios(),
                dto.getModalidad(),
                dto.getRecursos(),
                dto.getMotivacion(),
                dto.getCondiciones()
        ).allMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Debe proporcionar al menos un campo para actualizar");
        }

        Optional.ofNullable(dto.getDificultad()).ifPresent(experiencia::setDificultad);
        Optional.ofNullable(dto.getDiasEstudio()).ifPresent(experiencia::setDiasEstudio);
        Optional.ofNullable(dto.getHorasDiarias()).ifPresent(experiencia::setHorasDiarias);
        Optional.ofNullable(dto.getIntentosPrevios()).ifPresent(experiencia::setIntentosPrevios);
        Optional.ofNullable(dto.getModalidad()).ifPresent(experiencia::setModalidad);
        Optional.ofNullable(dto.getRecursos()).ifPresent(experiencia::setRecursos);
        Optional.ofNullable(dto.getMotivacion()).ifPresent(experiencia::setMotivacion);
        Optional.ofNullable(dto.getCondiciones()).ifPresent(experiencia::setCondiciones);

        return experienciaRepository.save(experiencia);
    }
    // Delete
    @Transactional
    public void eliminarExperiencia(Long id) {
        Experiencia experiencia = experienciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Experiencia no encontrada"));

        // Romper la relación bidireccional si existe
        if (experiencia.getExamen() != null && experiencia.getExamen().getExperiencia() != null) {
            experiencia.getExamen().setExperiencia(null);
        }

        experienciaRepository.delete(experiencia);
    }
}

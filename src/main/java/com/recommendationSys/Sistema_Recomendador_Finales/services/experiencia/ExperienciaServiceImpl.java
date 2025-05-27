package com.recommendationSys.Sistema_Recomendador_Finales.services.experiencia;

import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.ActualizarExperienciaDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.ExperienciaDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.exceptions.ResourceNotFoundException;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Examen;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Experiencia;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Materia;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.ExamenRepository;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.ExperienciaRepository;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.MateriaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ExperienciaServiceImpl implements ExperienciaCRUDService, ExperienciaQueryService {

    private final ExperienciaRepository experienciaRepository;
    private final ExamenRepository examenRepository;
    private final MateriaRepository materiaRepository;
    private final ExperienciaMapper experienciaMapper;
    private final ExperienciaValidator experienciaValidator;

    @Override
    public Experiencia crearExperiencia(ExperienciaDTO experienciaDTO) {
        experienciaValidator.validarCreacionExperiencia(experienciaDTO);

        Examen examen = examenRepository.findById(experienciaDTO.getExamenId())
                .orElseThrow(() -> new ResourceNotFoundException("Examen no encontrado"));

        Experiencia experiencia = experienciaMapper.toEntity(experienciaDTO, examen);
        return experienciaRepository.save(experiencia);
    }

    @Override
    public Experiencia obtenerExperienciaPorId(Long id) {
        return experienciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Experiencia no encontrada"));
    }

    @Override
    public List<Experiencia> obtenerTodasLasExperiencias() {
        return experienciaRepository.findAll();
    }

    @Override
    public List<Experiencia> obtenerExperienciasPorMateria(String codigoMateria) {
        Materia materia = materiaRepository.findById(codigoMateria)
                .orElseThrow(() -> new ResourceNotFoundException("Materia no encontrada"));

        return experienciaRepository.findByMateriaWithJoins(materia);
    }

    @Override
    public Experiencia obtenerExperienciaPorExamen(Long examenId) {
        Examen examen = examenRepository.findById(examenId)
                .orElseThrow(() -> new ResourceNotFoundException("Examen no encontrado"));

        return experienciaRepository.findByExamen(examen)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró experiencia para este examen"));
    }

    @Override
    public Experiencia actualizarExperiencia(Long id, ActualizarExperienciaDTO dto) {
        experienciaValidator.validarActualizacionExperiencia(dto);

        Experiencia experiencia = obtenerExperienciaPorId(id);
        experienciaMapper.updateFromDto(dto, experiencia);

        return experienciaRepository.save(experiencia);
    }

    @Override
    public void eliminarExperiencia(Long id) {
        Experiencia experiencia = obtenerExperienciaPorId(id);
        desvincularExamen(experiencia);
        experienciaRepository.delete(experiencia);
    }

    private void desvincularExamen(Experiencia experiencia) {
        if (experiencia.getExamen() != null && experiencia.getExamen().getExperiencia() != null) {
            experiencia.getExamen().setExperiencia(null);
        }
    }
}
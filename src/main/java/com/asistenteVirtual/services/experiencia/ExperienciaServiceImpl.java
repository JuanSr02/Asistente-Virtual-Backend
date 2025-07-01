package com.asistenteVirtual.services.experiencia;

import com.asistenteVirtual.DTOs.ActualizarExperienciaDTO;
import com.asistenteVirtual.DTOs.ExamenDTO;
import com.asistenteVirtual.DTOs.ExperienciaDTO;
import com.asistenteVirtual.DTOs.ExperienciaResponseDTO;
import com.asistenteVirtual.exceptions.ResourceNotFoundException;
import com.asistenteVirtual.model.Examen;
import com.asistenteVirtual.model.Experiencia;
import com.asistenteVirtual.model.Materia;
import com.asistenteVirtual.repository.EstudianteRepository;
import com.asistenteVirtual.repository.ExamenRepository;
import com.asistenteVirtual.repository.ExperienciaRepository;
import com.asistenteVirtual.repository.MateriaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ExperienciaServiceImpl implements ExperienciaCRUDService, ExperienciaQueryService {

    @Autowired
    private final ExperienciaRepository experienciaRepository;
    @Autowired
    private final ExamenRepository examenRepository;
    @Autowired
    private final MateriaRepository materiaRepository;
    @Autowired
    private final EstudianteRepository estudianteRepository;
    private final ExperienciaMapper experienciaMapper;
    private final ExperienciaValidator experienciaValidator;

    @Override
    public ExperienciaResponseDTO crearExperiencia(ExperienciaDTO experienciaDTO) {
        experienciaValidator.validarCreacionExperiencia(experienciaDTO);

        Examen examen = examenRepository.findById(experienciaDTO.getExamenId())
                .orElseThrow(() -> new ResourceNotFoundException("Examen no encontrado"));

        Experiencia experiencia = experienciaMapper.toEntity(experienciaDTO, examen);
        return experienciaMapper.mapToExperienciaResponseDTO(experienciaRepository.save(experiencia));
    }

    @Override
    public ExperienciaResponseDTO obtenerExperienciaPorId(Long id) {
        return experienciaMapper.mapToExperienciaResponseDTO(experienciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Experiencia no encontrada")));
    }

    @Override
    public List<ExperienciaResponseDTO> obtenerTodasLasExperiencias() {
        List<Experiencia> experiencias = experienciaRepository.findAll();
        return listToExperienciaResponseDTO(experiencias);
    }

    public List<ExperienciaResponseDTO> listToExperienciaResponseDTO(List<Experiencia> experiencias) {
        List<ExperienciaResponseDTO> mapeadas = new ArrayList<ExperienciaResponseDTO>();
        for (Experiencia e : experiencias) {
            mapeadas.add(experienciaMapper.mapToExperienciaResponseDTO(e));
        }
        return mapeadas;
    }

    @Override
    public List<ExperienciaResponseDTO> obtenerExperienciasPorMateria(String codigoMateria) {
        Materia materia = materiaRepository.findFirstByCodigo(codigoMateria)
                .orElseThrow(() -> new ResourceNotFoundException("Materia no encontrada"));

        return listToExperienciaResponseDTO(experienciaRepository.findAllByCodigoMateria(codigoMateria));
    }

    @Override
    public List<ExperienciaResponseDTO> obtenerExperienciasPorEstudiante(Long idEstudiante) {
        if (!estudianteRepository.existsById(idEstudiante)) {
            throw new ResourceNotFoundException("Estudiante no encontrado");
        }
        return listToExperienciaResponseDTO(experienciaRepository.findAllByEstudianteId(idEstudiante));
    }


    @Override
    public ExperienciaResponseDTO actualizarExperiencia(Long id, ActualizarExperienciaDTO dto) {
        experienciaValidator.validarActualizacionExperiencia(dto);

        Experiencia experiencia = experienciaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Experiencia no encontrada"));
        experienciaMapper.updateFromDto(dto, experiencia);

        return experienciaMapper.mapToExperienciaResponseDTO(experienciaRepository.save(experiencia));
    }

    @Override
    public void eliminarExperiencia(Long id) {
        Experiencia experiencia = experienciaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Experiencia no encontrada"));
        desvincularExamen(experiencia);
        experienciaRepository.delete(experiencia);
    }

    private void desvincularExamen(Experiencia experiencia) {
        if (experiencia.getExamen() != null && experiencia.getExamen().getExperiencia() != null) {
            experiencia.getExamen().setExperiencia(null);
        }
    }

    @Override
    public List<ExamenDTO> obtenerExamenesPorEstudiante(Long idEstudiante) {
        List<Examen> examenes = examenRepository.findByEstudianteId(idEstudiante);

        return examenes.stream()
                .map(examen -> {
                    String materiaCodigo = null;
                    String materiaNombre = null;

                    if (examen.getRenglon() != null && examen.getRenglon().getMateria() != null) {
                        materiaCodigo = examen.getRenglon().getMateria().getCodigo();
                        materiaNombre = examen.getRenglon().getMateria().getNombre();
                    }

                    return ExamenDTO.builder()
                            .id(examen.getId())
                            .fecha(examen.getFecha())
                            .nota(examen.getNota())
                            .renglonId(examen.getRenglon() != null ? examen.getRenglon().getId() : null)
                            .materiaCodigo(materiaCodigo)
                            .materiaNombre(materiaNombre)
                            .build();
                })
                .collect(Collectors.toList());
    }
}
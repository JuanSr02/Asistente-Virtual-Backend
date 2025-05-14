package com.recommendationSys.Sistema_Recomendador_Finales.services;

import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.InscripcionResponseDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.RegistroInscripcionDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.exceptions.IntegrityException;
import com.recommendationSys.Sistema_Recomendador_Finales.exceptions.ResourceNotFoundException;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Estudiante;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Materia;
import com.recommendationSys.Sistema_Recomendador_Finales.model.RegistroInscripcion;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.EstudianteRepository;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.MateriaRepository;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.RegistroInscripcionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RegistroInscripcionService {

    private final EmailService emailService;
    private final RegistroInscripcionRepository inscripcionRepo;
    private final MateriaRepository materiaRepo;
    private final EstudianteRepository estudianteRepo;

    public RegistroInscripcionService(EmailService emailService, RegistroInscripcionRepository inscripcionRepo,
                                      MateriaRepository materiaRepo,
                                      EstudianteRepository estudianteRepo) {
        this.emailService = emailService;
        this.inscripcionRepo = inscripcionRepo;
        this.materiaRepo = materiaRepo;
        this.estudianteRepo = estudianteRepo;
    }

    // Alta de inscripción
    public InscripcionResponseDTO crearInscripcion(RegistroInscripcionDTO dto) {
        // Validar que no exista una inscripción previa
        Materia materia = materiaRepo.findById(dto.getMateriaCodigo())
                .orElseThrow(() -> new ResourceNotFoundException("Materia no encontrada"));

        Estudiante estudiante = estudianteRepo.findById(dto.getEstudianteId())
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado"));

        if (inscripcionRepo.existsByMateriaAndEstudianteAndAnio(materia, estudiante, dto.getAnio())) {
            throw new IntegrityException("El estudiante ya está inscripto en esta materia para el año " + dto.getAnio());
        }

        RegistroInscripcion inscripcion = new RegistroInscripcion();
        inscripcion.setTurno(dto.getTurno());
        inscripcion.setAnio(dto.getAnio());
        inscripcion.setMateria(materia);
        inscripcion.setEstudiante(estudiante);

        RegistroInscripcion saved = inscripcionRepo.save(inscripcion);

        notificarCompaneros(saved);
        return mapToResponseDTO(saved);
    }

    // Baja de inscripción
    public void eliminarInscripcion(Long id) {
        if (!inscripcionRepo.existsById(id)) {
            throw new ResourceNotFoundException("Inscripción no encontrada");
        }

        inscripcionRepo.deleteById(id);
    }

    // Consulta de inscriptos
    public List<InscripcionResponseDTO> obtenerInscriptos(String materiaCodigo, Integer anio, String turno) {
        Materia materia = materiaRepo.findById(materiaCodigo)
                .orElseThrow(() -> new ResourceNotFoundException("Materia no encontrada"));

        List<RegistroInscripcion> inscriptos = inscripcionRepo.findByMateriaAndAnioAndTurno(materia, anio, turno);

        return inscriptos.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // Mapeo a DTO de respuesta
    private InscripcionResponseDTO mapToResponseDTO(RegistroInscripcion inscripcion) {
        InscripcionResponseDTO dto = new InscripcionResponseDTO();
        dto.setId(inscripcion.getId());
        dto.setTurno(inscripcion.getTurno());
        dto.setAnio(inscripcion.getAnio());
        dto.setMateriaCodigo(inscripcion.getMateria().getCodigo());
        dto.setMateriaNombre(inscripcion.getMateria().getNombre());
        dto.setEstudianteId(inscripcion.getEstudiante().getId());
        dto.setEstudianteNombre(inscripcion.getEstudiante().getNombreApellido());
        dto.setEstudianteNroRegistro(inscripcion.getEstudiante().getNroRegistro());

        return dto;
    }

    private void notificarCompaneros(RegistroInscripcion nuevaInscripcion) {
        List<RegistroInscripcion> companeros = inscripcionRepo
                .findByMateriaAndAnioAndTurno(
                        nuevaInscripcion.getMateria(),
                        nuevaInscripcion.getAnio(),
                        nuevaInscripcion.getTurno());

        companeros.stream()
                .filter(insc -> !insc.getEstudiante().getId().equals(nuevaInscripcion.getEstudiante().getId()))
                .forEach(insc -> emailService.enviarNotificacionNuevoInscripto(
                        insc.getEstudiante().getMail(),
                        nuevaInscripcion.getMateria().getNombre(),
                        nuevaInscripcion.getTurno()));
    }
}


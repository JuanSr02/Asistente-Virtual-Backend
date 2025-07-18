package com.asistenteVirtual.services.inscripciones;

import com.asistenteVirtual.DTOs.InscripcionResponseDTO;
import com.asistenteVirtual.DTOs.RegistroInscripcionDTO;
import com.asistenteVirtual.exceptions.ResourceNotFoundException;
import com.asistenteVirtual.model.Estudiante;
import com.asistenteVirtual.model.Materia;
import com.asistenteVirtual.model.PlanDeEstudio;
import com.asistenteVirtual.model.RegistroInscripcion;
import com.asistenteVirtual.repository.EstudianteRepository;
import com.asistenteVirtual.repository.MateriaRepository;
import com.asistenteVirtual.repository.PlanDeEstudioRepository;
import com.asistenteVirtual.repository.RegistroInscripcionRepository;
import com.asistenteVirtual.services.email.EmailNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class RegistroInscripcionServiceImpl implements InscripcionService, InscripcionNotificationService {

    private final EmailNotificationService emailNotificationService;
    private final RegistroInscripcionRepository inscripcionRepo;
    private final MateriaRepository materiaRepo;
    private final EstudianteRepository estudianteRepo;
    private final InscripcionMapper inscripcionMapper;
    private final InscripcionValidator inscripcionValidator;
    private final PlanDeEstudioRepository planDeEstudioRepository;

    @Override
    public InscripcionResponseDTO crearInscripcion(RegistroInscripcionDTO dto) {
        inscripcionValidator.validarInscripcion(dto);
        PlanDeEstudio plan = planDeEstudioRepository.findById(dto.getMateriaPlan())
                .orElseThrow(() -> new ResourceNotFoundException("Plan de estudios no encontrado"));
        Materia materia = materiaRepo.findByCodigoAndPlanDeEstudio(dto.getMateriaCodigo(),plan)
                .orElseThrow(() -> new ResourceNotFoundException("Materia no encontrada"));
        Estudiante estudiante = estudianteRepo.findById(dto.getEstudianteId())
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado"));
        RegistroInscripcion inscripcion = inscripcionMapper.toEntity(dto, materia, estudiante);
        RegistroInscripcion saved = inscripcionRepo.save(inscripcion);

        notificarCompaneros(saved);
        return inscripcionMapper.toResponseDTO(saved);
    }

    @Override
    public void eliminarInscripcion(Long id) {
        if (!inscripcionRepo.existsById(id)) {
            throw new ResourceNotFoundException("Inscripción no encontrada");
        }
        inscripcionRepo.deleteById(id);
    }

    @Override
    public List<InscripcionResponseDTO> obtenerInscriptos(String materiaCodigo, Integer anio, String turno) {
        Materia materia = materiaRepo.findFirstByCodigo(materiaCodigo)
                .orElseThrow(() -> new ResourceNotFoundException("Materia no encontrada"));

        List<RegistroInscripcion> inscriptos = inscripcionRepo.findByMateria_CodigoAndAnioAndTurno(materiaCodigo, anio, turno);

        return inscriptos.stream()
                .map(inscripcionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void notificarCompaneros(RegistroInscripcion nuevaInscripcion) {
        List<RegistroInscripcion> companeros = obtenerCompaneros(nuevaInscripcion);

        companeros.forEach(insc ->
                emailNotificationService.enviarNotificacionNuevoInscripto(
                        insc.getEstudiante().getMail(),
                        nuevaInscripcion.getMateria().getNombre(),
                        nuevaInscripcion.getTurno(),
                        String.valueOf(nuevaInscripcion.getAnio()),
                        nuevaInscripcion.getEstudiante()
                )
        );
    }

    @Override
    public void notificarCompanerosDTO(RegistroInscripcionDTO dto) {
        PlanDeEstudio plan = planDeEstudioRepository.findById(dto.getMateriaPlan())
                .orElseThrow(() -> new ResourceNotFoundException("Plan de estudios no encontrado"));
        Materia materia = materiaRepo.findByCodigoAndPlanDeEstudio(dto.getMateriaCodigo(),plan)
                .orElseThrow(() -> new ResourceNotFoundException("Materia no encontrada"));
        Estudiante estudiante = estudianteRepo.findById(dto.getEstudianteId())
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado"));
        RegistroInscripcion inscripcion = inscripcionMapper.toEntity(dto, materia, estudiante);
        notificarCompaneros(inscripcion);
    }

    private List<RegistroInscripcion> obtenerCompaneros(RegistroInscripcion inscripcion) {
        return inscripcionRepo.findByMateria_CodigoAndAnioAndTurno(
                        inscripcion.getMateria().getCodigo(),
                        inscripcion.getAnio(),
                        inscripcion.getTurno())
                .stream()
                .filter(insc -> !insc.getEstudiante().getId().equals(inscripcion.getEstudiante().getId()))
                .collect(Collectors.toList());
    }
}
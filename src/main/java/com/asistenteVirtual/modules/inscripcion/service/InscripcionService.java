package com.asistenteVirtual.modules.inscripcion.service;

import com.asistenteVirtual.common.exceptions.IntegrityException;
import com.asistenteVirtual.common.exceptions.ResourceNotFoundException;
import com.asistenteVirtual.modules.estudiante.repository.EstudianteRepository;
import com.asistenteVirtual.modules.inscripcion.dto.InscripcionRequest;
import com.asistenteVirtual.modules.inscripcion.dto.InscripcionResponse;
import com.asistenteVirtual.modules.inscripcion.model.Inscripcion;
import com.asistenteVirtual.modules.inscripcion.repository.InscripcionRepository;
import com.asistenteVirtual.modules.inscripcion.service.email.EmailNotificationService;
import com.asistenteVirtual.modules.planEstudio.repository.MateriaRepository;
import com.asistenteVirtual.modules.security.service.SecurityValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InscripcionService {

    private final InscripcionRepository inscripcionRepo;
    private final MateriaRepository materiaRepo;
    private final EstudianteRepository estudianteRepo;
    private final EmailNotificationService emailService;
    private final SecurityValidator securityValidator;

    @Transactional
    public InscripcionResponse crearInscripcion(InscripcionRequest dto) {
        securityValidator.validarAccesoEstudiante(dto.estudianteId());
        // 1. Validación de Integridad (Fail fast)
        if (inscripcionRepo.existeInscripcion(
                dto.materiaCodigo(), dto.materiaPlan(), dto.estudianteId(), dto.anio(), dto.turno())) {
            throw new IntegrityException("El estudiante ya está inscripto en esta materia/turno/año.");
        }

        // 2. Recuperación de Entidades (Optimizada)
        var estudiante = estudianteRepo.findById(dto.estudianteId())
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado"));

        // Buscamos la materia por clave compuesta (suponiendo que agregaste el metodo en MateriaRepository o usamos findById con MateriaId)
        var materia = materiaRepo.findByCodigoAndPlanDeEstudio_Codigo(dto.materiaCodigo(), dto.materiaPlan())
                .orElseThrow(() -> new ResourceNotFoundException("Materia no encontrada"));

        // 3. Persistencia
        var inscripcion = Inscripcion.builder()
                .anio(dto.anio())
                .turno(dto.turno())
                .materia(materia)
                .estudiante(estudiante)
                .build();

        var saved = inscripcionRepo.save(inscripcion);

        // 4. Notificación Asíncrona (Fire and Forget)
        notificarCompaneros(saved);

        return InscripcionResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<InscripcionResponse> obtenerInscriptos(String materiaCodigo, Integer anio, String turno) {
        return inscripcionRepo.findCompaneros(materiaCodigo, anio, turno).stream()
                .map(InscripcionResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InscripcionResponse> obtenerInscripcionesPorEstudiante(Long estudianteId) {
        return inscripcionRepo.findByEstudianteId(estudianteId).stream()
                .map(InscripcionResponse::fromEntity)
                .toList();
    }

    @Transactional
    public void eliminarInscripcion(Long id) {
        // 1. Primero hay que buscar la inscripción para saber de quién es
        var inscripcion = inscripcionRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inscripción no encontrada"));

        // 2. Validar que el dueño de la inscripción es quien llama
        securityValidator.validarAutoria(inscripcion.getEstudiante().getSupabaseUserId());

        inscripcionRepo.delete(inscripcion);
    }

    /**
     * Lógica de notificación encapsulada.
     * Busca compañeros de la misma cursada y dispara emails.
     */
    private void notificarCompaneros(Inscripcion nueva) {
        // Buscamos inscripciones que coincidan en materia, año y turno
        List<Inscripcion> companeros = inscripcionRepo.findCompaneros(
                nueva.getMateria().getCodigo(),
                nueva.getAnio(),
                nueva.getTurno()
        );

        // Filtramos al propio estudiante y disparamos notificaciones
        companeros.stream()
                .filter(i -> !i.getEstudiante().getId().equals(nueva.getEstudiante().getId()))
                .forEach(i -> {
                    try {
                        // Asumiendo que enviarNotificacionNuevoInscripto es @Async en EmailNotificationService
                        emailService.enviarNotificacionNuevoInscripto(
                                i.getEstudiante().getMail(),
                                nueva.getMateria().getNombre(),
                                nueva.getTurno(),
                                String.valueOf(nueva.getAnio()),
                                nueva.getEstudiante()
                        );
                    } catch (Exception e) {
                        log.error("Fallo al notificar a {}: {}", i.getEstudiante().getMail(), e.getMessage());
                    }
                });
    }
}
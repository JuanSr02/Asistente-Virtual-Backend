package com.asistenteVirtual.modules.experiencia.service;

import com.asistenteVirtual.common.exceptions.IntegrityException;
import com.asistenteVirtual.common.exceptions.ResourceNotFoundException;
import com.asistenteVirtual.modules.estudiante.repository.EstudianteRepository;
import com.asistenteVirtual.modules.experiencia.dto.ExamenDisponibleResponse;
import com.asistenteVirtual.modules.experiencia.dto.ExperienciaRequest;
import com.asistenteVirtual.modules.experiencia.dto.ExperienciaResponse;
import com.asistenteVirtual.modules.experiencia.dto.ExperienciaUpdate;
import com.asistenteVirtual.modules.experiencia.model.Experiencia;
import com.asistenteVirtual.modules.experiencia.repository.ExperienciaRepository;
import com.asistenteVirtual.modules.historiaAcademica.model.HistoriaAcademica;
import com.asistenteVirtual.modules.historiaAcademica.repository.ExamenRepository;
import com.asistenteVirtual.modules.historiaAcademica.repository.HistoriaAcademicaRepository;
import com.asistenteVirtual.modules.security.service.SecurityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExperienciaService {

    private final ExperienciaRepository experienciaRepo;
    private final ExamenRepository examenRepo;
    private final EstudianteRepository estudianteRepo;
    private final SecurityValidator securityValidator;
    private final HistoriaAcademicaRepository historiaAcademicaRepo;

    @Transactional
    public ExperienciaResponse crearExperiencia(ExperienciaRequest dto) {
        // 1. Validar existencia y duplicidad
        var examen = examenRepo.findById(dto.examenId())
                .orElseThrow(() -> new ResourceNotFoundException("Examen no encontrado con ID: " + dto.examenId()));

        if (experienciaRepo.existsByExamen_Id(examen.getId())) {
            throw new IntegrityException("Ya existe una experiencia cargada para este examen.");
        }

        // Accedemos al estudiante a través del grafo de objetos: Examen -> Renglon -> Historia -> Estudiante
        var propietarioId = examen.getRenglon().getHistoriaAcademica().getEstudiante().getSupabaseUserId();

        // Validamos
        securityValidator.validarAutoria(propietarioId);

        // 2. Build Entity
        var experiencia = Experiencia.builder()
                .examen(examen)
                .dificultad(dto.dificultad())
                .diasEstudio(dto.diasEstudio())
                .horasDiarias(dto.horasDiarias())
                .intentosPrevios(dto.intentosPrevios())
                .modalidad(dto.modalidad())
                .recursos(dto.recursos())
                .motivacion(dto.motivacion())
                .linkResumen(dto.linkResumen())
                .build();

        // 3. Save & Map
        return ExperienciaResponse.fromEntity(experienciaRepo.save(experiencia));
    }

    @Transactional(readOnly = true)
    public List<ExperienciaResponse> obtenerPorMateria(String codigoMateria) {
        return experienciaRepo.findAllByCodigoMateria(codigoMateria).stream()
                .map(ExperienciaResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ExperienciaResponse> obtenerPorEstudiante(Long estudianteId) {
        return experienciaRepo.findAllByEstudianteId(estudianteId).stream()
                .map(ExperienciaResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public ExperienciaResponse obtenerPorId(Long id) {
        return experienciaRepo.findById(id)
                .map(ExperienciaResponse::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Experiencia no encontrada"));
    }

    @Transactional
    public ExperienciaResponse actualizarExperiencia(Long id, ExperienciaUpdate dto) {
        var exp = experienciaRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Experiencia no encontrada"));

        // Validar autoría navegando el grafo
        var propietarioId = exp.getExamen().getRenglon().getHistoriaAcademica().getEstudiante().getSupabaseUserId();
        securityValidator.validarAutoria(propietarioId);

        // Actualización parcial limpia usando Optional de Java
        if (dto.dificultad() != null) exp.setDificultad(dto.dificultad());
        if (dto.diasEstudio() != null) exp.setDiasEstudio(dto.diasEstudio());
        if (dto.horasDiarias() != null) exp.setHorasDiarias(dto.horasDiarias());
        if (dto.intentosPrevios() != null) exp.setIntentosPrevios(dto.intentosPrevios());
        if (dto.modalidad() != null) exp.setModalidad(dto.modalidad());
        if (dto.recursos() != null) exp.setRecursos(dto.recursos());
        if (dto.motivacion() != null) exp.setMotivacion(dto.motivacion());
        if (dto.linkResumen() != null) exp.setLinkResumen(dto.linkResumen());

        return ExperienciaResponse.fromEntity(experienciaRepo.save(exp));
    }

    @Transactional
    public void eliminarExperiencia(Long id) {
        var exp = experienciaRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Experiencia no encontrada"));

        // Validar autoría navegando el grafo
        var propietarioId = exp.getExamen().getRenglon().getHistoriaAcademica().getEstudiante().getSupabaseUserId();
        securityValidator.validarAutoria(propietarioId);

        experienciaRepo.BorrarExperiencia(id);
    }

    @Transactional(readOnly = true)
    public List<ExamenDisponibleResponse> obtenerExamenesPendientesDeExperiencia(Long estudianteId) {
        if (!estudianteRepo.existsById(estudianteId)) {
            throw new ResourceNotFoundException("Estudiante no encontrado con ID: " + estudianteId);
        }

        HistoriaAcademica HAactiva = historiaAcademicaRepo.findByEstudiante_IdAndEstado(estudianteId, "ACTIVA").orElseThrow();

        return examenRepo.findAllByHistoriaAcademica(HAactiva).stream()
                .map(ExamenDisponibleResponse::fromEntity)
                .toList();
    }
}
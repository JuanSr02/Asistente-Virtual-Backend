package com.asistenteVirtual.modules.historiaAcademica.service;

import com.asistenteVirtual.common.exceptions.ResourceNotFoundException;
import com.asistenteVirtual.modules.estudiante.model.Estudiante;
import com.asistenteVirtual.modules.estudiante.repository.EstudianteRepository;
import com.asistenteVirtual.modules.historiaAcademica.dto.HistoriaAcademicaResponse;
import com.asistenteVirtual.modules.historiaAcademica.model.HistoriaAcademica;
import com.asistenteVirtual.modules.historiaAcademica.repository.HistoriaAcademicaRepository;
import com.asistenteVirtual.modules.security.service.SecurityValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistoriaAcademicaService {

    private final HistoriaImportService historiaImportService;
    private final HistoriaAcademicaRepository historiaRepository;
    private final EstudianteRepository estudianteRepository;
    private final SecurityValidator securityValidator;

    /**
     * Carga o Actualiza la historia académica.
     * Aprovechamos la lógica idempotente de 'HistoriaImportService' que ya hace merge inteligente.
     */
    @Transactional
    public HistoriaAcademicaResponse procesarHistoria(MultipartFile file, Long estudianteId, String codigoPlan) throws IOException {
        securityValidator.validarAccesoEstudiante(estudianteId);
        HistoriaAcademica historia = historiaImportService.cargarHistoria(file, estudianteId, codigoPlan);
        return HistoriaAcademicaResponse.fromEntity(historia);
    }

    /**
     * Eliminación lógica (Soft Delete)
     */
    @Transactional
    public void eliminarHistoria(Long estudianteId) {
        securityValidator.validarAccesoEstudiante(estudianteId);
        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado"));

        HistoriaAcademica historia = historiaRepository.findByEstudiante_IdAndEstado(estudianteId, "ACTIVA")
                .orElseThrow(() -> new ResourceNotFoundException("El estudiante no tiene historia académica activa"));

        historia.setEstado("BAJA");
        historiaRepository.save(historia);

    }

    @Transactional(readOnly = true)
    public HistoriaAcademicaResponse obtenerHistoria(Long estudianteId) {
        securityValidator.validarAccesoEstudiante(estudianteId);
        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado"));

        return historiaRepository.findByEstudiante(estudiante)
                .filter(h -> !"BAJA".equals(h.getEstado()))
                .map(HistoriaAcademicaResponse::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Historia académica no encontrada"));
    }
}
package com.asistenteVirtual.modules.historiaAcademica.service;

import com.asistenteVirtual.common.exceptions.UnsupportedFileTypeException;
import com.asistenteVirtual.modules.estudiante.model.Estudiante;
import com.asistenteVirtual.modules.estudiante.repository.EstudianteRepository;
import com.asistenteVirtual.modules.historiaAcademica.model.HistoriaAcademica;
import com.asistenteVirtual.modules.historiaAcademica.repository.HistoriaAcademicaRepository;
import com.asistenteVirtual.modules.historiaAcademica.service.parser.HistoriaFileParser;
import com.asistenteVirtual.modules.historiaAcademica.service.parser.ParserFactory;
import com.asistenteVirtual.modules.planEstudio.model.PlanDeEstudio;
import com.asistenteVirtual.modules.planEstudio.repository.PlanDeEstudioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistoriaImportService {

    private final ParserFactory parserFactory; // Fábrica para elegir Excel o PDF
    private final HistoriaAcademicaRepository historiaRepository;
    private final EstudianteRepository estudianteRepository;
    private final PlanDeEstudioRepository planRepository;
    private final HistoriaProcesadorDatos procesadorDatos; // Lógica de validación y guardado de renglones

    @Transactional
    public HistoriaAcademica cargarHistoria(MultipartFile file, Long estudianteId, String codigoPlan) throws IOException {
        // 1. Validaciones previas
        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
        
        PlanDeEstudio plan = planRepository.findById(codigoPlan)
                .orElseThrow(() -> new RuntimeException("Plan de estudio no encontrado"));

        // 2. Selección del Parser adecuado (Strategy Pattern)
        HistoriaFileParser parser = parserFactory.getParser(file);
        
        // 3. Extracción de datos crudos (sin lógica de negocio aún)
        var datosExtraidos = parser.parse(file);

        // 4. Obtener o crear la historia
        HistoriaAcademica historia = historiaRepository.findByEstudiante_IdAndEstado(estudianteId, "ACTIVA")
                .orElseGet(() -> crearHistoriaNueva(estudiante, plan));

        // 5. Procesamiento inteligente (Validación de plan, duplicados, reglas de negocio)
        procesadorDatos.procesarDatos(historia, datosExtraidos, plan);

        return historiaRepository.save(historia);
    }

    private HistoriaAcademica crearHistoriaNueva(Estudiante estudiante, PlanDeEstudio plan) {
        return HistoriaAcademica.builder()
                .estudiante(estudiante)
                .planDeEstudio(plan)
                .estado("ACTIVA")
                .build();
    }
}
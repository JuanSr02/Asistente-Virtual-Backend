package com.recommendationSys.Sistema_Recomendador_Finales.services.historiaAcademica;

import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.HistoriaAcademicaResponseDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.exceptions.ResourceNotFoundException;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Estudiante;
import com.recommendationSys.Sistema_Recomendador_Finales.model.HistoriaAcademica;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.EstudianteRepository;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.HistoriaAcademicaRepository;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.RenglonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class HistoriaAcademicaServiceImpl implements HistoriaAcademicaService {

    private final HistoriaAcademicaRepository historiaRepo;
    private final RenglonRepository renglonRepo;
    private final EstudianteRepository estudianteRepo;
    private final ExcelProcessingService excelProcessingService;
    private final HistoriaAcademicaValidator validator;

    @Override
    public HistoriaAcademicaResponseDTO cargarHistoriaAcademica(MultipartFile file, Long estudianteId, String codigoPlan) throws IOException {
        validator.validarEstudiante(estudianteId);
        validator.validarHistoria(estudianteId);
        HistoriaAcademica historia = excelProcessingService.procesarArchivoExcel(file, estudianteId, codigoPlan);
        Long renglonesCargados = renglonRepo.countByHistoriaAcademica(historia);
        return HistoriaAcademicaResponseDTO.builder()
                .nombreCompleto(historia.getEstudiante().getNombreApellido())
                .codigoPlan(historia.getPlanDeEstudio().getCodigo())
                .fechaUltimaActualizacion(LocalDate.now())
                .renglonesCargados(renglonesCargados)
                .build();
    }

    @Override
    public HistoriaAcademicaResponseDTO actualizarHistoriaAcademica(MultipartFile file, Long estudianteId, String codigoPlan) throws IOException {
        validator.validarEstudiante(estudianteId);
        Long renglonesOriginales = renglonRepo.countByHistoriaAcademica(historiaRepo.findByEstudiante(estudianteRepo.findById(estudianteId).orElseThrow()).orElseThrow());
        HistoriaAcademica historia = excelProcessingService.procesarArchivoExcelActualizacion(file, estudianteId, codigoPlan);
        Long renglonesCargados = renglonRepo.countByHistoriaAcademica(historia);
        return HistoriaAcademicaResponseDTO.builder()
                .nombreCompleto(historia.getEstudiante().getNombreApellido())
                .codigoPlan(historia.getPlanDeEstudio().getCodigo())
                .fechaUltimaActualizacion(LocalDate.now())
                .renglonesCargados(renglonesCargados - renglonesOriginales)
                .build();
    }


    @Override
    public void eliminarHistoriaAcademica(Long estudianteId) {
        Estudiante estudiante = estudianteRepo.findById(estudianteId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado"));

        HistoriaAcademica historia = estudiante.getHistoriaAcademica();
        if (historia != null) {
            eliminarHistoriaCompleta(estudiante, historia);
        }
    }

    private void eliminarHistoriaCompleta(Estudiante estudiante, HistoriaAcademica historia) {
        // Romper relación bidireccional
        estudiante.setHistoriaAcademica(null);
        historia.setEstudiante(null);

        // Eliminar renglones (orphanRemoval se encarga de los exámenes)
        historia.getRenglones().clear();

        // Guardar cambios
        estudianteRepo.save(estudiante);
        historiaRepo.delete(historia);
        historiaRepo.flush();
    }
}
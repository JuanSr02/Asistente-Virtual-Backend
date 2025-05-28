package com.recommendationSys.Sistema_Recomendador_Finales.services.historiaAcademica;

import com.recommendationSys.Sistema_Recomendador_Finales.exceptions.ResourceNotFoundException;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Estudiante;
import com.recommendationSys.Sistema_Recomendador_Finales.model.HistoriaAcademica;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class HistoriaAcademicaServiceImpl implements HistoriaAcademicaService {

    private final HistoriaAcademicaRepository historiaRepo;
    private final EstudianteRepository estudianteRepo;
    private final PlanDeEstudioRepository planRepo;
    private final MateriaRepository materiaRepo;
    private final RenglonRepository renglonRepo;
    private final ExamenRepository examenRepo;
    private final ExcelProcessingService excelProcessingService;
    private final HistoriaAcademicaValidator validator;

    @Override
    public void cargarHistoriaAcademica(MultipartFile file, Long estudianteId) throws IOException {
        validator.validarEstudiante(estudianteId);
        validator.validarHistoria(estudianteId);
        excelProcessingService.procesarArchivoExcel(file, estudianteId);
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
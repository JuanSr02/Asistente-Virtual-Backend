package com.recommendationSys.Sistema_Recomendador_Finales.services;

import com.recommendationSys.Sistema_Recomendador_Finales.model.*;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.*;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Service
@Transactional
public class HistoriaAcademicaService {

    private final HistoriaAcademicaRepository historiaRepo;
    private final EstudianteRepository estudianteRepo;
    private final PlanDeEstudioRepository planRepo;
    private final MateriaRepository materiaRepo;
    private final RenglonRepository renglonRepo;
    private final ExamenRepository examenRepo;

    public HistoriaAcademicaService(HistoriaAcademicaRepository historiaRepo,
                                    EstudianteRepository estudianteRepo,
                                    PlanDeEstudioRepository planRepo,
                                    MateriaRepository materiaRepo,
                                    RenglonRepository renglonRepo,
                                    ExamenRepository examenRepo) {
        this.historiaRepo = historiaRepo;
        this.estudianteRepo = estudianteRepo;
        this.planRepo = planRepo;
        this.materiaRepo = materiaRepo;
        this.renglonRepo = renglonRepo;
        this.examenRepo = examenRepo;
    }

    public void cargarHistoriaAcademica(MultipartFile file, Long estudianteId) throws IOException {
        if (!estudianteRepo.existsById(estudianteId)) {
            throw new RuntimeException("No se encontró el estudiante con ID " + estudianteId);
        }

        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        String nombrePlan = sheet.getRow(3).getCell(0).getStringCellValue().trim();
        PlanDeEstudio plan = (PlanDeEstudio) planRepo.findByPropuesta(nombrePlan)
                .orElseThrow(() -> new RuntimeException("No se encontró el plan de estudio: " + nombrePlan));

        Estudiante estudiante = estudianteRepo.findById(estudianteId).get();

        HistoriaAcademica historia = historiaRepo.findByEstudiante(estudiante)
                .orElseGet(() -> {
                    HistoriaAcademica h = new HistoriaAcademica();
                    h.setEstudiante(estudiante);
                    h.setPlanDeEstudio(plan);
                    return historiaRepo.save(h);
                });

        int lastRowWithData = 0;
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            if (sheet.getRow(i) != null && !ServiceUtil.isEmptyRow(sheet.getRow(i))) {
                lastRowWithData = i;
            }
        }

        for (int i = 6; i <= lastRowWithData; i++) {
            Row row = sheet.getRow(i);
            if (row == null || row.getCell(0) == null) continue;
            String actividad = row.getCell(0).getStringCellValue().trim();
            actividad = actividad.substring(0,actividad.indexOf("(")).trim();
            LocalDate fecha = row.getCell(1).getLocalDateTimeCellValue().toLocalDate();
            String tipo = row.getCell(2).getStringCellValue().trim();
            BigDecimal nota = null;
            if(!ServiceUtil.checkCell(row.getCell(3)).equalsIgnoreCase("")){
                nota = BigDecimal.valueOf(row.getCell(3).getNumericCellValue());}
            String resultado = row.getCell(4).getStringCellValue().trim();

            if ("En curso".equalsIgnoreCase(tipo)) continue;
            if ("Regularidad".equalsIgnoreCase(tipo) &&
                    ("Reprobado".equalsIgnoreCase(resultado) || "Ausente".equalsIgnoreCase(resultado))) continue;

            String finalActividad = actividad;
            Materia materia = materiaRepo.findByNombreAndPlanDeEstudio(actividad, plan)
                    .orElseThrow(() -> new RuntimeException("No se encontró la materia: " + finalActividad));

            if ("Regularidad".equalsIgnoreCase(tipo)) {
                boolean hayExamenAprobado = renglonRepo.existsByMateriaCodigoAndHistoriaIdAndTipoAndNotaGreaterThanEqual(
                        materia.getCodigo(), historia.getId(), "Examen", 4.0);

                if (hayExamenAprobado) continue;

                Renglon renglon = new Renglon(fecha.toString(), tipo, nota, resultado, historia, materia);
                renglonRepo.save(renglon);

            } else if ("Examen".equalsIgnoreCase(tipo)) {
                Renglon renglon = new Renglon(fecha.toString(), tipo, nota, resultado, historia, materia);
                renglonRepo.save(renglon);

                Examen examen = new Examen(fecha.toString(), nota, renglon);
                examenRepo.save(examen);

                if (nota.doubleValue() >= 4.0) {
                    renglonRepo.findByMateriaCodigoAndHistoriaIdAndTipoAndResultado(
                            materia.getCodigo(), historia.getId(), "Regularidad", "Aprobado"
                    ).ifPresent(renglonRepo::delete);
                }
            }
        }
    }
}

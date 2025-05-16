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
            if (sheet.getRow(i) != null && !ExcelServicesUtil.isEmptyRow(sheet.getRow(i))) {
                lastRowWithData = i;
            }
        }

        for (int i = 6; i <= lastRowWithData; i++) {
            Row row = sheet.getRow(i);
            String nombreMateria = row.getCell(0).getStringCellValue().trim();
            nombreMateria = nombreMateria.substring(0,nombreMateria.indexOf("(")).trim();
            String fecha = row.getCell(1).getStringCellValue().trim();
            String tipo = row.getCell(2).getStringCellValue().trim();
            Double nota = null;
            if(!ExcelServicesUtil.checkCell(row.getCell(3)).trim().equalsIgnoreCase("")){
                nota = Double.parseDouble(row.getCell(3).getStringCellValue().trim());}
            String resultado = row.getCell(4).getStringCellValue().trim();

            if ("En curso".equalsIgnoreCase(tipo)) continue;
            if ("Regularidad".equalsIgnoreCase(tipo) &&
                    ("Reprobado".equalsIgnoreCase(resultado) || "Ausente".equalsIgnoreCase(resultado))) continue;

            String finalNombreMateria = nombreMateria;
            Materia materia = materiaRepo.findByNombreAndPlanDeEstudio(nombreMateria, plan)
                    .orElseThrow(() -> new RuntimeException("No se encontró la materia: " + finalNombreMateria));

            if ("Regularidad".equalsIgnoreCase(tipo)) {
                boolean hayExamenAprobado = renglonRepo.existsByMateriaAndHistoriaAcademicaAndTipoAndNotaGreaterThanEqual(
                        materia, historia, "Examen", 4.0);
                boolean estaPromocionada = renglonRepo.existsByMateriaAndHistoriaAcademicaAndTipoAndResultado(materia,historia,"Promocion","Promocionado");

                if (hayExamenAprobado || estaPromocionada) continue;

                Renglon renglon = new Renglon(fecha, tipo, nota, resultado, historia, materia);
                renglonRepo.save(renglon);

            } else if ("Examen".equalsIgnoreCase(tipo)) {
                Renglon renglon = new Renglon(fecha, tipo, nota, resultado, historia, materia);
                renglonRepo.save(renglon);

                Examen examen = new Examen(fecha, nota, renglon);
                examenRepo.save(examen);

                if (nota.doubleValue() >= 4.0) {
                    renglonRepo.findByMateriaAndHistoriaAcademicaAndTipoAndResultado(
                            materia, historia, "Regularidad", "Aprobado"
                    ).ifPresent(renglonRepo::delete);
                }
            }
            if ("Promocion".equalsIgnoreCase(tipo) && "Promocionado".equalsIgnoreCase(resultado)){
                Renglon renglon = new Renglon(fecha, tipo, nota, resultado, historia, materia);
                renglonRepo.save(renglon);
                renglonRepo.findByMateriaAndHistoriaAcademicaAndTipoAndResultado(
                        materia, historia, "Regularidad", "Aprobado"
                ).ifPresent(renglonRepo::delete);
            }
        }
        renglonRepo.deleteByTipoAndResultado("Promocion","Promocionado");
    }

    @Transactional
    public void eliminarHistoriaAcademica(Long estudianteId) {
        Estudiante estudiante = estudianteRepo.findById(estudianteId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        // Obtener y romper la relación bidireccional primero
        HistoriaAcademica historia = estudiante.getHistoriaAcademica();
        if (historia != null) {
            // 1. Romper la relación en ambos lados
            estudiante.setHistoriaAcademica(null);
            historia.setEstudiante(null);

            // 2. Eliminar los renglones y exámenes (orphanRemoval se encargará de esto)
            historia.getRenglones().clear();

            // 3. Actualizar el estudiante primero
            estudianteRepo.save(estudiante);

            // 4. Eliminar la historia académica
            historiaRepo.delete(historia);

            // 5. Forzar sincronización con la BD
            historiaRepo.flush();
        }
    }

}

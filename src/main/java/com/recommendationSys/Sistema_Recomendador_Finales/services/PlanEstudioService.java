package com.recommendationSys.Sistema_Recomendador_Finales.services;

import com.recommendationSys.Sistema_Recomendador_Finales.model.Correlativa;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Materia;
import com.recommendationSys.Sistema_Recomendador_Finales.model.PlanDeEstudio;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.CorrelativaRepository;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.MateriaRepository;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.PlanDeEstudioRepository;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLOutput;

@Service
@Transactional
public class PlanEstudioService {

    private final PlanDeEstudioRepository planRepo;
    private final MateriaRepository materiaRepo;
    private final CorrelativaRepository correlativaRepo;

    public PlanEstudioService(PlanDeEstudioRepository planRepo,
                              MateriaRepository materiaRepo,
                              CorrelativaRepository correlativaRepo) {
        this.planRepo = planRepo;
        this.materiaRepo = materiaRepo;
        this.correlativaRepo = correlativaRepo;
    }

    public void procesarArchivoExcel(MultipartFile file) throws IOException {
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        // Leer datos del plan
        Row planRow = sheet.getRow(1);
        String propuesta = planRow.getCell(0).getStringCellValue();
        String codigoPlan = planRow.getCell(1).getStringCellValue();
        System.out.println(propuesta +"  "+ codigoPlan);

        // Crear o actualizar plan
        PlanDeEstudio plan = planRepo.findById(codigoPlan)
                .orElse(new PlanDeEstudio());
        plan.setCodigo(codigoPlan);
        plan.setPropuesta(propuesta);
        // Después de guardar el plan, verifica que tiene un código
        planRepo.save(plan);
        System.out.println("Plan guardado con código: " + plan.getCodigo()); // Verifica esto

        int lastRowWithData = 0;
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            if (sheet.getRow(i) != null && !isEmptyRow(sheet.getRow(i))) {
                lastRowWithData = i;
            }
        }

        // Procesar materias (empezando desde la fila 3)
        for (int i = 4; i <= lastRowWithData; i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            String codigoMateria = String.valueOf((int) row.getCell(1).getNumericCellValue());
            String nombreMateria = row.getCell(0).getStringCellValue();
            System.out.println(codigoMateria +"  "+ nombreMateria);

            Materia materia = materiaRepo.findById(codigoMateria)
                    .orElse(new Materia());
            materia.setCodigo(codigoMateria);
            materia.setNombre(nombreMateria);
            materia.setPlanDeEstudio(plan);
            materiaRepo.save(materia);

            // Procesar correlativas
            String correlativasStr = "";
            Cell cell = row.getCell(5);
            switch (cell.getCellType()) {
                case STRING:
                    correlativasStr = cell.getStringCellValue();
                    break;
                case NUMERIC:
                    correlativasStr = String.valueOf(cell.getNumericCellValue());
                    break;
                case BLANK:
                    break;
                default:
                    throw new IllegalArgumentException("Tipo de celda no soportado: " + cell.getCellType());
            }
            Materia materia1;
            if (!"No tiene".equalsIgnoreCase(correlativasStr)) {
                String[] codigosCorrelativas = correlativasStr.split("-");
                for (String codigoCorrelativa : codigosCorrelativas) {
                    if (!codigoCorrelativa.trim().isEmpty()) {
                        String codigoTrimmed = codigoCorrelativa.trim();
                        if (materiaRepo.existsById(codigoTrimmed)) {
                            Materia materiaCorrelativa = materiaRepo.findById(codigoTrimmed).get();
                            Correlativa correlativa = new Correlativa();
                            correlativa.setMateria(materia);
                            correlativa.setCorrelativa(materiaCorrelativa);
                            correlativa.setPlanDeEstudio(plan);
                            System.out.println("Plan código antes de correlativas: " + plan.getCodigo());
                            System.out.println("Guardando correlativa con plan: " + correlativa.getPlanDeEstudio().getCodigo());
                            correlativaRepo.save(correlativa);
                        }

                    }
                    }
                }
            }
        }
    // Metodo auxiliar para verificar filas vacías
    private boolean isEmptyRow(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

}


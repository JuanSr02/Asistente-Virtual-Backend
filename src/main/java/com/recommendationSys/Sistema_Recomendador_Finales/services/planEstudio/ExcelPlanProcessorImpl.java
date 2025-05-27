package com.recommendationSys.Sistema_Recomendador_Finales.services.planEstudio;

import com.recommendationSys.Sistema_Recomendador_Finales.model.Materia;
import com.recommendationSys.Sistema_Recomendador_Finales.model.PlanDeEstudio;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.CorrelativaRepository;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.MateriaRepository;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.PlanDeEstudioRepository;
import com.recommendationSys.Sistema_Recomendador_Finales.services.ExcelProcessingUtils;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ExcelPlanProcessorImpl implements ExcelPlanProcessor {

    private final PlanDeEstudioRepository planRepo;
    private final MateriaRepository materiaRepo;
    private final CorrelativaRepository correlativaRepo;
    private final PlanValidator planValidator;
    private final PlanMapper planMapper;
    private final CorrelativaProcessor correlativaProcessor;

    @Override
    public void procesarContenidoPlan(MultipartFile file) throws IOException {
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        // Procesar fila del plan
        Row planRow = sheet.getRow(1);
        planValidator.validarFilaPlan(planRow);

        PlanDeEstudio plan = planMapper.mapearPlanDesdeFila(planRow);
        planRepo.save(plan);

        // Procesar materias
        int lastRowWithData = ExcelProcessingUtils.obtenerUltimaFilaConDatos(sheet);
        for (int i = 4; i <= lastRowWithData; i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            planValidator.validarFilaMateria(row, i);
            procesarFilaMateria(row, plan);
        }
    }

    private void procesarFilaMateria(Row row, PlanDeEstudio plan) {
        Materia materia = planMapper.mapearMateriaDesdeFila(row, plan);
        materiaRepo.save(materia);

        String correlativasStr = ExcelProcessingUtils.extractCellValue(row.getCell(5));
        correlativaProcessor.procesarCorrelativas(correlativasStr, materia, plan);
    }
}
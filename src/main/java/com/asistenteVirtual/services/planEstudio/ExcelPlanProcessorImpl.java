package com.asistenteVirtual.services.planEstudio;

import com.asistenteVirtual.model.Correlativa;
import com.asistenteVirtual.model.Materia;
import com.asistenteVirtual.model.PlanDeEstudio;
import com.asistenteVirtual.repository.CorrelativaRepository;
import com.asistenteVirtual.repository.MateriaRepository;
import com.asistenteVirtual.repository.PlanDeEstudioRepository;
import com.asistenteVirtual.services.ExcelProcessingUtils;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExcelPlanProcessorImpl implements ExcelPlanProcessor {

    private final PlanDeEstudioRepository planRepo;
    private final MateriaRepository materiaRepo;
    private final PlanValidator planValidator;
    private final PlanMapper planMapper;
    private final CorrelativaProcessor correlativaProcessor;
    private final CorrelativaRepository correlativaRepo;


    @Override
    public PlanDeEstudio procesarContenidoPlan(MultipartFile file) throws IOException {
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        // --- 1. Crear y guardar el plan ---
        Row planRow = sheet.getRow(1);
        planValidator.validarFilaPlan(planRow);

        PlanDeEstudio plan = planMapper.mapearPlanDesdeFila(planRow);
        plan = planRepo.save(plan);
        
        // --- 2. Crear y guardar todas las materias ---
        int lastRowWithData = ExcelProcessingUtils.obtenerUltimaFilaConDatos(sheet);
        List<Materia> materias = new ArrayList<>();

        for (int i = 4; i <= lastRowWithData; i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            planValidator.validarFilaMateria(row, i);
            Materia materia = planMapper.mapearMateriaDesdeFila(row, plan);
            materias.add(materia);
        }

        List<Materia> materiasGuardadas = materiaRepo.saveAll(materias);
        Map<String, Materia> cacheMaterias = new HashMap<>();
        for (Materia m : materiasGuardadas) {
            cacheMaterias.put(m.getCodigo(), m);
        }

        // --- 3. Crear y guardar todas las correlativas ---
        List<Correlativa> correlativas = new ArrayList<>();
        for (int i = 4; i <= lastRowWithData; i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            String codigoMateria = ExcelProcessingUtils.extractCellValue(row.getCell(1));
            Materia materia = cacheMaterias.get(codigoMateria);
            if (materia == null) continue;

            String correlativasStr = ExcelProcessingUtils.extractCellValue(row.getCell(5));
            correlativas.addAll(correlativaProcessor.generarCorrelativasConCache(
                    correlativasStr, materia, cacheMaterias, plan));
        }

        correlativaRepo.saveAll(correlativas);

        return plan;
    }


}
package com.recommendationSys.Sistema_Recomendador_Finales.services.planEstudio;

import com.recommendationSys.Sistema_Recomendador_Finales.model.Materia;
import com.recommendationSys.Sistema_Recomendador_Finales.model.PlanDeEstudio;
import com.recommendationSys.Sistema_Recomendador_Finales.services.ExcelProcessingUtils;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Service;

@Service
public class PlanMapper {

    public PlanDeEstudio mapearPlanDesdeFila(Row planRow) {
        String propuesta = ExcelProcessingUtils.extractCellValue(planRow.getCell(0));
        String codigoPlan = ExcelProcessingUtils.extractCellValue(planRow.getCell(1));

        return PlanDeEstudio.builder()
                .codigo(codigoPlan)
                .propuesta(propuesta.substring(0, propuesta.indexOf("(")).trim())
                .build();
    }

    public Materia mapearMateriaDesdeFila(Row row, PlanDeEstudio plan) {
        String codigoMateria = ExcelProcessingUtils.extractCellValue(row.getCell(1));
        String nombreMateria = ExcelProcessingUtils.extractCellValue(row.getCell(0));

        return Materia.builder()
                .codigo(codigoMateria)
                .nombre(nombreMateria)
                .planDeEstudio(plan)
                .build();
    }
}
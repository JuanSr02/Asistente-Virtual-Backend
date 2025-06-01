package com.recommendationSys.Sistema_Recomendador_Finales.services.planEstudio;

import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.PlanEstudioResponseDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Materia;
import com.recommendationSys.Sistema_Recomendador_Finales.model.PlanDeEstudio;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.MateriaRepository;
import com.recommendationSys.Sistema_Recomendador_Finales.services.ExcelProcessingUtils;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlanMapper {
    private final MateriaRepository materiaRepo;

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
    public PlanEstudioResponseDTO toResponseDTO(PlanDeEstudio plan) {
        return PlanEstudioResponseDTO.builder()
                .codigo(plan.getCodigo())
                .propuesta(plan.getPropuesta())
                .cantidadMateriasCargadas(materiaRepo.ContarByPlanCodigo(plan.getCodigo()))
                .build();
    }

}
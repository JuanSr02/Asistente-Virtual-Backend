package com.asistenteVirtual.services.planEstudio;

import com.asistenteVirtual.DTOs.PlanEstudioResponseDTO;
import com.asistenteVirtual.model.Materia;
import com.asistenteVirtual.model.PlanDeEstudio;
import com.asistenteVirtual.repository.MateriaRepository;
import com.asistenteVirtual.services.ExcelProcessingUtils;
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
        if(propuesta.contains("(")){
            propuesta = propuesta.substring(0, propuesta.indexOf("(")).trim();
        }
        return PlanDeEstudio.builder()
                .codigo(codigoPlan)
                .propuesta(propuesta)
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
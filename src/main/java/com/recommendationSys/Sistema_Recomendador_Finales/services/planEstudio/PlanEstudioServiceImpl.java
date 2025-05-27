package com.recommendationSys.Sistema_Recomendador_Finales.services.planEstudio;

import com.recommendationSys.Sistema_Recomendador_Finales.exceptions.PlanEstudioException;
import com.recommendationSys.Sistema_Recomendador_Finales.model.PlanDeEstudio;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.PlanDeEstudioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Transactional
@RequiredArgsConstructor
public class PlanEstudioServiceImpl implements PlanEstudioService {

    private final ExcelPlanProcessor excelPlanProcessor;
    private final PlanDeEstudioRepository planRepo;
    private final PlanValidator planValidator;

    @Override
    public void procesarArchivoExcel(MultipartFile file) throws IOException {
        planValidator.validarArchivo(file);
        excelPlanProcessor.procesarContenidoPlan(file);
    }

    @Override
    public void eliminarPlanDeEstudio(String codigoPlan) {
        PlanDeEstudio plan = planRepo.findById(codigoPlan)
                .orElseThrow(() -> new PlanEstudioException(
                        String.format("El plan con código '%s' no existe", codigoPlan),
                        HttpStatus.NOT_FOUND));

        try {
            planRepo.delete(plan);
        } catch (DataIntegrityViolationException e) {
            throw new PlanEstudioException(
                    "No se puede eliminar el plan porque tiene referencias activas",
                    HttpStatus.CONFLICT);
        }
    }
}
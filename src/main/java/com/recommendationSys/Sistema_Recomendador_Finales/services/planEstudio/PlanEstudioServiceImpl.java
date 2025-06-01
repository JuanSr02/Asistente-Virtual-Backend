package com.recommendationSys.Sistema_Recomendador_Finales.services.planEstudio;

import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.PlanEstudioResponseDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.exceptions.IntegrityException;
import com.recommendationSys.Sistema_Recomendador_Finales.exceptions.ResourceNotFoundException;
import com.recommendationSys.Sistema_Recomendador_Finales.model.PlanDeEstudio;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.PlanDeEstudioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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
    private final PlanMapper planMapper;

    @Override
    @Transactional
    public PlanEstudioResponseDTO procesarArchivoExcel(MultipartFile file) throws IOException {
        planValidator.validarArchivo(file);
        return planMapper.toResponseDTO(excelPlanProcessor.procesarContenidoPlan(file));
    }

    @Override
    public void eliminarPlanDeEstudio(String codigoPlan) {
        PlanDeEstudio plan = planRepo.findById(codigoPlan)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("El plan con código '%s' no existe", codigoPlan)));

        try {
            planRepo.delete(plan);
        } catch (DataIntegrityViolationException e) {
            throw new IntegrityException(
                    "No se puede eliminar el plan porque tiene referencias activas");
        }
    }
}
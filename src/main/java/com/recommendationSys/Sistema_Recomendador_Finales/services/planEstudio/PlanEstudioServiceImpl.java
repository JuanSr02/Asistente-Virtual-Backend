package com.recommendationSys.Sistema_Recomendador_Finales.services.planEstudio;

import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.MateriaDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.PlanEstudioResponseDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.exceptions.IntegrityException;
import com.recommendationSys.Sistema_Recomendador_Finales.exceptions.ResourceNotFoundException;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Materia;
import com.recommendationSys.Sistema_Recomendador_Finales.model.PlanDeEstudio;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.MateriaRepository;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.PlanDeEstudioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PlanEstudioServiceImpl implements PlanEstudioService {

    private final ExcelPlanProcessor excelPlanProcessor;
    private final PlanDeEstudioRepository planRepo;
    private final PlanValidator planValidator;
    private final PlanMapper planMapper;
    private final MateriaRepository materiaRepo;

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
    @Override
    public List<PlanEstudioResponseDTO> obtenerPlanes(){
        List<PlanEstudioResponseDTO> planes = new ArrayList<>();
        for (PlanDeEstudio p : planRepo.findAll()){
            planes.add(planMapper.toResponseDTO(p));
        }
        return planes;
    }

    @Override
    public List<MateriaDTO> obtenerMateriasPorPlan(String codigoPlan){
        List<MateriaDTO> materiaDTOList = new ArrayList<>();
        for(Materia m : materiaRepo.findByPlanDeEstudio_Codigo(codigoPlan)){
            materiaDTOList.add(MateriaDTO.toMateriaDTO(m));
        }
        return materiaDTOList;
    }

}
package com.asistenteVirtual.modules.planEstudio.service;

import com.asistenteVirtual.common.exceptions.IntegrityException;
import com.asistenteVirtual.common.exceptions.ResourceNotFoundException;
import com.asistenteVirtual.modules.planEstudio.dto.MateriaResponse;
import com.asistenteVirtual.modules.planEstudio.dto.PlanEstudioResponse;
import com.asistenteVirtual.modules.planEstudio.model.PlanDeEstudio;
import com.asistenteVirtual.modules.planEstudio.repository.MateriaRepository;
import com.asistenteVirtual.modules.planEstudio.repository.PlanDeEstudioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlanEstudioService {

    private final PlanDeEstudioRepository planRepo;
    private final MateriaRepository materiaRepo;
    private final PlanImportService planImportService; // Delegamos la complejidad del Excel aquí

    @Transactional
    public PlanEstudioResponse cargarPlanDesdeExcel(MultipartFile file) throws IOException {
        PlanDeEstudio planGuardado = planImportService.procesarArchivoExcel(file);
        long cantidadMaterias = materiaRepo.ContarByPlanCodigo(planGuardado.getCodigo());

        return new PlanEstudioResponse(
                planGuardado.getCodigo(),
                planGuardado.getPropuesta(),
                cantidadMaterias
        );
    }

    @Transactional
    public void eliminarPlanDeEstudio(String codigoPlan) {
        if (!planRepo.existsById(codigoPlan)) {
            throw new ResourceNotFoundException("El plan con código '" + codigoPlan + "' no existe");
        }
        try {
            planRepo.deleteById(codigoPlan);
            log.info("Plan de estudio eliminado: {}", codigoPlan);
        } catch (DataIntegrityViolationException e) {
            throw new IntegrityException("No se puede eliminar el plan porque tiene alumnos o historias asociadas.");
        }
    }

    @Transactional(readOnly = true)
    public List<PlanEstudioResponse> obtenerPlanes() {
        return planRepo.findAll().stream()
                .map(plan -> new PlanEstudioResponse(
                        plan.getCodigo(),
                        plan.getPropuesta(),
                        (long) plan.getMaterias().size() // OJO: Esto puede ser lento si no es Lazy, usar count query es mejor
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MateriaResponse> obtenerMateriasPorPlan(String codigoPlan) {
        return materiaRepo.findByPlanDeEstudio_Codigo(codigoPlan).stream()
                .map(m -> new MateriaResponse(m.getCodigo(), m.getNombre()))
                .toList();
    }
}
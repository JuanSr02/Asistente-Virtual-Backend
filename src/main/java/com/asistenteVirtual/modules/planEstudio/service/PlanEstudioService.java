package com.asistenteVirtual.modules.planEstudio.service;

import com.asistenteVirtual.common.exceptions.ResourceNotFoundException;
import com.asistenteVirtual.modules.planEstudio.dto.MateriaResponse;
import com.asistenteVirtual.modules.planEstudio.dto.PlanEstudioResponse;
import com.asistenteVirtual.modules.planEstudio.model.PlanDeEstudio;
import com.asistenteVirtual.modules.planEstudio.repository.MateriaRepository;
import com.asistenteVirtual.modules.planEstudio.repository.PlanDeEstudioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final PlanImportService planImportService;

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
        // 1. Validación de existencia
        if (!planRepo.existsById(codigoPlan)) {
            throw new ResourceNotFoundException("El plan con código '" + codigoPlan + "' no existe");
        }

        // 2. Borrado en Cascada (Gestionado por JPA)
        // Al llamar a deleteById, Hibernate buscará el Plan, verá que tiene Materias e Historias
        // con CascadeType.ALL, y emitirá los DELETEs de los hijos antes de borrar el Plan.
        planRepo.deleteById(codigoPlan);

        log.info("Plan de estudio {} eliminado exitosamente junto con todas sus dependencias.", codigoPlan);
    }

    @Transactional(readOnly = true)
    public List<PlanEstudioResponse> obtenerPlanes() {
        return planRepo.findAll().stream()
                .map(plan -> new PlanEstudioResponse(
                        plan.getCodigo(),
                        plan.getPropuesta(),
                        (long) plan.getMaterias().size()
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
package com.asistenteVirtual.modules.planEstudio.controller;

import com.asistenteVirtual.modules.planEstudio.dto.MateriaResponse;
import com.asistenteVirtual.modules.planEstudio.dto.PlanEstudioResponse;
import com.asistenteVirtual.modules.planEstudio.service.PlanEstudioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PlanEstudioController {

    private final PlanEstudioService planEstudioService;

    @PostMapping(value = "/admin/planes-estudio/carga", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PlanEstudioResponse> cargarPlanDesdeExcel(
            @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(planEstudioService.cargarPlanDesdeExcel(file));
    }

    @DeleteMapping("/admin/planes-estudio")
    public ResponseEntity<Void> eliminarPlanDeEstudio(@RequestParam("codigo") String codigoPlan) {
        planEstudioService.eliminarPlanDeEstudio(codigoPlan);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/shared/planes-estudio")
    public ResponseEntity<List<PlanEstudioResponse>> obtenerTodosLosPlanes() {
        return ResponseEntity.ok(planEstudioService.obtenerPlanes());
    }

    @GetMapping("/shared/planes-estudio/materias")
    public ResponseEntity<List<MateriaResponse>> obtenerMateriasPorPlan(
            @RequestParam("codigoPlan") String codigoPlan) {
        return ResponseEntity.ok(planEstudioService.obtenerMateriasPorPlan(codigoPlan));
    }
}
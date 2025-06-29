package com.asistenteVirtual.controllers;

import com.asistenteVirtual.DTOs.MateriaDTO;
import com.asistenteVirtual.DTOs.PlanEstudioResponseDTO;
import com.asistenteVirtual.services.planEstudio.PlanEstudioService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PlanEstudioController {

    private final PlanEstudioService planEstudioService;

    /**
     * Carga un plan de estudio desde un archivo Excel
     *
     * @param file Archivo Excel con los datos del plan de estudio
     * @return Respuesta con el resultado de la operación
     */
    @PostMapping("/admin/planes-estudio/carga")
    public ResponseEntity<?> cargarPlanDesdeExcel(
            @RequestParam("file") MultipartFile file) throws IOException {

        log.info("Iniciando carga de plan de estudio desde archivo: {}", file.getOriginalFilename());
        PlanEstudioResponseDTO planCargado = planEstudioService.procesarArchivoExcel(file);
        return ResponseEntity.ok(planCargado);
    }

    /**
     * Elimina un plan de estudio
     *
     * @param codigoPlan Código único del plan de estudio (no puede estar vacío)
     * @return Respuesta con el resultado de la operación
     */
    @DeleteMapping("/admin/planes-estudio")
    public ResponseEntity<?> eliminarPlanDeEstudio(
            @RequestParam("codigo") @NotBlank String codigoPlan) {

        log.info("Eliminando plan de estudio con código: {}", codigoPlan);
        planEstudioService.eliminarPlanDeEstudio(codigoPlan);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtiene todos los planes de estudio disponibles.
     *
     * @return una lista de {@link PlanEstudioResponseDTO} que representa los planes de estudio existentes.
     */
    @GetMapping("/shared/planes-estudio")
    public ResponseEntity<List<PlanEstudioResponseDTO>> obtenerTodosLosPlanes() {
        List<PlanEstudioResponseDTO> planes = planEstudioService.obtenerPlanes();
        return ResponseEntity.ok(planes);
    }

    /**
     * Obtiene todas las materias de un plan dado.
     *
     * @return una lista de Materias (Codigo y su nombre) de un plan dado.
     */
    @GetMapping("/shared/planes-estudio/materias")
    public ResponseEntity<List<MateriaDTO>> obtenerMateriasPorPlan(@RequestParam("codigoPlan") @NotBlank String codigoPlan) {
        List<MateriaDTO> materias = planEstudioService.obtenerMateriasPorPlan(codigoPlan);
        return ResponseEntity.ok(materias);
    }
}
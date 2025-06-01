package com.recommendationSys.Sistema_Recomendador_Finales.controllers;

import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.PlanEstudioResponseDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.services.planEstudio.PlanEstudioService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/planes-estudio")
public class PlanEstudioController {

    private final PlanEstudioService planEstudioService;

    /**
     * Carga un plan de estudio desde un archivo Excel
     * @param file Archivo Excel con los datos del plan de estudio
     * @return Respuesta con el resultado de la operación
     */
    @PostMapping("/carga")
    public ResponseEntity<?> cargarPlanDesdeExcel(
            @RequestParam("file") MultipartFile file) throws IOException {

        log.info("Iniciando carga de plan de estudio desde archivo: {}", file.getOriginalFilename());
        PlanEstudioResponseDTO planCargado = planEstudioService.procesarArchivoExcel(file);
        return ResponseEntity.ok(planCargado);
    }

    /**
     * Elimina un plan de estudio
     * @param codigoPlan Código único del plan de estudio (no puede estar vacío)
     * @return Respuesta con el resultado de la operación
     */
    @DeleteMapping
    public ResponseEntity<?> eliminarPlanDeEstudio(
            @RequestParam("codigo") @NotBlank String codigoPlan) {

        log.info("Eliminando plan de estudio con código: {}", codigoPlan);
        planEstudioService.eliminarPlanDeEstudio(codigoPlan);
        return ResponseEntity.noContent().build();
    }
}
package com.recommendationSys.Sistema_Recomendador_Finales.controllers;


import com.recommendationSys.Sistema_Recomendador_Finales.exceptions.PlanEstudioException;
import com.recommendationSys.Sistema_Recomendador_Finales.services.planEstudio.PlanEstudioServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
public class PlanEstudioController {

    private final PlanEstudioServiceImpl planEstudioServiceImpl;

    public PlanEstudioController(PlanEstudioServiceImpl planEstudioServiceImpl) {
        this.planEstudioServiceImpl = planEstudioServiceImpl;
    }


    @PostMapping("/cargar-plan")
    public ResponseEntity<?> cargarPlanDesdeExcel(@RequestParam("file") MultipartFile file) {
        try {
            planEstudioServiceImpl.procesarArchivoExcel(file);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Plan de estudios cargado exitosamente",
                    "timestamp", LocalDateTime.now()
            ));
        } catch (PlanEstudioException e) {
            return ResponseEntity.status(e.getStatus()).body(Map.of(
                    "success", false,
                    "error", e.getMessage(),
                    "status", e.getStatus().value(),
                    "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", "Error interno del servidor: " + e.getMessage(),
                    "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    @DeleteMapping
    public ResponseEntity<Map<String, Object>> eliminarPlanDeEstudio(
            @RequestParam("codigo") String codigoPlan) {

        try {
            planEstudioServiceImpl.eliminarPlanDeEstudio(codigoPlan);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Plan eliminado correctamente",
                    "codigo", codigoPlan,
                    "timestamp", Instant.now()
            ));
        } catch (PlanEstudioException e) {
            return ResponseEntity.status(e.getStatus())
                    .body(errorResponse(e.getMessage(), e.getStatus(), codigoPlan));
        }
    }

    private Map<String, Object> errorResponse(String message, HttpStatus status, String codigo) {
        return Map.of(
                "status", "error",
                "message", message,
                "codigo", codigo,
                "httpStatus", status.value(),
                "timestamp", Instant.now()
        );
    }

}
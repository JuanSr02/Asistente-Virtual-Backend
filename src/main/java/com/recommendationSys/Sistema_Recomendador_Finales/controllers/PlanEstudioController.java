package com.recommendationSys.Sistema_Recomendador_Finales.controllers;


import com.recommendationSys.Sistema_Recomendador_Finales.services.PlanEstudioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/public")
public class PlanEstudioController {

    private final PlanEstudioService planEstudioService;

    public PlanEstudioController(PlanEstudioService planEstudioService) {
        this.planEstudioService = planEstudioService;
    }

    @PostMapping("/cargar-plan")
    public ResponseEntity<String> cargarPlanDesdeExcel(@RequestParam("file") MultipartFile file) {
        try {
            planEstudioService.procesarArchivoExcel(file);
            return ResponseEntity.ok("Plan de estudios cargado exitosamente");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al procesar el archivo: " + e.getMessage());
        }
    }
}

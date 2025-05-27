package com.recommendationSys.Sistema_Recomendador_Finales.controllers;

import com.recommendationSys.Sistema_Recomendador_Finales.services.historiaAcademica.HistoriaAcademicaServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
public class HistoriaAcademicaController {

    private final HistoriaAcademicaServiceImpl historiaService;

    public HistoriaAcademicaController(HistoriaAcademicaServiceImpl historiaService) {
        this.historiaService = historiaService;
    }

    @PostMapping("/cargar-historia")
    public ResponseEntity<?> cargarHistoriaDesdeExcel(
            @RequestParam("file") MultipartFile file,
            @RequestParam("estudianteId") Long estudianteId) {

        try {
            historiaService.cargarHistoriaAcademica(file, estudianteId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Historia académica cargada correctamente",
                    "timestamp", LocalDateTime.now()
            ));
        } catch (RuntimeException | IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "error", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

    @DeleteMapping("/eliminar-historia")
    public ResponseEntity<?> eliminarHistoriaPorEstudiante(@RequestParam("estudianteId") Long estudianteId) {
        try {
            historiaService.eliminarHistoriaAcademica(estudianteId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Historia académica eliminada correctamente",
                    "timestamp", LocalDateTime.now()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "error", e.getMessage(),
                    "timestamp", LocalDateTime.now()
            ));
        }
    }

}


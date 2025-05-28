package com.recommendationSys.Sistema_Recomendador_Finales.controllers;

import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.EstadisticasGeneralesDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.EstadisticasMateriaDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.exceptions.ResourceNotFoundException;
import com.recommendationSys.Sistema_Recomendador_Finales.services.estadisticas.EstadisticasCalculator;
import com.recommendationSys.Sistema_Recomendador_Finales.services.estadisticas.EstadisticasGeneralCalculator;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/public/estadisticas")
@RequiredArgsConstructor
public class EstadisticasController {

    private final EstadisticasCalculator estadisticasService;
    private final EstadisticasGeneralCalculator estadisticasGeneralService;

    /**
     * Obtiene estadísticas detalladas para una materia específica
     * @param codigoMateria Código único de la materia (no puede estar vacío)
     * @return ResponseEntity con las estadísticas de la materia
     * @throws ResourceNotFoundException si no se encuentra la materia
     */
    @GetMapping("/materia/{codigoMateria}")
    public ResponseEntity<EstadisticasMateriaDTO> obtenerEstadisticasMateria(
            @PathVariable @NotBlank(message = "El código de materia no puede estar vacío") String codigoMateria, @RequestParam("codigoPlan") @NotBlank String codigoPlan) {
        log.info("Solicitando estadísticas para materia: {} {}", codigoMateria,codigoPlan);
        EstadisticasMateriaDTO estadisticas = estadisticasService.obtenerEstadisticasMateria(codigoMateria,codigoPlan);
        return ResponseEntity.ok(estadisticas);
    }

    /**
     * Obtiene estadísticas generales del sistema
     * @return ResponseEntity con las estadísticas generales
     */
    @GetMapping("/generales")
    public ResponseEntity<EstadisticasGeneralesDTO> obtenerEstadisticasGenerales() {
        log.info("Solicitando estadísticas generales");
        return ResponseEntity.ok(estadisticasGeneralService.obtenerEstadisticasGenerales());
    }
}
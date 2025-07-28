package com.asistenteVirtual.controllers;

import com.asistenteVirtual.DTOs.EstadisticasGeneralesDTO;
import com.asistenteVirtual.DTOs.EstadisticasMateriaDTO;
import com.asistenteVirtual.exceptions.ResourceNotFoundException;
import com.asistenteVirtual.services.estadisticas.*;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/shared/estadisticas")
@RequiredArgsConstructor
public class EstadisticasController {

    private final EstadisticasCalculator estadisticasService;
    private final EstadisticasGeneralCalculator estadisticasGeneralService;
    private final EstadisticasAvanzadasService estadisticasAvanzadasService;
    private final EstadisticasMateriaPeriodoService estadisticasMateriaPeriodoService;


    /**
     * Obtiene estadísticas detalladas para una materia específica
     *
     * @param codigoMateria Código único de la materia (no puede estar vacío)
     * @return ResponseEntity con las estadísticas de la materia
     * @throws ResourceNotFoundException si no se encuentra la materia
     */
    @GetMapping("/materia/{codigoMateria}")
    public ResponseEntity<EstadisticasMateriaDTO> obtenerEstadisticasMateria(
            @PathVariable @NotBlank(message = "El código de materia no puede estar vacío") String codigoMateria) {
        EstadisticasMateriaDTO estadisticas = estadisticasService.obtenerEstadisticasSuperMateria(codigoMateria);
        return ResponseEntity.ok(estadisticas);
    }

    /**
     * Obtiene estadísticas generales del sistema
     *
     * @return ResponseEntity con las estadísticas generales
     */
    @GetMapping("/generales")
    public ResponseEntity<EstadisticasGeneralesDTO> obtenerEstadisticasGenerales() {
        return ResponseEntity.ok(estadisticasGeneralService.obtenerEstadisticasGenerales());
    }

    @GetMapping("/generales/carrera")
    public ResponseEntity<EstadisticasGeneralesDTO> obtenerEstadisticasPorCarrera(
            @RequestParam String plan,
            @RequestParam(required = false, defaultValue = "ULTIMO_ANIO") PeriodoEstadisticas periodo) {

        return ResponseEntity.ok(estadisticasAvanzadasService.obtenerEstadisticasPorCarrera(plan, periodo));
    }

    @GetMapping("/materia/{codigoMateria}/periodo")
    public ResponseEntity<EstadisticasMateriaDTO> obtenerEstadisticasMateriaPorPeriodo(
            @PathVariable @NotBlank String codigoMateria,
            @RequestParam PeriodoEstadisticas periodo) {

        return ResponseEntity.ok(estadisticasMateriaPeriodoService.obtenerEstadisticasMateriaPorPeriodo(codigoMateria, periodo));
    }


}
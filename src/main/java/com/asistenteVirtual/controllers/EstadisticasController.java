package com.asistenteVirtual.controllers;

import com.asistenteVirtual.DTOs.EstadisticasGeneralesDTO;
import com.asistenteVirtual.DTOs.EstadisticasMateriaDTO;
import com.asistenteVirtual.exceptions.ResourceNotFoundException;
import com.asistenteVirtual.services.estadisticas.EstadisticasCalculator;
import com.asistenteVirtual.services.estadisticas.EstadisticasGeneralCalculator;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/shared/estadisticas")
@RequiredArgsConstructor
public class EstadisticasController {

    private final EstadisticasCalculator estadisticasService;
    private final EstadisticasGeneralCalculator estadisticasGeneralService;

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
        log.info("Solicitando estadísticas para materia: {}", codigoMateria);
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
        log.info("Solicitando estadísticas generales");
        return ResponseEntity.ok(estadisticasGeneralService.obtenerEstadisticasGenerales());
    }
}
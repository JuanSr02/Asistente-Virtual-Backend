package com.asistenteVirtual.controllers;

import com.asistenteVirtual.DTOs.EstadisticasGeneralesDTO;
import com.asistenteVirtual.DTOs.EstadisticasMateriaDTO;
import com.asistenteVirtual.services.estadisticas.EstadisticasPorCarreraService;
import com.asistenteVirtual.services.estadisticas.FastStatisticsService;
import com.asistenteVirtual.services.estadisticas.PeriodoEstadisticas;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/shared/fast/estadisticas")
@RequiredArgsConstructor
public class FastEstadisticasController {

    private final FastStatisticsService fastStatisticsService;
    private final EstadisticasPorCarreraService estadisticasPorCarreraService;


    @GetMapping("/materia/{codigoMateria}")
    public ResponseEntity<EstadisticasMateriaDTO> obtenerEstadisticasMateriaRapido(
            @PathVariable String codigoMateria) {
        return ResponseEntity.ok(fastStatisticsService.getCachedMateriaStatistics(codigoMateria));
    }

    @GetMapping("/generales")
    public ResponseEntity<EstadisticasGeneralesDTO> obtenerEstadisticasGeneralesRapido() {
        return ResponseEntity.ok(fastStatisticsService.getCachedGeneralStatistics());
    }

    @GetMapping("/generales/carrera")
    public ResponseEntity<EstadisticasGeneralesDTO> obtenerEstadisticasPorCarreraRapido(
            @RequestParam String plan,
            @RequestParam(required = false, defaultValue = "ULTIMO_ANIO") PeriodoEstadisticas periodo) {

        return ResponseEntity.ok(estadisticasPorCarreraService.obtenerEstadisticasPorCarreraRapido(plan, periodo));
    }
}
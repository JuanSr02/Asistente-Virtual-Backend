package com.asistenteVirtual.modules.estadisticas.controller;

import com.asistenteVirtual.modules.estadisticas.dto.EstadisticasGeneralesResponse;
import com.asistenteVirtual.modules.estadisticas.dto.EstadisticasMateriaResponse;
import com.asistenteVirtual.modules.estadisticas.service.EstadisticasService;
import com.asistenteVirtual.modules.estadisticas.service.FastStatisticsService;
import com.asistenteVirtual.modules.estadisticas.service.EstadisticasAvanzadasService;
import com.asistenteVirtual.modules.estadisticas.model.PeriodoEstadisticas; // Enum movido al modelo
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shared/estadisticas")
@RequiredArgsConstructor
public class EstadisticasController {

    private final FastStatisticsService fastStatisticsService;
    private final EstadisticasService estadisticasService; // Para cálculos on-demand si fuera necesario
    private final EstadisticasAvanzadasService avanzadasService;

    @GetMapping("/generales")
    public ResponseEntity<EstadisticasGeneralesResponse> obtenerGenerales() {
        // Usamos la versión rápida (cached) por defecto para rendimiento
        return ResponseEntity.ok(fastStatisticsService.getCachedGeneralStatistics());
    }

    @GetMapping("/materia/{codigoMateria}")
    public ResponseEntity<EstadisticasMateriaResponse> obtenerPorMateria(@PathVariable String codigoMateria) {
        return ResponseEntity.ok(fastStatisticsService.getCachedMateriaStatistics(codigoMateria));
    }

    @GetMapping("/generales/carrera")
    public ResponseEntity<EstadisticasGeneralesResponse> obtenerPorCarrera(
            @RequestParam String plan,
            @RequestParam(required = false, defaultValue = "ULTIMO_ANIO") PeriodoEstadisticas periodo) {
        return ResponseEntity.ok(avanzadasService.obtenerEstadisticasPorCarrera(plan, periodo));
    }
    
    // Endpoint administrativo para forzar recálculo
    @PostMapping("/recalcular")
    public ResponseEntity<Void> forzarRecalculo() {
        estadisticasService.actualizarTodas();
        return ResponseEntity.ok().build();
    }
}
package com.asistenteVirtual.modules.estadisticas.controller;

import com.asistenteVirtual.modules.estadisticas.dto.EstadisticasGeneralesResponse;
import com.asistenteVirtual.modules.estadisticas.dto.EstadisticasMateriaResponse;
import com.asistenteVirtual.modules.estadisticas.model.PeriodoEstadisticas;
import com.asistenteVirtual.modules.estadisticas.service.EstadisticasAvanzadasService;
import com.asistenteVirtual.modules.estadisticas.service.EstadisticasMateriaPeriodoService;
import com.asistenteVirtual.modules.estadisticas.service.EstadisticasService;
import com.asistenteVirtual.modules.estadisticas.service.FastStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shared/estadisticas")
@RequiredArgsConstructor
public class EstadisticasController {

    private final FastStatisticsService fastStatisticsService;
    private final EstadisticasService estadisticasService;
    private final EstadisticasAvanzadasService avanzadasService;
    private final EstadisticasMateriaPeriodoService periodoService;

    @GetMapping("/generales")
    public ResponseEntity<EstadisticasGeneralesResponse> obtenerGenerales() {
        return ResponseEntity.ok(fastStatisticsService.getCachedGeneralStatistics());
    }

    @GetMapping("/materia/{codigoMateria}")
    public ResponseEntity<EstadisticasMateriaResponse> obtenerPorMateria(@PathVariable String codigoMateria) {
        return ResponseEntity.ok(fastStatisticsService.getCachedMateriaStatistics(codigoMateria));
    }

    @GetMapping("/materia/{codigoMateria}/periodo")
    public ResponseEntity<EstadisticasMateriaResponse> obtenerPorMateriaYPeriodo(
            @PathVariable String codigoMateria,
            @RequestParam(required = false, defaultValue = "TODOS_LOS_TIEMPOS") PeriodoEstadisticas periodo) {
        return ResponseEntity.ok(periodoService.obtenerEstadisticasPorPeriodo(codigoMateria, periodo));
    }

    @GetMapping("/generales/carrera")
    public ResponseEntity<EstadisticasGeneralesResponse> obtenerPorCarrera(
            @RequestParam String plan,
            @RequestParam(required = false, defaultValue = "ULTIMO_ANIO") PeriodoEstadisticas periodo) {
        
        // ✅ CORRECCIÓN CRÍTICA: Intenta leer caché primero (Fast), si falla, calcula (Avanzado).
        // Esto recupera el rendimiento del "FastController" de main pero en un solo endpoint.
        try {
            return ResponseEntity.ok(fastStatisticsService.getCachedCarreraStatistics(plan, periodo));
        } catch (Exception e) {
            // Fallback: Si no hay caché, calculamos al vuelo
            return ResponseEntity.ok(avanzadasService.obtenerEstadisticasPorCarrera(plan, periodo));
        }
    }

    @PostMapping("/recalcular")
    public ResponseEntity<Void> forzarRecalculo() {
        estadisticasService.actualizarTodas();
        return ResponseEntity.ok().build();
    }
}
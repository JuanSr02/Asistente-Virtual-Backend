package com.asistenteVirtual.modules.estadisticas.controller;

import com.asistenteVirtual.common.exceptions.ResourceNotFoundException;
import com.asistenteVirtual.modules.estadisticas.dto.EstadisticasGeneralesResponse;
import com.asistenteVirtual.modules.estadisticas.dto.EstadisticasMateriaResponse;
import com.asistenteVirtual.modules.estadisticas.model.PeriodoEstadisticas;
import com.asistenteVirtual.modules.estadisticas.service.EstadisticasAvanzadasService;
import com.asistenteVirtual.modules.estadisticas.service.EstadisticasMateriaPeriodoService;
import com.asistenteVirtual.modules.estadisticas.service.EstadisticasService;
import com.asistenteVirtual.modules.estadisticas.service.FastStatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/shared/estadisticas")
@RequiredArgsConstructor
public class EstadisticasController {

    private final FastStatisticsService fastService; // Servicio de Lectura (Rápido)
    private final EstadisticasService calculationService; // Servicio de Escritura (Lento/Cálculo)
    private final EstadisticasAvanzadasService avanzadasService;
    private final EstadisticasMateriaPeriodoService periodoService;

    @GetMapping("/generales")
    public ResponseEntity<EstadisticasGeneralesResponse> obtenerGenerales() {
        try {
            // 1. Intentar caché
            return ResponseEntity.ok(fastService.getCachedGeneralStatistics());
        } catch (ResourceNotFoundException e) {
            log.info("Cache miss para estadísticas generales. Calculando al vuelo...");
            // 2. Fallback: Calcular y guardar
            return ResponseEntity.ok(calculationService.calcularYGuardarGenerales());
        }
    }

    @GetMapping("/materia/{codigoMateria}")
    public ResponseEntity<EstadisticasMateriaResponse> obtenerPorMateria(@PathVariable String codigoMateria) {
        try {
            // 1. Intentar caché
            return ResponseEntity.ok(fastService.getCachedMateriaStatistics(codigoMateria));
        } catch (ResourceNotFoundException e) {
            log.info("Cache miss para materia {}. Calculando al vuelo...", codigoMateria);
            // 2. Fallback: Calcular y guardar solo esta materia
            var result = calculationService.calcularYGuardarMateria(codigoMateria);
            if (result == null) {
                throw new ResourceNotFoundException("Materia no encontrada para cálculo: " + codigoMateria);
            }
            return ResponseEntity.ok(result);
        }
    }

    @GetMapping("/materia/{codigoMateria}/periodo")
    public ResponseEntity<EstadisticasMateriaResponse> obtenerPorMateriaYPeriodo(
            @PathVariable String codigoMateria,
            @RequestParam(required = false, defaultValue = "TODOS_LOS_TIEMPOS") PeriodoEstadisticas periodo) {
        // Este servicio ya calcula al vuelo (no tiene caché persistente en tabla propia para periodos específicos por ahora)
        return ResponseEntity.ok(periodoService.obtenerEstadisticasPorPeriodo(codigoMateria, periodo));
    }

    @GetMapping("/generales/carrera")
    public ResponseEntity<EstadisticasGeneralesResponse> obtenerPorCarrera(
            @RequestParam String plan,
            @RequestParam(required = false, defaultValue = "ULTIMO_ANIO") PeriodoEstadisticas periodo) {

        try {
            // 1. Intentar caché
            return ResponseEntity.ok(fastService.getCachedCarreraStatistics(plan, periodo));
        } catch (ResourceNotFoundException e) {
            // 2. Fallback
            log.info("Cache miss para carrera {} periodo {}. Calculando al vuelo...", plan, periodo);
            return ResponseEntity.ok(avanzadasService.obtenerEstadisticasPorCarrera(plan, periodo));
        }
    }

    @PostMapping("/recalcular")
    public ResponseEntity<Void> forzarRecalculo() {
        // Opción administrativa para regenerar todo el caché
        calculationService.actualizarTodas();
        return ResponseEntity.ok().build();
    }
}
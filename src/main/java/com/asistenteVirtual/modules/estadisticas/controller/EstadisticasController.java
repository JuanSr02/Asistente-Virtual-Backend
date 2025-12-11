package com.asistenteVirtual.modules.estadisticas.controller;

import com.asistenteVirtual.common.exceptions.ResourceNotFoundException;
import com.asistenteVirtual.modules.estadisticas.dto.EstadisticasGeneralesResponse;
import com.asistenteVirtual.modules.estadisticas.dto.EstadisticasMateriaResponse;
import com.asistenteVirtual.modules.estadisticas.model.PeriodoEstadisticas;
import com.asistenteVirtual.modules.estadisticas.service.EstadisticasAvanzadasService;
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
    private final EstadisticasAvanzadasService avanzadasService; // Mantenemos para fallback de carrera si es necesario

    @GetMapping("/generales")
    public ResponseEntity<EstadisticasGeneralesResponse> obtenerGenerales() {
        try {
            return ResponseEntity.ok(fastService.getCachedGeneralStatistics());
        } catch (ResourceNotFoundException e) {
            log.info("Cache miss: Calculando Generales al vuelo...");
            return ResponseEntity.ok(calculationService.calcularYGuardarGenerales());
        }
    }

    @GetMapping("/materia/{codigoMateria}")
    public ResponseEntity<EstadisticasMateriaResponse> obtenerPorMateria(
            @PathVariable String codigoMateria,
            @RequestParam(required = false, defaultValue = "TODOS_LOS_TIEMPOS") PeriodoEstadisticas periodo) {

        try {
            // 1. Intentar Caché
            return ResponseEntity.ok(fastService.getCachedMateriaStatistics(codigoMateria, periodo));
        } catch (ResourceNotFoundException e) {
            // 2. Cache Miss -> Calcular, Guardar y Retornar
            log.info("Cache miss materia {} periodo {}. Calculando al vuelo...", codigoMateria, periodo);
            var result = calculationService.calcularYGuardarMateria(codigoMateria, periodo);

            if (result == null) {
                throw new ResourceNotFoundException("Materia no encontrada: " + codigoMateria);
            }
            return ResponseEntity.ok(result);
        }
    }

    @GetMapping("/generales/carrera")
    public ResponseEntity<EstadisticasGeneralesResponse> obtenerPorCarrera(
            @RequestParam String plan,
            @RequestParam(required = false, defaultValue = "ULTIMO_ANIO") PeriodoEstadisticas periodo) {

        try {
            return ResponseEntity.ok(fastService.getCachedCarreraStatistics(plan, periodo));
        } catch (ResourceNotFoundException e) {
            log.info("Cache miss carrera {} periodo {}. Calculando al vuelo...", plan, periodo);
            return ResponseEntity.ok(avanzadasService.obtenerEstadisticasPorCarrera(plan, periodo));
        }
    }

    @PostMapping("/recalcular")
    public ResponseEntity<Void> forzarRecalculo() {
        calculationService.actualizarTodas();
        return ResponseEntity.ok().build();
    }
}
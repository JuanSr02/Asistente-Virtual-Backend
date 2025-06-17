package com.asistenteVirtual.controllers;

import com.asistenteVirtual.DTOs.EstadisticasGeneralesDTO;
import com.asistenteVirtual.DTOs.EstadisticasMateriaDTO;
import com.asistenteVirtual.services.estadisticas.FastStatisticsService;
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

    @GetMapping("/materia/{codigoMateria}")
    public ResponseEntity<EstadisticasMateriaDTO> obtenerEstadisticasMateriaRapido(
            @PathVariable String codigoMateria) {
        log.info("Solicitando estadísticas rápidas para materia: {}", codigoMateria);
        return ResponseEntity.ok(fastStatisticsService.getCachedMateriaStatistics(codigoMateria));
    }

    @GetMapping("/generales")
    public ResponseEntity<EstadisticasGeneralesDTO> obtenerEstadisticasGeneralesRapido() {
        log.info("Solicitando estadísticas generales rápidas");
        return ResponseEntity.ok(fastStatisticsService.getCachedGeneralStatistics());
    }
}
package com.asistenteVirtual.controllers;

import com.asistenteVirtual.DTOs.EstadisticasGeneralesDTO;
import com.asistenteVirtual.DTOs.EstadisticasMateriaDTO;
import com.asistenteVirtual.services.estadisticas.FastStatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/shared/fast/estadisticas")
@RequiredArgsConstructor
public class FastEstadisticasController {

    private final FastStatisticsService fastStatisticsService;

    @GetMapping("/materia/{codigoMateria}")
    public ResponseEntity<EstadisticasMateriaDTO> obtenerEstadisticasMateriaRapido(
            @PathVariable String codigoMateria) {
        return ResponseEntity.ok(fastStatisticsService.getCachedMateriaStatistics(codigoMateria));
    }

    @GetMapping("/generales")
    public ResponseEntity<EstadisticasGeneralesDTO> obtenerEstadisticasGeneralesRapido() {
        return ResponseEntity.ok(fastStatisticsService.getCachedGeneralStatistics());
    }
}
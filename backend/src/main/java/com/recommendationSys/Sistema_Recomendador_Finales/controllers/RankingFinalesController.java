package com.recommendationSys.Sistema_Recomendador_Finales.controllers;

import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.FinalDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.OrdenFinales;
import com.recommendationSys.Sistema_Recomendador_Finales.exceptions.ResourceNotFoundException;
import com.recommendationSys.Sistema_Recomendador_Finales.services.rankingFinales.RankingFinalesService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shared/finales/{estudianteId}")
public class RankingFinalesController {

    private final RankingFinalesService rankingFinalesService;

    /**
     * Obtiene el ranking de finales para rendir por un estudiante
     * @param estudianteId id del estudiante (no puede ser nulo)
     * @param orden Criterio de ordenamiento (default: CORRELATIVAS)
     * @return Lista de finales ordenados según el criterio
     * @throws ResourceNotFoundException si no se encuentra el estudiante
     */
    @GetMapping
    public ResponseEntity<?> obtenerFinalesParaRendir(
            @PathVariable @NotNull Long estudianteId,
            @RequestParam(defaultValue = "CORRELATIVAS") OrdenFinales orden) {
        log.info("Obteniendo finales para estudiante ID: {} ordenados por: {}", estudianteId, orden);
        List<FinalDTO> finales = rankingFinalesService.obtenerFinalesParaRendir(estudianteId, orden);
        return ResponseEntity.ok(finales);
    }
}
package com.asistenteVirtual.controllers;

import com.asistenteVirtual.DTOs.FinalDTO;
import com.asistenteVirtual.DTOs.MateriaDTO;
import com.asistenteVirtual.DTOs.OrdenFinales;
import com.asistenteVirtual.exceptions.ResourceNotFoundException;
import com.asistenteVirtual.services.rankingFinales.RankingFinalesService;
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
     *
     * @param estudianteId id del estudiante (no puede ser nulo)
     * @param orden        Criterio de ordenamiento (default: CORRELATIVAS)
     * @return Lista de finales ordenados según el criterio
     * @throws ResourceNotFoundException si no se encuentra el estudiante
     */
    @GetMapping
    public ResponseEntity<?> obtenerFinalesParaRendir(
            @PathVariable @NotNull Long estudianteId,
            @RequestParam(defaultValue = "CORRELATIVAS") OrdenFinales orden) {
        List<FinalDTO> finales = rankingFinalesService.obtenerFinalesParaRendir(estudianteId, orden);
        return ResponseEntity.ok(finales);
    }

    @GetMapping("/inscripciones")
    public ResponseEntity<?> obtenerInscripcionesPosibles(@PathVariable @NotNull Long estudianteId) {
        List<MateriaDTO> finales = rankingFinalesService.obtenerFinalesParaInscribirse(estudianteId);
        return ResponseEntity.ok(finales);
    }
}
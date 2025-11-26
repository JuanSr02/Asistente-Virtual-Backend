package com.asistenteVirtual.modules.ranking.controller;

import com.asistenteVirtual.modules.planEstudio.dto.MateriaResponse;
import com.asistenteVirtual.modules.ranking.dto.FinalResponse;
import com.asistenteVirtual.modules.ranking.model.OrdenFinales;
import com.asistenteVirtual.modules.ranking.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shared/finales/{estudianteId}")
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;

    @GetMapping
    public ResponseEntity<List<FinalResponse>> obtenerFinalesParaRendir(
            @PathVariable Long estudianteId,
            @RequestParam(defaultValue = "CORRELATIVAS") OrdenFinales orden) {

        return ResponseEntity.ok(rankingService.obtenerFinalesParaRendir(estudianteId, orden));
    }

    @GetMapping("/inscripciones")
    public ResponseEntity<List<MateriaResponse>> obtenerInscripcionesPosibles(
            @PathVariable Long estudianteId) {

        return ResponseEntity.ok(rankingService.obtenerFinalesParaInscribirse(estudianteId));
    }
}
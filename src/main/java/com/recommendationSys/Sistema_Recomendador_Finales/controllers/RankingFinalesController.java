package com.recommendationSys.Sistema_Recomendador_Finales.controllers;

import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.FinalDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.OrdenFinales;
import com.recommendationSys.Sistema_Recomendador_Finales.services.RankingFinalesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/finales")
@RequiredArgsConstructor
public class RankingFinalesController {

    private final RankingFinalesService finalesService;

    @GetMapping("/{estudianteId}")
    public ResponseEntity<List<FinalDTO>> getFinalesParaRendir(
            @PathVariable Long estudianteId,
            @RequestParam(defaultValue = "CORRELATIVAS") OrdenFinales orden) {

        return ResponseEntity.ok(finalesService.obtenerFinalesParaRendir(estudianteId, orden));
    }
}

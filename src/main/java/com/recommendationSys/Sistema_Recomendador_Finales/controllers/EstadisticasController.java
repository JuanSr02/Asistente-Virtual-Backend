package com.recommendationSys.Sistema_Recomendador_Finales.controllers;

import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.EstadisticasGeneralesDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.EstadisticasMateriaDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.model.EstadisticasMateria;
import com.recommendationSys.Sistema_Recomendador_Finales.services.EstadisticasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/estadisticas")
@RequiredArgsConstructor
public class EstadisticasController {

    private final EstadisticasService estadisticasService;

    @GetMapping("/materia/{codigoMateria}")
    public ResponseEntity<EstadisticasMateriaDTO> getEstadisticasMateria(
            @PathVariable String codigoMateria) {

        EstadisticasMateria stats = estadisticasService.calcularEstadisticasPorMateria(codigoMateria);
        return ResponseEntity.ok(estadisticasService.convertToDTO(stats));
    }

    @GetMapping("/generales")
    public ResponseEntity<EstadisticasGeneralesDTO> getEstadisticasGenerales() {
        return ResponseEntity.ok(estadisticasService.obtenerEstadisticasGenerales());
    }


}

package com.recommendationSys.Sistema_Recomendador_Finales.controllers;

import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.InscripcionResponseDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.RegistroInscripcionDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.model.RegistroInscripcion;
import com.recommendationSys.Sistema_Recomendador_Finales.services.inscripciones.RegistroInscripcionServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/inscripciones")
public class RegistroInscripcionController {

    private final RegistroInscripcionServiceImpl inscripcionService;

    public RegistroInscripcionController(RegistroInscripcionServiceImpl inscripcionService) {
        this.inscripcionService = inscripcionService;
    }

    // Alta de inscripción
    @PostMapping
    public ResponseEntity<InscripcionResponseDTO> crearInscripcion(
            @Valid @RequestBody RegistroInscripcionDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inscripcionService.crearInscripcion(dto));
    }

    // Baja de inscripción
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarInscripcion(@PathVariable Long id) {
        inscripcionService.eliminarInscripcion(id);
        return ResponseEntity.noContent().build();
    }

    // Consulta de inscriptos
    @GetMapping("/inscriptos")
    public ResponseEntity<List<InscripcionResponseDTO>> obtenerInscriptos(
            @RequestParam String materia,
            @RequestParam Integer anio,
            @RequestParam String turno) {

        return ResponseEntity.ok(inscripcionService.obtenerInscriptos(materia, anio, turno));
    }

    @PostMapping("/avisar")
    public ResponseEntity<Void> avisarRinde(@Valid @RequestBody RegistroInscripcion dto){
        inscripcionService.notificarCompaneros(dto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}

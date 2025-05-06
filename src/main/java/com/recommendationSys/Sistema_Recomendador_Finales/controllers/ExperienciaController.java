package com.recommendationSys.Sistema_Recomendador_Finales.controllers;

import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.ExperienciaDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Experiencia;
import com.recommendationSys.Sistema_Recomendador_Finales.services.ExperienciaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/exp")
public class ExperienciaController {

    private final ExperienciaService experienciaService;

    public ExperienciaController(ExperienciaService experienciaService) {
        this.experienciaService = experienciaService;
    }

    @PostMapping
    public ResponseEntity<Experiencia> crearExperiencia(@Valid @RequestBody ExperienciaDTO experienciaDTO) {
        Experiencia nuevaExperiencia = experienciaService.crearExperiencia(experienciaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaExperiencia);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Experiencia> obtenerExperiencia(@PathVariable Long id) {
        return ResponseEntity.ok(experienciaService.obtenerExperienciaPorId(id));
    }

    @GetMapping("/por-examen/{examenId}")
    public ResponseEntity<Experiencia> obtenerExperienciaPorExamen(@PathVariable Long examenId) {
        return ResponseEntity.ok(experienciaService.obtenerExperienciaPorExamen(examenId));
    }

    @GetMapping
    public ResponseEntity<List<Experiencia>> obtenerTodasLasExperiencias() {
        return ResponseEntity.ok(experienciaService.obtenerTodasLasExperiencias());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Experiencia> actualizarExperiencia(
            @PathVariable Long id,
            @Valid @RequestBody ExperienciaDTO experienciaDTO) {
        return ResponseEntity.ok(experienciaService.actualizarExperiencia(id, experienciaDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarExperiencia(@PathVariable Long id) {
        experienciaService.eliminarExperiencia(id);
        return ResponseEntity.noContent().build();
    }
}

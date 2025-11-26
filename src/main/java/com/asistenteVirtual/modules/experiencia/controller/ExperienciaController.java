package com.asistenteVirtual.modules.experiencia.controller;

import com.asistenteVirtual.modules.experiencia.dto.ExamenDisponibleResponse;
import com.asistenteVirtual.modules.experiencia.dto.ExperienciaRequest;
import com.asistenteVirtual.modules.experiencia.dto.ExperienciaResponse;
import com.asistenteVirtual.modules.experiencia.dto.ExperienciaUpdate;
import com.asistenteVirtual.modules.experiencia.service.ExperienciaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shared/experiencias")
@RequiredArgsConstructor
public class ExperienciaController {

    private final ExperienciaService experienciaService;

    @PostMapping
    public ResponseEntity<ExperienciaResponse> crear(@Valid @RequestBody ExperienciaRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(experienciaService.crearExperiencia(dto));
    }

    @GetMapping("/examenes-por-estudiante/{estudianteId}")
    public ResponseEntity<List<ExamenDisponibleResponse>> listarExamenesSinExperiencia(@PathVariable Long estudianteId) {
        return ResponseEntity.ok(experienciaService.obtenerExamenesPendientesDeExperiencia(estudianteId));
    }

    @GetMapping("/por-materia/{codigoMateria}")
    public ResponseEntity<List<ExperienciaResponse>> listarPorMateria(@PathVariable String codigoMateria) {
        return ResponseEntity.ok(experienciaService.obtenerPorMateria(codigoMateria));
    }

    @GetMapping("/por-estudiante/{estudianteId}")
    public ResponseEntity<List<ExperienciaResponse>> listarPorEstudiante(@PathVariable Long estudianteId) {
        return ResponseEntity.ok(experienciaService.obtenerPorEstudiante(estudianteId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExperienciaResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(experienciaService.obtenerPorId(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ExperienciaResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ExperienciaUpdate dto) {
        return ResponseEntity.ok(experienciaService.actualizarExperiencia(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        experienciaService.eliminarExperiencia(id);
        return ResponseEntity.noContent().build();
    }
}
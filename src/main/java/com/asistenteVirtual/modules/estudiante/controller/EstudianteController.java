package com.asistenteVirtual.modules.estudiante.controller;

import com.asistenteVirtual.modules.estudiante.dto.EstudianteRequest;
import com.asistenteVirtual.modules.estudiante.dto.EstudianteResponse;
import com.asistenteVirtual.modules.estudiante.dto.EstudianteUpdate;
import com.asistenteVirtual.modules.estudiante.service.EstudianteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EstudianteController {

    private final EstudianteService estudianteService;

    @PostMapping("/public/estudiantes")
    public ResponseEntity<EstudianteResponse> crearEstudiante(@RequestBody @Valid EstudianteRequest dto) {
        return ResponseEntity.ok(estudianteService.crearEstudiante(dto));
    }

    @GetMapping("/shared/estudiantes/{id}")
    public ResponseEntity<EstudianteResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(estudianteService.obtenerPorId(id));
    }

    @PatchMapping("/shared/estudiantes/{id}")
    public ResponseEntity<EstudianteResponse> actualizar(
            @PathVariable Long id,
            @RequestBody @Valid EstudianteUpdate dto) {
        return ResponseEntity.ok(estudianteService.actualizarEstudiante(id, dto));
    }

    @DeleteMapping("/shared/estudiantes/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        estudianteService.eliminarEstudiante(id);
        return ResponseEntity.noContent().build();
    }
}
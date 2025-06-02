package com.recommendationSys.Sistema_Recomendador_Finales.controllers;

import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.ActualizarEstudianteDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.EstudianteDto;
import com.recommendationSys.Sistema_Recomendador_Finales.services.estudiante.EstudianteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para manejar operaciones sobre estudiantes.
 */
@RestController
@RequestMapping("/api/shared/estudiantes")
@RequiredArgsConstructor
public class EstudianteController {

    private final EstudianteService estudianteService;

    /**
     * Crea un nuevo estudiante.
     *
     * @param dto Datos del estudiante a crear.
     * @return El estudiante creado.
     */
    @PostMapping
    public ResponseEntity<?> crearEstudiante(@RequestBody @Valid EstudianteDto dto) {
        return ResponseEntity.ok(estudianteService.crearEstudiante(dto));
    }

    /**
     * Obtiene un estudiante por su ID.
     *
     * @param id ID del estudiante.
     * @return El estudiante encontrado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(estudianteService.obtenerPorId(id));
    }

    /**
     * Obtiene todos los estudiantes.
     *
     * @return Lista de todos los estudiantes registrados.
     */
    @GetMapping
    public ResponseEntity<?> obtenerTodos() {
        return ResponseEntity.ok(estudianteService.obtenerTodos());
    }

    /**
     * Actualiza un estudiante existente.
     *
     * @param id  ID del estudiante a actualizar.
     * @param dto Nuevos datos del estudiante.
     * @return Estudiante actualizado.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody ActualizarEstudianteDTO dto) {
        return ResponseEntity.ok(estudianteService.actualizarEstudiante(id, dto));
    }

    /**
     * Elimina un estudiante por su ID.
     *
     * @param id ID del estudiante a eliminar.
     * @return Respuesta sin contenido.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        estudianteService.eliminarEstudiante(id);
        return ResponseEntity.noContent().build();
    }
}

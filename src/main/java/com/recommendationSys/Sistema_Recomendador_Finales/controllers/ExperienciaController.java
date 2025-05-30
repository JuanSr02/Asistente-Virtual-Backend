package com.recommendationSys.Sistema_Recomendador_Finales.controllers;

import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.ActualizarExperienciaDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.ExperienciaDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.ExperienciaResponseDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.exceptions.ResourceNotFoundException;
import com.recommendationSys.Sistema_Recomendador_Finales.services.experiencia.ExperienciaCRUDService;
import com.recommendationSys.Sistema_Recomendador_Finales.services.experiencia.ExperienciaQueryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/experiencias")
public class ExperienciaController {

    private final ExperienciaCRUDService experienciaService;
    private final ExperienciaQueryService experienciaQueryService;

    /**
     * Crea una nueva experiencia de examen
     * @param experienciaDTO Datos de la experiencia a crear
     * @return ResponseEntity con la experiencia creada
     */
    @PostMapping
    public ResponseEntity<?> crearExperiencia(@Valid @RequestBody ExperienciaDTO experienciaDTO) {
        log.info("Creando nueva experiencia para examen: {}", experienciaDTO.getExamenId());
        ExperienciaResponseDTO nuevaExperiencia = experienciaService.crearExperiencia(experienciaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaExperiencia);
    }

    /**
     * Obtiene una experiencia por su ID
     * @param id de la experiencia (no puede ser nulo)
     * @return ResponseEntity con la experiencia encontrada
     * @throws ResourceNotFoundException si no se encuentra la experiencia
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerExperiencia(
            @PathVariable @NotNull(message = "El ID no puede ser nulo") Long id) {
        log.info("Obteniendo experiencia con ID: {}", id);
        return ResponseEntity.ok(experienciaService.obtenerExperienciaPorId(id));
    }


    /**
     * Obtiene todas las experiencias registradas
     * @return ResponseEntity con la lista de experiencias
     */
    @GetMapping
    public ResponseEntity<?> obtenerTodasLasExperiencias() {
        log.info("Obteniendo todas las experiencias");
        return ResponseEntity.ok(experienciaService.obtenerTodasLasExperiencias());
    }

    /**
     * Actualiza una experiencia existente
     * @param id de la experiencia a actualizar (no puede ser nulo)
     * @param dto Datos actualizados de la experiencia
     * @return ResponseEntity con la experiencia actualizada
     * @throws ResourceNotFoundException si no se encuentra la experiencia
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarExperiencia(
            @PathVariable @NotNull(message = "El ID no puede ser nulo") Long id,
            @Valid @RequestBody ActualizarExperienciaDTO dto) {
        log.info("Actualizando experiencia ID: {}", id);
        return ResponseEntity.ok(experienciaService.actualizarExperiencia(id, dto));
    }

    /**
     * Elimina una experiencia
     * @param id de la experiencia a eliminar (no puede ser nulo)
     * @return ResponseEntity sin contenido
     * @throws ResourceNotFoundException si no se encuentra la experiencia
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarExperiencia(
            @PathVariable @NotNull(message = "El ID no puede ser nulo") Long id) {
        log.info("Eliminando experiencia ID: {}", id);
        experienciaService.eliminarExperiencia(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtiene experiencias por materia
     * @param codigoMateria Código de la materia (no puede estar vacío)
     * @return ResponseEntity con la lista de experiencias
     * @throws ResourceNotFoundException si no se encuentra la materia
     */
    @GetMapping("/por-materia/{codigoMateria}")
    public ResponseEntity<?> obtenerExperienciasPorMateria(
            @PathVariable @NotBlank(message = "El código de materia no puede estar vacío") String codigoMateria) {
        log.info("Obteniendo experiencias para materia: {}", codigoMateria);
        return ResponseEntity.ok(experienciaQueryService.obtenerExperienciasPorMateria(codigoMateria));
    }

    /**
     * Obtiene experiencias por estudiante
     * @param idEstudiante id del estudiante (no puede estar vacío)
     * @return ResponseEntity con la lista de experiencias
     * @throws ResourceNotFoundException si no se encuentra el estudiante
     */
    @GetMapping("/por-estudiante/{idEstudiante}")
    public ResponseEntity<?> obtenerExperienciasPorEstudiante(
            @PathVariable @NotNull(message = "El id del estudiante no puede ser nulo") Long idEstudiante) {
        log.info("Obteniendo experiencias para estudiante: {}", idEstudiante);
        return ResponseEntity.ok(experienciaQueryService.obtenerExperienciasPorEstudiante(idEstudiante));
    }
}
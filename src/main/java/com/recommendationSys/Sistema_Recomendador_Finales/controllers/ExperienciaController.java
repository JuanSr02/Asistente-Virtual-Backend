package com.recommendationSys.Sistema_Recomendador_Finales.controllers;

import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.ActualizarExperienciaDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.ExperienciaDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.exceptions.ResourceNotFoundException;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Experiencia;
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

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/experiencias")
public class ExperienciaController {

    private final ExperienciaCRUDService experienciaService;
    private final ExperienciaQueryService experienciaQueryService;

    /**
     * Crea una nueva experiencia de estudio
     * @param experienciaDTO Datos de la experiencia a crear
     * @return ResponseEntity con el ID de la experiencia creada
     */
    @PostMapping
    public ResponseEntity<?> crearExperiencia(@Valid @RequestBody ExperienciaDTO experienciaDTO) {
        log.info("Creando nueva experiencia para examen: {}", experienciaDTO.getExamenId());
        Experiencia nuevaExperiencia = experienciaService.crearExperiencia(experienciaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("Experiencia creada correctamente.");
    }

    /**
     * Obtiene una experiencia por su ID
     * @param id de la experiencia (no puede ser nulo)
     * @return ResponseEntity con la experiencia encontrada
     * @throws ResourceNotFoundException si no se encuentra la experiencia
     */
    @GetMapping("/{id}")
    public ResponseEntity<Experiencia> obtenerExperiencia(
            @PathVariable @NotNull(message = "El ID no puede ser nulo") Long id) {
        log.info("Obteniendo experiencia con ID: {}", id);
        return ResponseEntity.ok(experienciaService.obtenerExperienciaPorId(id));
    }


    /**
     * Obtiene todas las experiencias registradas
     * @return ResponseEntity con la lista de experiencias
     */
    @GetMapping
    public ResponseEntity<List<Experiencia>> obtenerTodasLasExperiencias() {
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
    public ResponseEntity<Experiencia> actualizarExperiencia(
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
    public ResponseEntity<Void> eliminarExperiencia(
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
    public ResponseEntity<List<Experiencia>> obtenerExperienciasPorMateria(
            @PathVariable @NotBlank(message = "El código de materia no puede estar vacío") String codigoMateria, @RequestParam("codigoPlan") @NotBlank String codigoPlan) {
        log.info("Obteniendo experiencias para materia: {} {}", codigoMateria,codigoPlan);
        return ResponseEntity.ok(experienciaQueryService.obtenerExperienciasPorMateria(codigoMateria,codigoPlan));
    }
}
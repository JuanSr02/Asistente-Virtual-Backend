package com.asistenteVirtual.controllers;

import com.asistenteVirtual.DTOs.InscripcionResponseDTO;
import com.asistenteVirtual.DTOs.RegistroInscripcionDTO;
import com.asistenteVirtual.exceptions.IntegrityException;
import com.asistenteVirtual.exceptions.ResourceNotFoundException;
import com.asistenteVirtual.services.inscripciones.InscripcionNotificationService;
import com.asistenteVirtual.services.inscripciones.InscripcionService;
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
@RequestMapping("/api/shared/inscripciones")
public class RegistroInscripcionController {

    private final InscripcionService inscripcionService;
    private final InscripcionNotificationService inscripcionNotificationService;

    /**
     * Crea una nueva inscripción
     * @param dto Datos de la inscripción
     * @return Inscripción creada con status 201
     * @throws ResourceNotFoundException si no encuentra el estudiante o la materia
     * @throws IntegrityException si el estudiante ya está inscripto a esta materia y año.
     */
    @PostMapping
    public ResponseEntity<?> crearInscripcion(
            @Valid @RequestBody RegistroInscripcionDTO dto) {
        log.info("Creando nueva inscripción para estudiante: {}", dto.getEstudianteId());
        InscripcionResponseDTO response = inscripcionService.crearInscripcion(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Elimina una inscripción existente
     * @param id de la inscripción a eliminar
     * @return Respuesta vacía con status 204
     * @throws ResourceNotFoundException si no encuentra la inscripcion.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarInscripcion(
            @PathVariable @NotNull Long id) {
        log.info("Eliminando inscripción con ID: {}", id);
        inscripcionService.eliminarInscripcion(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Obtiene listado de inscriptos a una materia
     * @param codigoMateria Código de la materia
     * @param anio Año académico
     * @param turno Turno (mañana/tarde/noche)
     * @return Lista de inscriptos
     */
    @GetMapping
    public ResponseEntity<?> obtenerInscriptos(
            @RequestParam @NotBlank String codigoMateria,
            @RequestParam @NotNull Integer anio,
            @RequestParam @NotBlank String turno) {
        log.info("Consultando inscriptos a materia: {} año: {} turno: {}",
                codigoMateria, anio, turno);
        List<InscripcionResponseDTO> inscriptos = inscripcionService
                .obtenerInscriptos(codigoMateria, anio, turno);
        return ResponseEntity.ok(inscriptos);
    }

    /**
     * Notifica a compañeros sobre nueva inscripción
     * @param dto Datos de la inscripción
     * @return Respuesta vacía con status 200
     */
    @PostMapping("/notificaciones")
    public ResponseEntity<?> notificarCompaneros(
            @Valid @RequestBody RegistroInscripcionDTO dto) {
        log.info("Notificando compañeros de nueva inscripción");
        inscripcionNotificationService.notificarCompanerosDTO(dto);
        return ResponseEntity.ok().build();
    }
}
package com.asistenteVirtual.modules.inscripcion.controller;

import com.asistenteVirtual.modules.inscripcion.dto.InscripcionRequest;
import com.asistenteVirtual.modules.inscripcion.dto.InscripcionResponse;
import com.asistenteVirtual.modules.inscripcion.service.InscripcionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/shared/inscripciones")
@RequiredArgsConstructor
public class InscripcionController {

    private final InscripcionService inscripcionService;

    @PostMapping
    public ResponseEntity<InscripcionResponse> crear(@Valid @RequestBody InscripcionRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inscripcionService.crearInscripcion(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        inscripcionService.eliminarInscripcion(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<InscripcionResponse>> listar(
            @RequestParam @NotBlank String codigoMateria,
            @RequestParam @NotNull Integer anio,
            @RequestParam @NotBlank String turno) {

        return ResponseEntity.ok(inscripcionService.obtenerInscriptos(codigoMateria, anio, turno));
    }

    // Eliminamos el endpoint "/notificaciones" manual.
    // Las notificaciones ahora son automáticas al crear la inscripción,
    // lo cual es más seguro y consistente con el negocio.
}
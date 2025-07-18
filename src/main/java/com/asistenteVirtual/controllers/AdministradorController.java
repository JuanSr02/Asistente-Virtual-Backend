package com.asistenteVirtual.controllers;

import com.asistenteVirtual.DTOs.ActualizarAdministradorDTO;
import com.asistenteVirtual.DTOs.AdministradorDto;
import com.asistenteVirtual.services.administradores.AdministradorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para manejar operaciones sobre administradores.
 */
@RestController
@RequestMapping("/api/admin/administradores")
@RequiredArgsConstructor
public class AdministradorController {

    private final AdministradorService administradorService;

    /**
     * Crea un nuevo administrador.
     *
     * @param dto Datos del administrador a crear.
     * @return El administrador creado.
     */
    @PostMapping
    public ResponseEntity<?> crearAdministrador(@RequestBody @Valid AdministradorDto dto) {
        return ResponseEntity.ok(administradorService.crearAdministrador(dto));
    }

    /**
     * Obtiene un administrador por su ID.
     *
     * @param id ID del administrador.
     * @return El administrador encontrado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(administradorService.obtenerPorId(id));
    }

    /**
     * Obtiene todos los administradores.
     *
     * @return Lista de administradores registrados.
     */
    @GetMapping
    public ResponseEntity<?> obtenerTodos() {
        return ResponseEntity.ok(administradorService.obtenerTodos());
    }

    /**
     * Actualiza un administrador existente.
     *
     * @param id  ID del administrador a actualizar.
     * @param dto Nuevos datos del administrador.
     * @return Administrador actualizado.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody ActualizarAdministradorDTO dto) {
        return ResponseEntity.ok(administradorService.actualizarAdministrador(id, dto));
    }

    /**
     * Elimina un administrador por su ID.
     *
     * @param id ID del administrador a eliminar.
     * @return Respuesta sin contenido.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        administradorService.eliminarAdministrador(id);
        return ResponseEntity.noContent().build();
    }
}


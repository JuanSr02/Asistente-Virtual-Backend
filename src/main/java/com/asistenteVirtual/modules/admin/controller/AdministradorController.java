package com.asistenteVirtual.modules.admin.controller;

import com.asistenteVirtual.modules.admin.dto.AdministradorRequest;
import com.asistenteVirtual.modules.admin.dto.AdministradorResponse;
import com.asistenteVirtual.modules.admin.dto.AdministradorUpdate;
import com.asistenteVirtual.modules.admin.service.AdministradorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/administradores")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class AdministradorController {

    private final AdministradorService administradorService;

    @PostMapping
    public ResponseEntity<AdministradorResponse> crear(@RequestBody @Valid AdministradorRequest dto) {
        return ResponseEntity.ok(administradorService.crearAdministrador(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdministradorResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(administradorService.obtenerPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<AdministradorResponse>> obtenerTodos() {
        return ResponseEntity.ok(administradorService.obtenerTodos());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AdministradorResponse> actualizar(
            @PathVariable Long id,
            @RequestBody @Valid AdministradorUpdate dto) {
        return ResponseEntity.ok(administradorService.actualizarAdministrador(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        administradorService.eliminarAdministrador(id);
        return ResponseEntity.noContent().build();
    }
}
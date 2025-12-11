package com.asistenteVirtual.modules.admin.service;

import com.asistenteVirtual.common.exceptions.ResourceNotFoundException;
import com.asistenteVirtual.common.repository.PersonaRepository;
import com.asistenteVirtual.modules.admin.dto.AdministradorRequest;
import com.asistenteVirtual.modules.admin.dto.AdministradorResponse;
import com.asistenteVirtual.modules.admin.dto.AdministradorUpdate;
import com.asistenteVirtual.modules.admin.model.Administrador;
import com.asistenteVirtual.modules.admin.repository.AdministradorRepository;
import com.asistenteVirtual.modules.security.service.SupabaseAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdministradorService {

    private final AdministradorRepository administradorRepo;
    private final PersonaRepository personaRepo;
    private final SupabaseAuthService supabaseAuthService;

    public AdministradorResponse crearAdministrador(AdministradorRequest dto) {
        if (personaRepo.existsByMail(dto.mail())) {
            throw new IllegalArgumentException("El email ya está registrado en el sistema.");
        }

        var admin = Administrador.builder()
                .nombreApellido(dto.nombreApellido())
                .mail(dto.mail())
                .telefono(dto.telefono())
                .supabaseUserId(dto.supabaseUserId())
                .rolUsuario(dto.role())
                .build();

        return AdministradorResponse.fromEntity(administradorRepo.save(admin));
    }

    @Transactional(readOnly = true)
    public AdministradorResponse obtenerPorId(Long id) {
        return administradorRepo.findById(id)
                .map(AdministradorResponse::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<AdministradorResponse> obtenerTodos() {
        return administradorRepo.findAll().stream()
                .map(AdministradorResponse::fromEntity)
                .toList();
    }

    public AdministradorResponse actualizarAdministrador(Long id, AdministradorUpdate dto) {
        var admin = administradorRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador no encontrado con ID: " + id));

        boolean actualizarSupabase = false;
        String nuevoEmail = null;

        if (dto.nombreApellido() != null) admin.setNombreApellido(dto.nombreApellido());
        if (dto.telefono() != null) admin.setTelefono(dto.telefono());

        // Validación de cambio de mail
        if (dto.mail() != null && !dto.mail().equals(admin.getMail())) {
            if (personaRepo.existsByMail(dto.mail())) {
                throw new IllegalArgumentException("El email ya está en uso por otro usuario.");
            }
            admin.setMail(dto.mail());
            nuevoEmail = dto.mail();
            actualizarSupabase = true;
        }

        if (dto.contrasenia() != null && !dto.contrasenia().isBlank()) {
            actualizarSupabase = true;
        }

        // Sincronización con Supabase (Auth)
        if (actualizarSupabase && admin.getSupabaseUserId() != null) {
            supabaseAuthService.actualizarUsuarioSupabase(
                    admin.getSupabaseUserId(),
                    nuevoEmail,
                    dto.contrasenia()
            );
        }

        return AdministradorResponse.fromEntity(administradorRepo.save(admin));
    }

    public void eliminarAdministrador(Long id) {
        var admin = administradorRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador no encontrado con ID: " + id));

        if (admin.getSupabaseUserId() != null) {
            supabaseAuthService.eliminarUsuarioSupabase(admin.getSupabaseUserId());
        }

        administradorRepo.delete(admin);
    }
}
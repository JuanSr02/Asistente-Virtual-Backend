package com.asistenteVirtual.services.administradores;

import com.asistenteVirtual.DTOs.ActualizarAdministradorDTO;
import com.asistenteVirtual.DTOs.AdministradorDto;
import com.asistenteVirtual.DTOs.AdministradorResponseDTO;
import com.asistenteVirtual.common.exceptions.ResourceNotFoundException;
import com.asistenteVirtual.common.repository.PersonaRepository;
import com.asistenteVirtual.model.Administrador;
import com.asistenteVirtual.modules.security.service.SupabaseAuthService;
import com.asistenteVirtual.repository.AdministradorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdministradorServiceImpl implements AdministradorService {

    private final AdministradorRepository administradorRepository;
    private final PersonaRepository personaRepository;
    private final SupabaseAuthService supabaseAuthService;

    @Override
    public AdministradorResponseDTO crearAdministrador(AdministradorDto dto) {
        Administrador admin = Administrador.builder()
                .nombreApellido(dto.getNombreApellido())
                .mail(dto.getMail())
                .telefono(dto.getTelefono())
                .supabaseUserId(dto.getSupabaseUserId())
                .rol_usuario(dto.getRole())
                .build();
        return AdministradorResponseDTO.fromEntity(administradorRepository.save(admin));
    }

    @Override
    public AdministradorResponseDTO obtenerPorId(Long id) {
        return AdministradorResponseDTO.fromEntity(administradorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador no encontrado con ID: " + id)));
    }

    @Override
    public List<AdministradorResponseDTO> obtenerTodos() {
        List<AdministradorResponseDTO> list = new ArrayList<>();
        for (Administrador a : administradorRepository.findAll()) {
            list.add(AdministradorResponseDTO.fromEntity(a));
        }
        return list;
    }

    @Override
    public AdministradorResponseDTO actualizarAdministrador(Long id, ActualizarAdministradorDTO dto) {
        Administrador existente = administradorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador no encontrado con ID: " + id));

        boolean actualizarEnSupabase = false;
        String nuevoMail = null;
        String nuevaPassword = null;

        if (dto.getNombreApellido() != null) {
            existente.setNombreApellido(dto.getNombreApellido());
        }

        if (dto.getTelefono() != null) {
            existente.setTelefono(dto.getTelefono());
        }

        if (dto.getMail() != null) {
            boolean mailExistente = personaRepository.existsByMail(dto.getMail());
            if (mailExistente && !dto.getMail().equals(existente.getMail())) {
                throw new IllegalArgumentException("Ya existe una persona con ese correo electrónico.");
            }

            if (!dto.getMail().equals(existente.getMail())) {
                nuevoMail = dto.getMail();
                existente.setMail(nuevoMail);
                actualizarEnSupabase = true;
            }
        }

        if (dto.getContrasenia() != null && !dto.getContrasenia().isBlank()) {
            nuevaPassword = dto.getContrasenia();
            actualizarEnSupabase = true;
        }

        // Si hay cambios relevantes, se actualiza también en auth.users
        if (actualizarEnSupabase) {
            String supabaseUserId = existente.getSupabaseUserId();
            if (supabaseUserId != null && !supabaseUserId.isBlank()) {
                supabaseAuthService.actualizarUsuarioSupabase(supabaseUserId, nuevoMail, nuevaPassword);
            }
        }

        Administrador actualizado = administradorRepository.save(existente);
        return AdministradorResponseDTO.fromEntity(actualizado);
    }


    @Override
    public void eliminarAdministrador(Long id) {
        Administrador admin = administradorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador no encontrado con ID: " + id));

        // ✅ Eliminar usuario de Supabase Auth si tiene supabaseUserId
        if (admin.getSupabaseUserId() != null) {
            supabaseAuthService.eliminarUsuarioSupabase(admin.getSupabaseUserId());
        }

        administradorRepository.delete(admin); // borra admin y persona por cascade
    }

}

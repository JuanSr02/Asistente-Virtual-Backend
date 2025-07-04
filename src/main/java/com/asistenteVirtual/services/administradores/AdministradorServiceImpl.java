package com.asistenteVirtual.services.administradores;

import com.asistenteVirtual.DTOs.ActualizarAdministradorDTO;
import com.asistenteVirtual.DTOs.AdministradorDto;
import com.asistenteVirtual.DTOs.AdministradorResponseDTO;
import com.asistenteVirtual.exceptions.ResourceNotFoundException;
import com.asistenteVirtual.model.Administrador;
import com.asistenteVirtual.repository.AdministradorRepository;
import com.asistenteVirtual.repository.PersonaRepository;
import com.asistenteVirtual.services.supabase.SupabaseAuthService;
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

        if (dto.getNombreApellido() != null) {
            existente.setNombreApellido(dto.getNombreApellido());
        }
        if (dto.getMail() != null) {
            boolean mailExistente = personaRepository.existsByMail(dto.getMail());
            // Evitar conflicto con el mismo estudiante (es decir, si no cambió su propio mail)
            if (mailExistente && !dto.getMail().equals(existente.getMail())) {
                throw new IllegalArgumentException("Ya existe una persona con ese correo electrónico.");
            }
            existente.setMail(dto.getMail());
        }
        if (dto.getTelefono() != null) {
            existente.setTelefono(dto.getTelefono());
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

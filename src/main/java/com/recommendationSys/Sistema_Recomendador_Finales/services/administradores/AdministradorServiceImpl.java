package com.recommendationSys.Sistema_Recomendador_Finales.services.administradores;

import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.AdministradorDto;
import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.AdministradorResponseDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.exceptions.ResourceNotFoundException;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Administrador;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.AdministradorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdministradorServiceImpl implements AdministradorService {

    private final AdministradorRepository administradorRepository;

    @Override
    public AdministradorResponseDTO crearAdministrador(AdministradorDto dto) {
        Administrador admin = Administrador.builder()
                .nombreApellido(dto.getNombreApellido())
                .mail(dto.getMail())
                .telefono(dto.getTelefono())
                .supabaseUserId(dto.getSupabaseUserId())
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
        for (Administrador a : administradorRepository.findAll()){
            list.add(AdministradorResponseDTO.fromEntity(a));
        }
        return list;
    }

    @Override
    public AdministradorResponseDTO actualizarAdministrador(Long id, AdministradorDto dto) {
        Administrador existente = administradorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador no encontrado con ID: " + id));
        existente.setNombreApellido(dto.getNombreApellido());
        existente.setMail(dto.getMail());
        existente.setTelefono(dto.getTelefono());
        return AdministradorResponseDTO.fromEntity(administradorRepository.save(existente));
    }

    @Override
    public void eliminarAdministrador(Long id) {
        Administrador admin = administradorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador no encontrado con ID: " + id));
        administradorRepository.delete(admin);
    }
}

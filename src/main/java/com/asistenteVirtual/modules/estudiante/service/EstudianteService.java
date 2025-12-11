package com.asistenteVirtual.modules.estudiante.service;

import com.asistenteVirtual.common.exceptions.ResourceNotFoundException;
import com.asistenteVirtual.common.repository.PersonaRepository;
import com.asistenteVirtual.modules.estudiante.dto.EstudianteRequest;
import com.asistenteVirtual.modules.estudiante.dto.EstudianteResponse;
import com.asistenteVirtual.modules.estudiante.dto.EstudianteUpdate;
import com.asistenteVirtual.modules.estudiante.model.Estudiante;
import com.asistenteVirtual.modules.estudiante.repository.EstudianteRepository;
import com.asistenteVirtual.modules.security.service.SupabaseAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EstudianteService {

    private final EstudianteRepository estudianteRepository;
    private final PersonaRepository personaRepository;
    private final SupabaseAuthService supabaseAuthService;
    private final SecurityValidator securityValidator;

    public EstudianteResponse crearEstudiante(EstudianteRequest dto) {
        // Validar si el mail ya existe
        if (personaRepository.existsByMail(dto.mail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        var estudiante = Estudiante.builder()
                .nombreApellido(dto.nombreApellido())
                .mail(dto.mail())
                .telefono(dto.telefono())
                .supabaseUserId(dto.supabaseUserId())
                .rolUsuario(dto.role())
                .build();

        return EstudianteResponse.fromEntity(estudianteRepository.save(estudiante));
    }

    @Transactional(readOnly = true)
    public EstudianteResponse obtenerPorId(Long id) {
        securityValidator.validarAccesoEstudiante(id);
        return estudianteRepository.findById(id)
                .map(EstudianteResponse::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con ID: " + id));
    }

    public EstudianteResponse actualizarEstudiante(Long id, EstudianteUpdate dto) {
        securityValidator.validarAccesoEstudiante(id);
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con ID: " + id));

        boolean actualizarSupabase = false;
        String nuevoEmail = null;

        if (dto.nombreApellido() != null) estudiante.setNombreApellido(dto.nombreApellido());
        if (dto.telefono() != null) estudiante.setTelefono(dto.telefono());

        // Lógica de cambio de mail
        if (dto.mail() != null && !dto.mail().equals(estudiante.getMail())) {
            if (personaRepository.existsByMail(dto.mail())) {
                throw new IllegalArgumentException("El email ya está en uso por otro usuario");
            }
            estudiante.setMail(dto.mail());
            nuevoEmail = dto.mail();
            actualizarSupabase = true;
        }

        if (dto.contrasenia() != null && !dto.contrasenia().isBlank()) {
            actualizarSupabase = true;
        }

        if (actualizarSupabase && estudiante.getSupabaseUserId() != null) {
            supabaseAuthService.actualizarUsuarioSupabase(
                    estudiante.getSupabaseUserId(),
                    nuevoEmail,
                    dto.contrasenia()
            );
        }

        return EstudianteResponse.fromEntity(estudianteRepository.save(estudiante));
    }

    public void eliminarEstudiante(Long id) {
        securityValidator.validarAccesoEstudiante(id);
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con ID: " + id));

        if (estudiante.getSupabaseUserId() != null) {
            supabaseAuthService.eliminarUsuarioSupabase(estudiante.getSupabaseUserId());
        }

        estudianteRepository.delete(estudiante);
    }
}
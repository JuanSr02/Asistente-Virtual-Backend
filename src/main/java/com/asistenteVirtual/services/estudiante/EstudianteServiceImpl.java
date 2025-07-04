package com.asistenteVirtual.services.estudiante;

import com.asistenteVirtual.DTOs.ActualizarEstudianteDTO;
import com.asistenteVirtual.DTOs.EstudianteDto;
import com.asistenteVirtual.DTOs.EstudianteResponseDTO;
import com.asistenteVirtual.exceptions.ResourceNotFoundException;
import com.asistenteVirtual.model.Estudiante;
import com.asistenteVirtual.repository.EstudianteRepository;
import com.asistenteVirtual.repository.PersonaRepository;
import com.asistenteVirtual.services.supabase.SupabaseAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EstudianteServiceImpl implements EstudianteService {

    private final EstudianteRepository estudianteRepository;
    private final PersonaRepository personaRepository;
    private final SupabaseAuthService supabaseAuthService;

    @Override
    public EstudianteResponseDTO crearEstudiante(EstudianteDto dto) {
        Estudiante estudiante = Estudiante.builder()
                .nombreApellido(dto.getNombreApellido())
                .mail(dto.getMail())
                .telefono(dto.getTelefono())
                .supabaseUserId(dto.getSupabaseUserId())
                .rol_usuario(dto.getRole())
                .build();
        return EstudianteResponseDTO.fromEntity(estudianteRepository.save(estudiante));
    }

    @Override
    public EstudianteResponseDTO obtenerPorId(Long id) {
        return EstudianteResponseDTO.fromEntity(estudianteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con ID: " + id)));
    }

    @Override
    public List<EstudianteResponseDTO> obtenerTodos() {
        List<EstudianteResponseDTO> list = new ArrayList<>();
        for (Estudiante e : estudianteRepository.findAll()) {
            list.add(EstudianteResponseDTO.fromEntity(e));
        }
        return list;
    }

    @Override
    public EstudianteResponseDTO actualizarEstudiante(Long id, ActualizarEstudianteDTO dto) {
        Estudiante existente = estudianteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con ID: " + id));

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

        Estudiante actualizado = estudianteRepository.save(existente);
        return EstudianteResponseDTO.fromEntity(actualizado);
    }


    @Override
    public void eliminarEstudiante(Long id) {
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con ID: " + id));

        // ✅ Eliminar usuario de Supabase Auth si tiene supabaseUserId
        if (estudiante.getSupabaseUserId() != null) {
            supabaseAuthService.eliminarUsuarioSupabase(estudiante.getSupabaseUserId());
        }

        estudianteRepository.delete(estudiante);
    }
}

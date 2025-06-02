package com.recommendationSys.Sistema_Recomendador_Finales.services.estudiante;

import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.EstudianteDto;
import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.EstudianteResponseDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.exceptions.ResourceNotFoundException;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Estudiante;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.EstudianteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EstudianteServiceImpl implements EstudianteService {

    private final EstudianteRepository estudianteRepository;

    @Override
    public EstudianteResponseDTO crearEstudiante(EstudianteDto dto) {
        Estudiante estudiante = Estudiante.builder()
                .nombreApellido(dto.getNombreApellido())
                .mail(dto.getMail())
                .telefono(dto.getTelefono())
                .supabaseUserId(dto.getSupabaseUserId())
                .nroRegistro(dto.getNroRegistro())
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
        for (Estudiante e : estudianteRepository.findAll()){
            list.add(EstudianteResponseDTO.fromEntity(e));
        }
        return list;
    }

    @Override
    public EstudianteResponseDTO actualizarEstudiante(Long id, EstudianteDto dto) {
        Estudiante existente = estudianteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con ID: " + id));
        existente.setNombreApellido(dto.getNombreApellido());
        existente.setMail(dto.getMail());
        existente.setTelefono(dto.getTelefono());
        existente.setNroRegistro(dto.getNroRegistro());
        return EstudianteResponseDTO.fromEntity(estudianteRepository.save(existente));
    }

    @Override
    public void eliminarEstudiante(Long id) {
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con ID: " + id));
        estudianteRepository.delete(estudiante);
    }
}

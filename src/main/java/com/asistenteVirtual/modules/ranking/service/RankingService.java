package com.asistenteVirtual.modules.ranking.service;

import com.asistenteVirtual.common.exceptions.ResourceNotFoundException;
import com.asistenteVirtual.modules.estudiante.repository.EstudianteRepository;
import com.asistenteVirtual.modules.historiaAcademica.repository.HistoriaAcademicaRepository;
import com.asistenteVirtual.modules.planEstudio.dto.MateriaResponse;
import com.asistenteVirtual.modules.ranking.dto.FinalResponse;
import com.asistenteVirtual.modules.ranking.model.OrdenFinales;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // Por defecto lectura, optimiza rendimiento
public class RankingService {

    private final EstudianteRepository estudianteRepo;
    private final HistoriaAcademicaRepository historiaRepo;
    private final RankingStrategy rankingStrategy; // Inyectamos nuestra estrategia encapsulada

    public List<FinalResponse> obtenerFinalesParaRendir(Long estudianteId, OrdenFinales orden) {
        var historia = buscarHistoria(estudianteId);

        // 1. Buscar habilitadas
        List<FinalResponse> finales = rankingStrategy.buscarMateriasHabilitadas(historia);

        // 2. Ordenar
        return rankingStrategy.ordenar(finales, orden);
    }

    public List<MateriaResponse> obtenerFinalesParaInscribirse(Long estudianteId) {
        // Reutilizamos la lógica de búsqueda pero proyectamos a MateriaResponse simple
        var historia = buscarHistoria(estudianteId);

        return rankingStrategy.buscarMateriasHabilitadas(historia).stream()
                .map(f -> new MateriaResponse(f.codigoMateria(), f.nombreMateria()))
                .toList();
    }

    private com.asistenteVirtual.modules.historiaAcademica.model.HistoriaAcademica buscarHistoria(Long estudianteId) {
        // Validamos estudiante primero
        if (!estudianteRepo.existsById(estudianteId)) {
            throw new ResourceNotFoundException("Estudiante no encontrado");
        }
        return historiaRepo.findByEstudiante_IdAndEstado(estudianteId, "ACTIVA")
                .orElseThrow(() -> new ResourceNotFoundException("Historia académica no encontrada"));
    }
}
package com.asistenteVirtual.services.estadisticas;

import com.asistenteVirtual.DTOs.EstadisticasMateriaDTO;
import com.asistenteVirtual.exceptions.ResourceNotFoundException;
import com.asistenteVirtual.model.EstadisticasMateria;
import com.asistenteVirtual.model.Examen;
import com.asistenteVirtual.model.Experiencia;
import com.asistenteVirtual.model.Materia;
import com.asistenteVirtual.repository.ExamenRepository;
import com.asistenteVirtual.repository.ExperienciaRepository;
import com.asistenteVirtual.repository.MateriaRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EstadisticasMateriaPeriodoServiceImpl implements EstadisticasMateriaPeriodoService {
    private final ExamenRepository examenRepository;
    private final MateriaRepository materiaRepository;
    private final ExperienciaRepository experienciaRepository;
    private final EstadisticasHelper estadisticasHelper;

    @Override
    public EstadisticasMateriaDTO obtenerEstadisticasMateriaPorPeriodo(String codigoMateria, PeriodoEstadisticas periodo) {
        LocalDate fechaLimite = calcularFechaLimite(periodo);
        List<Materia> materiasConMismoCodigo = materiaRepository.findByCodigo(codigoMateria);

        if (materiasConMismoCodigo.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron materias con el código: " + codigoMateria);
        }

        List<Examen> examenesFiltrados = filtrarExamenesPorPeriodo(materiasConMismoCodigo, fechaLimite);
        List<Experiencia> experienciasFiltradas = filtrarExperienciasPorPeriodo(materiasConMismoCodigo, fechaLimite);

        EstadisticasMateria stats = calcularEstadisticasFiltradas(
                codigoMateria,
                materiasConMismoCodigo.getFirst().getNombre(),
                examenesFiltrados,
                experienciasFiltradas
        );

        return convertirADTO(stats);
    }

    private List<Examen> filtrarExamenesPorPeriodo(List<Materia> materias, LocalDate fechaLimite) {
        List<Examen> examenes = new ArrayList<>();
        for (Materia materia : materias) {
            if (fechaLimite != null) {
                examenes.addAll(examenRepository.findByMateriaAndFechaAfter(materia, fechaLimite));
            } else {
                examenes.addAll(examenRepository.findByMateriaWithJoins(materia));
            }
        }
        return examenes;
    }

    private List<Experiencia> filtrarExperienciasPorPeriodo(List<Materia> materias, LocalDate fechaLimite) {
        List<Experiencia> experiencias = new ArrayList<>();
        for (Materia materia : materias) {
            if (fechaLimite != null) {
                experiencias.addAll(experienciaRepository.findByMateriaAndFechaAfter(materia, fechaLimite));
            } else {
                experiencias.addAll(experienciaRepository.findByMateriaWithJoins(materia));
            }
        }
        return experiencias;
    }

    private EstadisticasMateria calcularEstadisticasFiltradas(String codigoMateria, String nombreMateria, List<Examen> examenes, List<Experiencia> experiencias) {
        int totalRendidos = examenes.size();
        int aprobados = estadisticasHelper.calcularAprobados(examenes);

        return EstadisticasMateria.builder()
                .codigoMateria(codigoMateria)
                .nombreMateria(nombreMateria)
                .totalRendidos(totalRendidos)
                .aprobados(aprobados)
                .reprobados(totalRendidos - aprobados)
                .promedioNotas(estadisticasHelper.calcularPromedioNotas(examenes))
                .promedioDiasEstudio(estadisticasHelper.calcularPromedioDiasEstudio(experiencias))
                .promedioHorasDiarias(estadisticasHelper.calcularPromedioHorasDiarias(experiencias))
                .promedioDificultad(estadisticasHelper.calcularPromedioDificultad(experiencias))
                .distribucionDificultad(estadisticasHelper.calcularDistribucionDificultad(experiencias))
                .distribucionModalidad(estadisticasHelper.calcularDistribucionModalidad(experiencias))
                .distribucionRecursos(estadisticasHelper.calcularDistribucionRecursos(experiencias))
                .fechaUltimaActualizacion(LocalDateTime.now())
                .build();
    }

    private EstadisticasMateriaDTO convertirADTO(EstadisticasMateria stats) {
        EstadisticasMateriaDTO dto = new EstadisticasMateriaDTO();
        dto.setCodigoMateria(stats.getCodigoMateria());
        dto.setNombreMateria(stats.getNombreMateria());
        dto.setTotalRendidos(stats.getTotalRendidos());
        dto.setAprobados(stats.getAprobados());
        dto.setReprobados(stats.getReprobados());
        dto.setPromedioNotas(stats.getPromedioNotas());
        dto.setPromedioDiasEstudio(stats.getPromedioDiasEstudio());
        dto.setPromedioHorasDiarias(stats.getPromedioHorasDiarias());
        dto.setPromedioDificultad(stats.getPromedioDificultad());
        dto.setPorcentajeAprobados(estadisticasHelper.calcularPorcentaje(stats.getAprobados(), stats.getTotalRendidos()));

        try {
            estadisticasHelper.cargarDistribuciones(dto, stats);
        } catch (JsonProcessingException ignored) {
        }

        dto.setFechaUltimaActualizacion(stats.getFechaUltimaActualizacion().toLocalDate());
        return dto;
    }

    private LocalDate calcularFechaLimite(PeriodoEstadisticas periodo) {
        return switch (periodo) {
            case ULTIMO_ANIO -> LocalDate.now().minusYears(1);
            case ULTIMOS_2_ANIOS -> LocalDate.now().minusYears(2);
            case ULTIMOS_5_ANIOS -> LocalDate.now().minusYears(5);
            default -> null;
        };
    }
}
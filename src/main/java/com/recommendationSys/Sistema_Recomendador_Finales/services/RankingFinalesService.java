package com.recommendationSys.Sistema_Recomendador_Finales.services;

import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.EstadisticasFinalDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.FinalDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.OrdenFinales;
import com.recommendationSys.Sistema_Recomendador_Finales.exceptions.ResourceNotFoundException;
import com.recommendationSys.Sistema_Recomendador_Finales.model.EstadisticasMateria;
import com.recommendationSys.Sistema_Recomendador_Finales.model.HistoriaAcademica;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Renglon;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankingFinalesService {

    private final HistoriaAcademicaRepository historiaAcademicaRepo;
    private final RenglonRepository renglonRepo;
    private final CorrelativaRepository correlativaRepo;
    private final EstadisticasMateriaRepository estadisticasRepo;
    private final EstudianteRepository estudianteRepo;

    public List<FinalDTO> obtenerFinalesParaRendir(Long estudianteId, OrdenFinales orden) {
        // 1. Obtener historia académica del estudiante
        HistoriaAcademica historia = historiaAcademicaRepo.findByEstudiante(estudianteRepo.findById(estudianteId).orElseThrow())
                .orElseThrow(() -> new ResourceNotFoundException("Historia académica no encontrada"));

        // 2. Obtener materias regulares aprobadas
        List<Renglon> regularesAprobadas = renglonRepo.findByHistoriaAcademicaAndTipoAndResultado(
                historia, "Regularidad", "Aprobado");

        // 3. Mapear a DTO con información adicional
        List<FinalDTO> finales = regularesAprobadas.stream()
                .map(renglon -> {
                    FinalDTO dto = new FinalDTO();
                    dto.setCodigoMateria(renglon.getMateria().getCodigo());
                    dto.setNombreMateria(renglon.getMateria().getNombre());
                    dto.setFechaRegularidad(renglon.getFecha());

                    // Calcular vencimiento (2 años y 9 meses desde la fecha de regularidad)
                    dto.setFechaVencimiento(dto.getFechaRegularidad().plusYears(2).plusMonths(9));
                    dto.setSemanasParaVencimiento(ChronoUnit.WEEKS.between(LocalDate.now(), dto.getFechaVencimiento()));

                    // Contar veces que es correlativa
                    dto.setVecesEsCorrelativa(correlativaRepo.countByCorrelativaCodigo(renglon.getMateria().getCodigo()));

                    // Obtener estadísticas
                    estadisticasRepo.findById(renglon.getMateria().getCodigo())
                            .ifPresent(est -> dto.setEstadisticas(mapEstadisticas(est)));

                    return dto;
                })
                .collect(Collectors.toList());

        // 4. Ordenar según el criterio seleccionado
        switch (orden) {
            case CORRELATIVAS:
                finales.sort(Comparator.comparingLong(FinalDTO::getVecesEsCorrelativa).reversed());
                break;

            case VENCIMIENTO:
                finales.sort(Comparator.comparingLong(FinalDTO::getSemanasParaVencimiento));
                break;

            case ESTADISTICAS:
                finales.sort(Comparator.<FinalDTO>comparingDouble(
                        f -> {
                            if (f.getEstadisticas() == null) {
                                return 0.0; // o algún valor por defecto
                            }
                            return f.getEstadisticas().getPorcentajeAprobados() /
                                    f.getEstadisticas().getPromedioDificultad();
                        }).reversed());
                break;
        }

        return finales;
    }

    private EstadisticasFinalDTO mapEstadisticas(EstadisticasMateria estadisticas) {
        EstadisticasFinalDTO dto = new EstadisticasFinalDTO();
        dto.setPorcentajeAprobados(estadisticas.getAprobados() * 100.0 / estadisticas.getTotalRendidos());
        dto.setPromedioNotas(estadisticas.getPromedioNotas());
        dto.setPromedioDiasEstudio(estadisticas.getPromedioDiasEstudio());
        dto.setPromedioHorasDiarias(estadisticas.getPromedioHorasDiarias());
        dto.setPromedioDificultad(estadisticas.getPromedioDificultad());
        return dto;
    }
}

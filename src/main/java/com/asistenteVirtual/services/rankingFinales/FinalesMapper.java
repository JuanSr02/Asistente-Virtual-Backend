package com.asistenteVirtual.services.rankingFinales;

import com.asistenteVirtual.DTOs.EstadisticasFinalDTO;
import com.asistenteVirtual.DTOs.FinalDTO;
import com.asistenteVirtual.model.EstadisticasMateria;
import com.asistenteVirtual.modules.historiaAcademica.model.Renglon;
import com.asistenteVirtual.modules.planEstudio.repository.CorrelativaRepository;
import com.asistenteVirtual.repository.EstadisticasMateriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class FinalesMapper {

    private final CorrelativaRepository correlativaRepo;
    private final EstadisticasMateriaRepository estadisticasRepo;

    public FinalDTO toFinalDTO(Renglon renglon) {
        return FinalDTO.builder()
                .codigoMateria(renglon.getMateria().getCodigo())
                .nombreMateria(renglon.getMateria().getNombre())
                .fechaRegularidad(renglon.getFecha())
                .fechaVencimiento(calcularFechaVencimiento(renglon.getFecha()))
                .semanasParaVencimiento(calcularSemanasParaVencimiento(renglon.getFecha()))
                .vecesEsCorrelativa(contarCorrelativas(renglon.getMateria().getCodigo(), renglon.getMateria().getPlanDeEstudio().getCodigo()))
                .estadisticas(mapearEstadisticas(renglon.getMateria().getCodigo()))
                .build();
    }

    private LocalDate calcularFechaVencimiento(LocalDate fechaRegularidad) {
        return fechaRegularidad.plusYears(2).plusMonths(9);
    }

    private long calcularSemanasParaVencimiento(LocalDate fechaRegularidad) {
        return ChronoUnit.WEEKS.between(LocalDate.now(), calcularFechaVencimiento(fechaRegularidad));
    }

    private long contarCorrelativas(String codigoMateria, String codigoPlan) {
        return correlativaRepo.countByCorrelativaCodigo_CodigoAndCorrelativaCodigo_PlanDeEstudio_Codigo(codigoMateria, codigoPlan);
    }

    private EstadisticasFinalDTO mapearEstadisticas(String codigoMateria) {
        return estadisticasRepo.findById(codigoMateria)
                .map(this::toEstadisticasDTO)
                .orElse(null);
    }

    private EstadisticasFinalDTO toEstadisticasDTO(EstadisticasMateria estadisticas) {
        return EstadisticasFinalDTO.builder()
                .porcentajeAprobados(calcularPorcentajeAprobados(estadisticas))
                .promedioNotas(estadisticas.getPromedioNotas())
                .promedioDiasEstudio(estadisticas.getPromedioDiasEstudio())
                .promedioHorasDiarias(estadisticas.getPromedioHorasDiarias())
                .promedioDificultad(estadisticas.getPromedioDificultad())
                .build();
    }

    private double calcularPorcentajeAprobados(EstadisticasMateria estadisticas) {
        return estadisticas.getAprobados() * 100.0 / estadisticas.getTotalRendidos();
    }
}
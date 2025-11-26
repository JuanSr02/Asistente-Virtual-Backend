package com.asistenteVirtual.modules.ranking.service;

import com.asistenteVirtual.modules.estadisticas.model.EstadisticasMateria;
import com.asistenteVirtual.modules.estadisticas.repository.EstadisticasMateriaRepository;
import com.asistenteVirtual.modules.historiaAcademica.model.HistoriaAcademica;
import com.asistenteVirtual.modules.historiaAcademica.model.Renglon;
import com.asistenteVirtual.modules.historiaAcademica.repository.RenglonRepository;
import com.asistenteVirtual.modules.planEstudio.repository.CorrelativaRepository;
import com.asistenteVirtual.modules.ranking.dto.EstadisticasFinalResponse;
import com.asistenteVirtual.modules.ranking.dto.FinalResponse;
import com.asistenteVirtual.modules.ranking.model.OrdenFinales;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
class RankingStrategy { // Package-private: Solo el servicio lo usa

    private final RenglonRepository renglonRepo;
    private final CorrelativaRepository correlativaRepo;
    private final EstadisticasMateriaRepository estadisticasRepo;

    /**
     * Busca las materias regulares que el alumno puede rendir (filtra correlativas no aprobadas).
     */
    public List<FinalResponse> buscarMateriasHabilitadas(HistoriaAcademica historia) {
        List<Renglon> regulares = renglonRepo.findRegularesHabilitadas(historia.getId());

        return regulares.stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<FinalResponse> ordenar(List<FinalResponse> finales, OrdenFinales criterio) {
        // Hacemos una copia mutable de la lista (toList() retorna inmutable en Java 16+)
        List<FinalResponse> listaOrdenable = new java.util.ArrayList<>(finales);

        switch (criterio) {
            case CORRELATIVAS ->
                    listaOrdenable.sort(Comparator.comparingLong(FinalResponse::vecesEsCorrelativa).reversed());
            case VENCIMIENTO -> listaOrdenable.sort(Comparator.comparingLong(FinalResponse::semanasParaVencimiento));
            case ESTADISTICAS ->
                    listaOrdenable.sort(Comparator.comparingDouble(this::calcularPuntajeEstadisticas).reversed());
        }
        return listaOrdenable;
    }

    // --- Métodos de Mapeo y Cálculo Interno ---

    private FinalResponse mapToResponse(Renglon renglon) {
        String codigo = renglon.getMateria().getCodigo();
        String plan = renglon.getMateria().getPlanDeEstudio().getCodigo();
        LocalDate fechaReg = renglon.getFecha();

        return new FinalResponse(
                codigo,
                renglon.getMateria().getNombre(),
                fechaReg,
                calcularVencimiento(fechaReg),
                calcularSemanasRestantes(fechaReg),
                contarCorrelativas(codigo, plan),
                obtenerEstadisticas(codigo)
        );
    }

    private LocalDate calcularVencimiento(LocalDate fechaRegularidad) {
        return fechaRegularidad.plusYears(2).plusMonths(9); // Regla de negocio hardcodeada (podría ser configurable)
    }

    private long calcularSemanasRestantes(LocalDate fechaRegularidad) {
        return ChronoUnit.WEEKS.between(LocalDate.now(), calcularVencimiento(fechaRegularidad));
    }

    private long contarCorrelativas(String codigoMateria, String codigoPlan) {
        return correlativaRepo.countByCorrelativaCodigo_CodigoAndCorrelativaCodigo_PlanDeEstudio_Codigo(codigoMateria, codigoPlan);
    }

    private EstadisticasFinalResponse obtenerEstadisticas(String codigoMateria) {
        return estadisticasRepo.findById(codigoMateria)
                .map(this::mapEstadisticas)
                .orElse(null);
    }

    private EstadisticasFinalResponse mapEstadisticas(EstadisticasMateria stats) {
        // Usamos el builder del DTO existente (asumiendo que ya fue refactorizado o se usa el viejo temporalmente)
        return EstadisticasFinalResponse.builder()
                .porcentajeAprobados(stats.getTotalRendidos() > 0 ? (double) stats.getAprobados() / stats.getTotalRendidos() * 100 : 0)
                .promedioNotas(stats.getPromedioNotas())
                .promedioDiasEstudio(stats.getPromedioDiasEstudio())
                .promedioHorasDiarias(stats.getPromedioHorasDiarias())
                .promedioDificultad(stats.getPromedioDificultad())
                .build();
    }

    private double calcularPuntajeEstadisticas(FinalResponse f) {
        if (f.estadisticas() == null || f.estadisticas().getPromedioDificultad() == null || f.estadisticas().getPromedioDificultad() == 0) {
            return 0.0;
        }
        // Fórmula: Más aprobados y menos dificultad = Mejor puntaje
        return f.estadisticas().getPorcentajeAprobados() / f.estadisticas().getPromedioDificultad();
    }
}
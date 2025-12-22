package com.asistenteVirtual.modules.ranking.service;

import com.asistenteVirtual.config.AcademicProperties;
import com.asistenteVirtual.modules.estadisticas.model.EstadisticasMateria;
import com.asistenteVirtual.modules.estadisticas.model.PeriodoEstadisticas;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
class RankingStrategy {

    private final RenglonRepository renglonRepo;
    private final CorrelativaRepository correlativaRepo;
    private final EstadisticasMateriaRepository estadisticasRepo;
    private final AcademicProperties academicConfig;

    public List<FinalResponse> buscarMateriasHabilitadas(HistoriaAcademica historia) {
        List<Renglon> regulares = renglonRepo.findRegularesHabilitadas(historia.getId());
        if (regulares.isEmpty()) return List.of();

        List<String> codigos = regulares.stream()
                .map(r -> r.getMateria().getCodigo())
                .toList();
        String planCodigo = historia.getPlanDeEstudio().getCodigo();

        Map<String, Long> correlativasMap = correlativaRepo.contarCorrelativasFuturasMasivo(codigos, planCodigo)
                .stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (Long) row[1]
                ));

        Map<String, EstadisticasMateria> statsMap = estadisticasRepo
                .findByCodigoMateriaInAndPeriodo(codigos, PeriodoEstadisticas.TODOS_LOS_TIEMPOS.toString())
                .stream()
                .collect(Collectors.toMap(EstadisticasMateria::getCodigoMateria, Function.identity()));

        return regulares.stream()
                .map(r -> mapToResponseOptimizado(r, correlativasMap, statsMap))
                .toList();
    }

    public List<FinalResponse> ordenar(List<FinalResponse> finales, OrdenFinales criterio) {
        List<FinalResponse> listaOrdenable = new ArrayList<>(finales);
        switch (criterio) {
            case CORRELATIVAS ->
                    listaOrdenable.sort(Comparator.comparingLong(FinalResponse::vecesEsCorrelativa).reversed());
            case VENCIMIENTO -> listaOrdenable.sort(Comparator.comparingLong(FinalResponse::semanasParaVencimiento));
            case ESTADISTICAS ->
                // Usamos el metodo auxiliar (this::extraerPuntaje)
                // .reversed() para que los de mayor puntaje queden arriba
                    listaOrdenable.sort(Comparator.comparingDouble(this::extraerPuntaje).reversed());
        }
        return listaOrdenable;
    }

    // --- Helpers Internos ---

    private double extraerPuntaje(FinalResponse f) {
        // Verificamos nulos para evitar NullPointerException
        if (f.estadisticas() != null && f.estadisticas().getPuntaje() != null) {
            return f.estadisticas().getPuntaje();
        }
        return 0.0; // Si no hay datos, el puntaje es 0
    }

    private FinalResponse mapToResponseOptimizado(Renglon renglon, Map<String, Long> correlativasMap, Map<String, EstadisticasMateria> statsMap) {
        String codigo = renglon.getMateria().getCodigo();
        LocalDate fechaReg = renglon.getFecha();

        // Obtener datos de los mapas en memoria
        long cantCorrelativas = correlativasMap.getOrDefault(codigo, 0L);
        EstadisticasMateria stats = statsMap.get(codigo);

        return new FinalResponse(
                codigo,
                renglon.getMateria().getNombre(),
                fechaReg,
                calcularVencimiento(fechaReg),
                calcularSemanasRestantes(fechaReg),
                cantCorrelativas,
                mapEstadisticas(stats)
        );
    }


    private LocalDate calcularVencimiento(LocalDate fechaRegularidad) {
        return fechaRegularidad
                .plusYears(academicConfig.getRegularidadAnios())
                .plusMonths(academicConfig.getRegularidadMeses());
    }

    private long calcularSemanasRestantes(LocalDate fechaRegularidad) {
        return ChronoUnit.WEEKS.between(LocalDate.now(), calcularVencimiento(fechaRegularidad));
    }

    private EstadisticasFinalResponse mapEstadisticas(EstadisticasMateria stats) {
        if (stats == null) return null;

        double aprobadosPct = stats.getTotalRendidos() > 0
                ? (double) stats.getAprobados() / stats.getTotalRendidos() * 100
                : 0;

        double dificultad = stats.getPromedioDificultad() != null ? stats.getPromedioDificultad() : 0.0;

        return EstadisticasFinalResponse.builder()
                .porcentajeAprobados(aprobadosPct)
                .promedioNotas(stats.getPromedioNotas())
                .promedioDiasEstudio(stats.getPromedioDiasEstudio())
                .promedioHorasDiarias(stats.getPromedioHorasDiarias())
                .promedioDificultad(stats.getPromedioDificultad())
                .puntaje(calcularScorePonderado(aprobadosPct, dificultad))
                .build();
    }

    /**
     * Opción 1: Promedio Ponderado
     * 70% Peso a la tasa de aprobación
     * 30% Peso a la facilidad (inversa de dificultad)
     */
    private double calcularScorePonderado(double porcentajeAprobados, double dificultadPromedio) {
        // Normalizar dificultad (1-10) a Facilidad (0-100)
        // Si dificultad es 0 o nula, asumimos dificultad media (5) para no penalizar ni premiar en exceso
        double dificultadEfectiva = dificultadPromedio > 0 ? dificultadPromedio : 5.0;

        // Dificultad 1 -> Facilidad 100 | Dificultad 10 -> Facilidad 0
        double facilidad = (10.0 - dificultadEfectiva) * (100.0 / 9.0);
        if (facilidad < 0) facilidad = 0;
        if (facilidad > 100) facilidad = 100;

        // Ponderación
        return (porcentajeAprobados * 0.7) + (facilidad * 0.3);
    }

}
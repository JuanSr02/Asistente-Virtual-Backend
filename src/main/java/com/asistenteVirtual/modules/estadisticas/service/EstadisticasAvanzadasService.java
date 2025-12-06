package com.asistenteVirtual.modules.estadisticas.service;

import com.asistenteVirtual.common.exceptions.ResourceNotFoundException;
import com.asistenteVirtual.common.utils.JsonConverter;
import com.asistenteVirtual.modules.estadisticas.dto.EstadisticasGeneralesResponse;
import com.asistenteVirtual.modules.estadisticas.dto.MateriaRankingResponse;
import com.asistenteVirtual.modules.estadisticas.model.EstadisticasPorCarrera;
import com.asistenteVirtual.modules.estadisticas.model.PeriodoEstadisticas;
import com.asistenteVirtual.modules.estadisticas.repository.EstadisticasPorCarreraRepository;
import com.asistenteVirtual.modules.historiaAcademica.model.Examen;
import com.asistenteVirtual.modules.historiaAcademica.model.HistoriaAcademica;
import com.asistenteVirtual.modules.historiaAcademica.repository.ExamenRepository;
import com.asistenteVirtual.modules.historiaAcademica.repository.HistoriaAcademicaRepository;
import com.asistenteVirtual.modules.planEstudio.model.Materia;
import com.asistenteVirtual.modules.planEstudio.model.PlanDeEstudio;
import com.asistenteVirtual.modules.planEstudio.repository.MateriaRepository;
import com.asistenteVirtual.modules.planEstudio.repository.PlanDeEstudioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EstadisticasAvanzadasService {

    private final ExamenRepository examenRepository;
    private final MateriaRepository materiaRepository;
    private final PlanDeEstudioRepository planRepository;
    private final HistoriaAcademicaRepository historiaRepository;
    private final EstadisticasPorCarreraRepository statsPorCarreraRepo;

    private final EstadisticasCalculatorHelper helper;
    private final JsonConverter jsonConverter;

    @Transactional
    public EstadisticasGeneralesResponse obtenerEstadisticasPorCarrera(String codigoPlan, PeriodoEstadisticas periodo) {
        PlanDeEstudio plan = planRepository.findById(codigoPlan)
                .orElseThrow(() -> new ResourceNotFoundException("Plan de estudio no encontrado: " + codigoPlan));

        // 1. Obtener datos filtrados
        LocalDate fechaLimite = calcularFechaLimite(periodo);
        List<Materia> materiasCarrera = materiaRepository.findByPlanDeEstudio_Codigo(codigoPlan);
        List<String> codigosMaterias = materiasCarrera.stream().map(Materia::getCodigo).toList();

        List<Examen> examenesFiltrados = filtrarExamenes(codigosMaterias, fechaLimite, codigoPlan);
        if (examenesFiltrados.isEmpty()) return null;
        List<HistoriaAcademica> historiasCarrera = historiaRepository.findByPlanDeEstudio_Codigo(codigoPlan);

        // 2. Construir el DTO de respuesta (Cálculo en memoria)
        EstadisticasGeneralesResponse response = calcularMetricas(plan, examenesFiltrados, historiasCarrera, codigosMaterias);

        // 3. Persistir el snapshot para caché o histórico
        guardarSnapshot(codigoPlan, periodo, response);

        return response;
    }

    private EstadisticasGeneralesResponse calcularMetricas(PlanDeEstudio plan, List<Examen> examenes,
                                                           List<HistoriaAcademica> historias, List<String> codigosMaterias) {
        if (examenes.isEmpty()) {
            return construirRespuestaVacia(codigosMaterias.size(), historias.size());
        }

        // Cálculos básicos
        long totalExamenes = examenes.size();
        Double promedioGeneral = helper.calcularPromedioNotas(examenes);
        Double porcentajeAprobados = helper.calcularPorcentajeGlobal(examenes);

        // Rankings y Distribuciones
        // Nota: No podemos usar las queries SQL nativas del repo general porque aquí filtramos por FECHA y PLAN
        // Debemos calcular en memoria sobre la lista 'examenesFiltrados'.

        MateriaRankingResponse materiaMasRendida = calcularMateriaMasRendida(examenes);
        List<MateriaRankingResponse> top5Aprobadas = calcularTopRankings(examenes, true);
        List<MateriaRankingResponse> top5Reprobadas = calcularTopRankings(examenes, false);

        Map<String, Integer> distExamenes = helper.calcularDistribucionExamenesPorMateria(examenes);
        // La distribución de estudiantes por carrera es trivial aquí (todos son de esta carrera), 
        // pero mantenemos el mapa por compatibilidad con el frontend.
        Map<String, Integer> distEstudiantes = Map.of(plan.getPropuesta(), historias.size());

        Map<String, Double> promediosMateria = calcularPromediosPorMateriaEnMemoria(examenes);

        return new EstadisticasGeneralesResponse(
                (long) historias.size(),
                codigosMaterias.size(),
                (int) totalExamenes,
                porcentajeAprobados,
                promedioGeneral,
                distEstudiantes,
                distExamenes,
                materiaMasRendida,
                materiaMasRendida.cantidad(),
                top5Aprobadas,
                top5Reprobadas,
                promediosMateria
        );
    }

    private void guardarSnapshot(String codigoPlan, PeriodoEstadisticas periodo, EstadisticasGeneralesResponse dto) {
        // Borramos snapshot previo para evitar duplicados
        statsPorCarreraRepo.deleteByCodigoPlanAndPeriodo(codigoPlan, periodo.toString());

        EstadisticasPorCarrera stats = EstadisticasPorCarrera.builder()
                .codigoPlan(codigoPlan)
                .periodo(periodo.toString())
                .estudiantesActivos(dto.estudiantesActivos().intValue())
                .totalMaterias(dto.totalMaterias())
                .totalExamenesRendidos(dto.totalExamenesRendidos())
                .porcentajeAprobadosGeneral(dto.porcentajeAprobadosGeneral())
                .promedioGeneral(dto.promedioGeneral())
                .distribucionExamenesPorMateria(jsonConverter.toJson(dto.distribucionExamenesPorMateria()))
                .top5Aprobadas(jsonConverter.toJson(dto.top5Aprobadas()))
                .top5Reprobadas(jsonConverter.toJson(dto.top5Reprobadas()))
                .promedioNotasPorMateria(jsonConverter.toJson(dto.promedioNotasPorMateria()))
                .materiaMasRendida(jsonConverter.toJson(dto.materiaMasRendida()))
                .cantidadMateriaMasRendida(dto.cantidadMateriaMasRendida() != null ? dto.cantidadMateriaMasRendida() : 0)
                .fechaUltimaActualizacion(LocalDateTime.now())
                .build();

        statsPorCarreraRepo.save(stats);
    }

    // --- Métodos de Cálculo en Memoria (necesarios porque filtramos por fecha) ---

    private List<MateriaRankingResponse> calcularTopRankings(List<Examen> examenes, boolean buscarAprobadas) {
        // Agrupar exámenes por código de materia
        Map<String, List<Examen>> porMateria = examenes.stream()
                .collect(Collectors.groupingBy(e -> e.getRenglon().getMateria().getCodigo()));

        return porMateria.entrySet().stream()
                .map(entry -> {
                    String codigo = entry.getKey();
                    List<Examen> listaExamenes = entry.getValue();
                    String nombre = listaExamenes.get(0).getRenglon().getMateria().getNombre();

                    long total = listaExamenes.size();
                    long aprobados = helper.calcularAprobados(listaExamenes);

                    // Si buscamos aprobadas, ordenamos por % aprobados. Si reprobadas, por % reprobados.
                    double score = buscarAprobadas
                            ? helper.calcularPorcentaje(aprobados, total)
                            : helper.calcularPorcentaje(total - aprobados, total); // % Reprobados

                    return new MateriaRankingResponse(codigo, nombre, score, 0L);
                })
                .sorted(Comparator.comparingDouble(MateriaRankingResponse::porcentaje).reversed())
                .limit(5)
                .toList();
    }

    private MateriaRankingResponse calcularMateriaMasRendida(List<Examen> examenes) {
        if (examenes.isEmpty()) return null;

        Map<String, Long> conteo = examenes.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getRenglon().getMateria().getCodigo(),
                        Collectors.counting()
                ));

        return conteo.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> {
                    String codigo = entry.getKey();
                    // Buscamos el nombre de cualquier examen de esa materia
                    String nombre = examenes.stream()
                            .filter(e -> e.getRenglon().getMateria().getCodigo().equals(codigo))
                            .findFirst()
                            .map(e -> e.getRenglon().getMateria().getNombre())
                            .orElse("Desconocida");

                    // Calculamos aprobados para el DTO
                    long total = entry.getValue();
                    long aprobados = examenes.stream()
                            .filter(e -> e.getRenglon().getMateria().getCodigo().equals(codigo) && e.getNota() >= 4)
                            .count();

                    return helper.crearRankingDTO(codigo, nombre, total, aprobados);
                })
                .orElse(null);
    }

    private Map<String, Double> calcularPromediosPorMateriaEnMemoria(List<Examen> examenes) {
        return examenes.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getRenglon().getMateria().getNombre(),
                        Collectors.averagingDouble(Examen::getNota)
                ));
    }

    private List<Examen> filtrarExamenes(List<String> codigosMaterias, LocalDate fechaLimite, String plan) {
        if (fechaLimite == null) {
            return examenRepository.findByRenglon_Materia_CodigoIn(codigosMaterias, plan);
        }
        return examenRepository.findByRenglon_Materia_CodigoInAndFechaAfter(codigosMaterias, fechaLimite, plan);
    }

    private LocalDate calcularFechaLimite(PeriodoEstadisticas periodo) {
        return switch (periodo) {
            case ULTIMO_ANIO -> LocalDate.now().minusYears(1);
            case ULTIMOS_2_ANIOS -> LocalDate.now().minusYears(2);
            case ULTIMOS_5_ANIOS -> LocalDate.now().minusYears(5);
            case TODOS_LOS_TIEMPOS -> null;
        };
    }

    private EstadisticasGeneralesResponse construirRespuestaVacia(int totalMaterias, long estudiantesActivos) {
        return new EstadisticasGeneralesResponse(
                estudiantesActivos, totalMaterias, 0, 0.0, 0.0,
                Collections.emptyMap(), Collections.emptyMap(), null, 0L,
                Collections.emptyList(), Collections.emptyList(), Collections.emptyMap()
        );
    }
}
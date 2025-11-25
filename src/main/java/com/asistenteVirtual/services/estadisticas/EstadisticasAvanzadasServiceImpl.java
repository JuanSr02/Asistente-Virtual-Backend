package com.asistenteVirtual.services.estadisticas;

import com.asistenteVirtual.DTOs.EstadisticasGeneralesDTO;
import com.asistenteVirtual.DTOs.MateriaRankingDTO;
import com.asistenteVirtual.model.EstadisticasPorCarrera;
import com.asistenteVirtual.model.Examen;
import com.asistenteVirtual.model.HistoriaAcademica;
import com.asistenteVirtual.modules.planEstudio.model.Materia;
import com.asistenteVirtual.modules.planEstudio.model.PlanDeEstudio;
import com.asistenteVirtual.modules.planEstudio.repository.MateriaRepository;
import com.asistenteVirtual.modules.planEstudio.repository.PlanDeEstudioRepository;
import com.asistenteVirtual.repository.EstadisticasPorCarreraRepository;
import com.asistenteVirtual.repository.ExamenRepository;
import com.asistenteVirtual.repository.HistoriaAcademicaRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EstadisticasAvanzadasServiceImpl implements EstadisticasAvanzadasService {
    private final ExamenRepository examenRepository;
    private final MateriaRepository materiaRepository;
    private final PlanDeEstudioRepository planDeEstudioRepository;
    private final EstadisticasHelper estadisticasHelper;
    private final HistoriaAcademicaRepository historiaAcademicaRepository;
    private final ObjectMapper objectMapper;
    private final EstadisticasPorCarreraRepository estadisticasPorCarreraRepo;


    @Transactional
    @Override
    public EstadisticasGeneralesDTO obtenerEstadisticasPorCarrera(String codigoPlan, PeriodoEstadisticas periodo) {
        PlanDeEstudio plan = planDeEstudioRepository.findById(codigoPlan)
                .orElseThrow(() -> new EntityNotFoundException("Plan de estudio no encontrado"));

        LocalDate fechaLimite = calcularFechaLimite(periodo);
        List<Materia> materiasCarrera = materiaRepository.findByPlanDeEstudio_Codigo(codigoPlan);
        List<String> codigosMaterias = materiasCarrera.stream().map(Materia::getCodigo).toList();

        List<Examen> examenesFiltrados = filtrarExamenesPorMateriasYFecha(codigosMaterias, fechaLimite, codigoPlan);
        List<HistoriaAcademica> historiasCarrera = filtrarHistoriasPorPlan(codigoPlan);

        EstadisticasGeneralesDTO dto = construirEstadisticasDTO(plan, examenesFiltrados, historiasCarrera, codigosMaterias);

        guardarEstadisticasPorCarrera(codigoPlan, periodo, dto);

        return dto;
    }

    private EstadisticasGeneralesDTO construirEstadisticasDTO(PlanDeEstudio plan, List<Examen> examenes, List<HistoriaAcademica> historias, List<String> codigosMaterias) {

        long totalExamenes = examenes.size();
        long totalAprobados = examenes.stream()
                .filter(examen -> examen.getNota() >= 4)
                .count();

        String materiaMasRendida = obtenerMateriaMasRendida(examenes);
        String materiaMasRendidaNombre = materiaRepository.findFirstNombreByCodigo(materiaMasRendida);
        long cantMateriaMasRendida = contarExamenesPorMateria(examenes, materiaMasRendida);
        long cantAprobadosMateriaMasRendida = contarAprobadosPorMateria(examenes, materiaMasRendida);

        return EstadisticasGeneralesDTO.builder()
                .totalMaterias(codigosMaterias.size())
                .totalExamenesRendidos((int) totalExamenes)
                .materiaMasRendida(estadisticasHelper.calcularMateriaMasRendida(
                        materiaMasRendida, materiaMasRendidaNombre, cantMateriaMasRendida, cantAprobadosMateriaMasRendida))
                .cantidadMateriaMasRendida(cantMateriaMasRendida)
                .porcentajeAprobadosGeneral(estadisticasHelper.calcularPorcentaje(totalAprobados, totalExamenes))
                .top5Aprobadas(calcularTop5Aprobadas(examenes))
                .top5Reprobadas(calcularTop5Reprobadas(examenes))
                .promedioNotasPorMateria(calcularPromediosPorMateria(examenes))
                .estudiantesActivos(contarEstudiantesActivos(historias))
                .promedioGeneral(estadisticasHelper.calcularPromedioNotas(examenes))
                .distribucionExamenesPorMateria(calcularDistribucionExamenesPorMateria(examenes))
                .build();
    }

    @Transactional
    public void guardarEstadisticasPorCarrera(String codigoPlan, PeriodoEstadisticas periodo, EstadisticasGeneralesDTO dto) {
        try {
            EstadisticasPorCarrera stats = EstadisticasPorCarrera.builder()
                    .codigoPlan(codigoPlan)
                    .periodo(periodo.toString())
                    .estudiantesActivos(Math.toIntExact(dto.getEstudiantesActivos()))
                    .totalMaterias(dto.getTotalMaterias())
                    .totalExamenesRendidos(dto.getTotalExamenesRendidos())
                    .porcentajeAprobadosGeneral(dto.getPorcentajeAprobadosGeneral())
                    .promedioGeneral(dto.getPromedioGeneral())
                    .distribucionExamenesPorMateria(objectMapper.writeValueAsString(dto.getDistribucionExamenesPorMateria()))
                    .top5Aprobadas(objectMapper.writeValueAsString(dto.getTop5Aprobadas()))
                    .top5Reprobadas(objectMapper.writeValueAsString(dto.getTop5Reprobadas()))
                    .promedioNotasPorMateria(objectMapper.writeValueAsString(dto.getPromedioNotasPorMateria()))
                    .materiaMasRendida(objectMapper.writeValueAsString(dto.getMateriaMasRendida()))
                    .cantidadMateriaMasRendida(dto.getCantidadMateriaMasRendida())
                    .fechaUltimaActualizacion(LocalDateTime.now())
                    .build();

            estadisticasPorCarreraRepo.deleteByCodigoPlanAndPeriodo(codigoPlan, periodo.toString());
            estadisticasPorCarreraRepo.save(stats);
        } catch (JsonProcessingException e) {
            log.error("Error al serializar estadísticas por carrera", e);
        }
    }


    private long contarEstudiantesActivos(List<HistoriaAcademica> historias) {
        return historias.size();
    }

    private Map<String, Integer> calcularDistribucionExamenesPorMateria(List<Examen> examenes) {
        return examenes.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getRenglon().getMateria().getNombre(),
                        Collectors.summingInt(e -> 1)
                ));
    }

    private Map<String, Double> calcularPromediosPorMateria(List<Examen> examenes) {
        return examenes.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getRenglon().getMateria().getNombre(),
                        Collectors.averagingDouble(Examen::getNota)
                ));
    }

    private List<MateriaRankingDTO> calcularTop5Aprobadas(List<Examen> examenes) {
        Map<String, Long> totalPorMateria = examenes.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getRenglon().getMateria().getCodigo(),
                        Collectors.counting()
                ));

        Map<String, Long> aprobadosPorMateria = examenes.stream()
                .filter(e -> e.getNota() >= 4)
                .collect(Collectors.groupingBy(
                        e -> e.getRenglon().getMateria().getCodigo(),
                        Collectors.counting()
                ));

        return totalPorMateria.entrySet().stream()
                .map(entry -> {
                    String codigo = entry.getKey();
                    String nombre = materiaRepository.findFirstNombreByCodigo(codigo);
                    long total = entry.getValue();
                    long aprobados = aprobadosPorMateria.getOrDefault(codigo, 0L);
                    double porcentaje = estadisticasHelper.calcularPorcentaje(aprobados, total);

                    MateriaRankingDTO dto = new MateriaRankingDTO();
                    dto.setCodigoMateria(codigo);
                    dto.setNombre(nombre);
                    dto.setPorcentaje(porcentaje);
                    return dto;
                })
                .sorted(Comparator.comparingDouble(MateriaRankingDTO::getPorcentaje).reversed())
                .limit(5)
                .collect(Collectors.toList());
    }

    private List<MateriaRankingDTO> calcularTop5Reprobadas(List<Examen> examenes) {
        Map<String, Long> totalPorMateria = examenes.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getRenglon().getMateria().getCodigo(),
                        Collectors.counting()
                ));

        Map<String, Long> reprobadosPorMateria = examenes.stream()
                .filter(e -> e.getNota() < 4)
                .collect(Collectors.groupingBy(
                        e -> e.getRenglon().getMateria().getCodigo(),
                        Collectors.counting()
                ));

        return totalPorMateria.entrySet().stream()
                .map(entry -> {
                    String codigo = entry.getKey();
                    String nombre = materiaRepository.findFirstNombreByCodigo(codigo);
                    long total = entry.getValue();
                    long aprobados = reprobadosPorMateria.getOrDefault(codigo, 0L);
                    double porcentaje = estadisticasHelper.calcularPorcentaje(aprobados, total);

                    MateriaRankingDTO dto = new MateriaRankingDTO();
                    dto.setCodigoMateria(codigo);
                    dto.setNombre(nombre);
                    dto.setPorcentaje(porcentaje);
                    return dto;
                })
                .sorted(Comparator.comparingDouble(MateriaRankingDTO::getPorcentaje).reversed())
                .limit(5)
                .collect(Collectors.toList());
    }

    private String obtenerMateriaMasRendida(List<Examen> examenes) {
        return examenes.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getRenglon().getMateria().getCodigo(),
                        Collectors.counting()
                ))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    private long contarExamenesPorMateria(List<Examen> examenes, String codigoMateria) {
        return examenes.stream()
                .filter(e -> e.getRenglon().getMateria().getCodigo().equals(codigoMateria))
                .count();
    }

    private long contarAprobadosPorMateria(List<Examen> examenes, String codigoMateria) {
        return examenes.stream()
                .filter(e -> e.getRenglon().getMateria().getCodigo().equals(codigoMateria))
                .filter(e -> e.getNota() >= 4)
                .count();
    }

    private List<Examen> filtrarExamenesPorMateriasYFecha(List<String> codigosMaterias, LocalDate fechaLimite, String plan) {
        if (fechaLimite == null) {
            return examenRepository.findByRenglon_Materia_CodigoIn(codigosMaterias, plan);
        }
        return examenRepository.findByRenglon_Materia_CodigoInAndFechaAfter(codigosMaterias, fechaLimite, plan);
    }

    private List<HistoriaAcademica> filtrarHistoriasPorPlan(String codigoPlan) {
        return historiaAcademicaRepository.findByPlanDeEstudio_Codigo(codigoPlan);
    }

    private LocalDate calcularFechaLimite(PeriodoEstadisticas periodo) {
        return switch (periodo) {
            case ULTIMO_ANIO -> LocalDate.now().minusYears(1);
            case ULTIMOS_2_ANIOS -> LocalDate.now().minusYears(2);
            case ULTIMOS_5_ANIOS -> LocalDate.now().minusYears(5);
            case TODOS_LOS_TIEMPOS -> null;
        };
    }
}
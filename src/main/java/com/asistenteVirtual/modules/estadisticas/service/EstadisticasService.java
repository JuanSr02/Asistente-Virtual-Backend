package com.asistenteVirtual.modules.estadisticas.service;

import com.asistenteVirtual.common.utils.JsonConverter;
import com.asistenteVirtual.modules.estadisticas.dto.EstadisticasGeneralesResponse;
import com.asistenteVirtual.modules.estadisticas.dto.EstadisticasMateriaResponse;
import com.asistenteVirtual.modules.estadisticas.dto.MateriaRankingResponse;
import com.asistenteVirtual.modules.estadisticas.model.EstadisticasGenerales;
import com.asistenteVirtual.modules.estadisticas.model.EstadisticasMateria;
import com.asistenteVirtual.modules.estadisticas.model.PeriodoEstadisticas;
import com.asistenteVirtual.modules.estadisticas.repository.EstadisticasGeneralesRepository;
import com.asistenteVirtual.modules.estadisticas.repository.EstadisticasMateriaRepository;
import com.asistenteVirtual.modules.experiencia.model.Experiencia;
import com.asistenteVirtual.modules.experiencia.repository.ExperienciaRepository;
import com.asistenteVirtual.modules.historiaAcademica.model.Examen;
import com.asistenteVirtual.modules.historiaAcademica.model.HistoriaAcademica;
import com.asistenteVirtual.modules.historiaAcademica.repository.ExamenRepository;
import com.asistenteVirtual.modules.historiaAcademica.repository.HistoriaAcademicaRepository;
import com.asistenteVirtual.modules.planEstudio.model.Materia;
import com.asistenteVirtual.modules.planEstudio.model.PlanDeEstudio;
import com.asistenteVirtual.modules.planEstudio.repository.MateriaRepository;
import com.asistenteVirtual.modules.planEstudio.repository.PlanDeEstudioRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EstadisticasService {

    private final ExamenRepository examenRepo;
    private final MateriaRepository materiaRepo;
    private final HistoriaAcademicaRepository historiaRepo;
    private final ExperienciaRepository experienciaRepo;
    private final PlanDeEstudioRepository planRepo;

    private final EstadisticasMateriaRepository statsMateriaRepo;
    private final EstadisticasGeneralesRepository statsGeneralesRepo;

    // Inyectamos el servicio de carreras para reutilizar su lógica de cálculo y guardado
    private final EstadisticasAvanzadasService statsCarreraService;

    private final EstadisticasCalculatorHelper helper;
    private final JsonConverter jsonConverter;

    @Transactional
    public void actualizarTodas() {
        log.info("🔄 Iniciando CRON JOB: Actualización masiva de TODAS las estadísticas...");

        // 0. Borrado de Historias BAJA
        historiaRepo.deleteByEstado("BAJA");

        // 1. Estadísticas Por Materia para CADA Periodo
        log.info("📚 Calculando Estadísticas por Materia y Periodo...");
        List<String> codigosMaterias = examenRepo.findDistinctMateriasPorCodigo();
        for (String codigo : codigosMaterias) {
            for (PeriodoEstadisticas periodo : PeriodoEstadisticas.values()) {
                try {
                    calcularYGuardarMateria(codigo, periodo);
                } catch (Exception e) {
                    log.error("Error actualizando materia {} periodo {}: {}", codigo, periodo, e.getMessage());
                }
            }
        }

        // 2. Estadísticas Por Carrera (Plan) para CADA Periodo
        log.info("🎓 Calculando Estadísticas por Carrera y Periodo...");
        List<PlanDeEstudio> planes = planRepo.findAll();
        for (PlanDeEstudio plan : planes) {
            for (PeriodoEstadisticas periodo : PeriodoEstadisticas.values()) {
                try {
                    // Este servicio ya calcula y guarda en la entidad EstadisticasPorCarrera
                    statsCarreraService.obtenerEstadisticasPorCarrera(plan.getCodigo(), periodo);
                } catch (Exception e) {
                    log.error("Error actualizando carrera {} periodo {}: {}", plan.getCodigo(), periodo, e.getMessage());
                }
            }
        }

        // 3. Estadísticas Generales Globales
        log.info("📊 Calculando Generales Globales...");
        calcularYGuardarGenerales();

        log.info("✅ Actualización masiva finalizada exitosamente.");
    }

    @Transactional
    public EstadisticasMateriaResponse calcularYGuardarMateria(String codigoMateria, PeriodoEstadisticas periodo) {
        // 1. Validar materia
        List<Materia> materias = materiaRepo.findByCodigo(codigoMateria);
        if (materias.isEmpty()) return null;

        // 2. Determinar fecha límite según periodo
        LocalDate fechaLimite = calcularFechaLimite(periodo);

        // 3. Obtener Datos (Examenes y Experiencias) filtrados
        List<Examen> examenes = new ArrayList<>();
        List<Experiencia> experiencias = new ArrayList<>();

        for (Materia materia : materias) {
            if (fechaLimite != null) {
                examenes.addAll(examenRepo.findByMateriaAndFechaAfter(materia, fechaLimite));
                experiencias.addAll(experienciaRepo.findByMateriaAndFechaAfter(materia, fechaLimite));
            } else {
                examenes.addAll(examenRepo.findByMateriaWithJoins(materia));
                experiencias.addAll(experienciaRepo.findByMateriaWithJoins(materia)); // Usamos el nuevo método optimizado
            }
        }

        if (examenes.isEmpty()) return null;

        String nombreMateria = materias.getFirst().getNombre();

        // 4. Construir Entidad
        var stats = EstadisticasMateria.builder()
                .codigoMateria(codigoMateria)
                .periodo(periodo.toString())
                .nombreMateria(nombreMateria)
                .totalRendidos(examenes.size())
                .aprobados(helper.calcularAprobados(examenes))
                .reprobados(examenes.size() - helper.calcularAprobados(examenes))
                .promedioNotas(helper.calcularPromedioNotas(examenes))
                .promedioDiasEstudio(helper.calcularPromedioDiasEstudio(experiencias))
                .promedioHorasDiarias(helper.calcularPromedioHorasDiarias(experiencias))
                .promedioDificultad(helper.calcularPromedioDificultad(experiencias))
                .distribucionDificultad(jsonConverter.toJson(helper.calcularDistribucionDificultadMap(experiencias)))
                .distribucionModalidad(jsonConverter.toJson(helper.calcularDistribucionModalidadMap(experiencias)))
                .distribucionRecursos(jsonConverter.toJson(helper.calcularDistribucionRecursosMap(experiencias)))
                .fechaUltimaActualizacion(LocalDateTime.now())
                .build();

        // 5. Guardar
        statsMateriaRepo.deleteByCodigoMateriaAndPeriodo(codigoMateria, periodo.toString());
        stats = statsMateriaRepo.save(stats);

        return mapMateriaToResponse(stats);
    }

    @Transactional
    public EstadisticasGeneralesResponse calcularYGuardarGenerales() {
        List<Examen> todosExamenes = examenRepo.findAll();
        List<HistoriaAcademica> historias = historiaRepo.findAll();

        var topAprobadas = helper.mapToMateriaRankingResponse(examenRepo.findTop5MateriasAprobadas());
        var topReprobadas = helper.mapToMateriaRankingResponse(examenRepo.findTop5MateriasReprobadas());

        String materiaMasRendida = examenRepo.findCodigoMateriaMasRendida();
        List<Materia> nombresDiferentes = materiaRepo.findByCodigo(materiaMasRendida);
        String materiaMasRendidaNombre = nombresDiferentes.stream()
                .map(Materia::getNombre)              // 1. Transformamos la lista de objetos a lista de Strings (nombres)
                .distinct()                           // 2. Eliminamos nombres repetidos (por si es el mismo en varios planes)
                .collect(Collectors.joining(" - "));  // 3. Unimos con el separador
        long cantMateriaMasRendida = (materiaMasRendida != null) ? examenRepo.countExamenesByCodigoMateria(materiaMasRendida) : 0;
        long cantAprobadosMateriaMasRendida = (materiaMasRendida != null) ? examenRepo.countExamenesAprobadosByCodigoMateria(materiaMasRendida) : 0;

        var stats = EstadisticasGenerales.builder()
                .totalMaterias((int) materiaRepo.count())
                .totalExamenesRendidos(todosExamenes.size())
                .estudiantesActivos((long) historias.size())
                .promedioGeneral(helper.calcularPromedioNotas(todosExamenes))
                .porcentajeAprobadosGeneral(helper.calcularPorcentajeGlobal(todosExamenes))
                .distribucionEstudiantesPorCarrera(jsonConverter.toJson(helper.calcularDistribucionEstudiantesPorCarrera(historias)))
                .distribucionExamenesPorMateria(jsonConverter.toJson(helper.calcularDistribucionExamenesPorMateria(todosExamenes)))
                .top5Aprobadas(jsonConverter.toJson(topAprobadas))
                .top5Reprobadas(jsonConverter.toJson(topReprobadas))
                .promedioNotasPorMateria(jsonConverter.toJson(helper.obtenerPromediosPorMateria(statsMateriaRepo.findByPeriodo("TODOS_LOS_TIEMPOS"))))
                .materiaMasRendida(jsonConverter.toJson(
                        helper.crearRankingDTO(materiaMasRendida, materiaMasRendidaNombre, cantMateriaMasRendida, cantAprobadosMateriaMasRendida)
                ))
                .cantidadMateriaMasRendida(cantMateriaMasRendida)
                .fechaUltimaActualizacion(LocalDateTime.now())
                .build();

        statsGeneralesRepo.deleteAll();
        stats = statsGeneralesRepo.save(stats);
        return mapGeneralToResponse(stats);
    }

    // --- Helpers Privados ---

    private LocalDate calcularFechaLimite(PeriodoEstadisticas periodo) {
        return switch (periodo) {
            case ULTIMO_ANIO -> LocalDate.now().minusYears(1);
            case ULTIMOS_2_ANIOS -> LocalDate.now().minusYears(2);
            case ULTIMOS_5_ANIOS -> LocalDate.now().minusYears(5);
            case TODOS_LOS_TIEMPOS -> null;
        };
    }

    private EstadisticasMateriaResponse mapMateriaToResponse(EstadisticasMateria stats) {
        return new EstadisticasMateriaResponse(
                stats.getCodigoMateria(),
                stats.getNombreMateria(),
                stats.getTotalRendidos(),
                stats.getAprobados(),
                stats.getReprobados(),
                helper.calcularPorcentaje(stats.getAprobados(), stats.getTotalRendidos()),
                stats.getPromedioNotas(),
                stats.getPromedioDiasEstudio(),
                stats.getPromedioHorasDiarias(),
                stats.getPromedioDificultad(),
                jsonConverter.fromJson(stats.getDistribucionDificultad(), new TypeReference<Map<Integer, Integer>>() {
                }),
                jsonConverter.fromJson(stats.getDistribucionModalidad(), new TypeReference<Map<String, Integer>>() {
                }),
                jsonConverter.fromJson(stats.getDistribucionRecursos(), new TypeReference<Map<String, Integer>>() {
                }),
                stats.getFechaUltimaActualizacion().toLocalDate()
        );
    }

    private EstadisticasGeneralesResponse mapGeneralToResponse(EstadisticasGenerales stats) {
        return new EstadisticasGeneralesResponse(
                stats.getEstudiantesActivos(),
                stats.getTotalMaterias(),
                stats.getTotalExamenesRendidos(),
                stats.getPorcentajeAprobadosGeneral(),
                stats.getPromedioGeneral(),
                jsonConverter.fromJson(stats.getDistribucionEstudiantesPorCarrera(), new TypeReference<Map<String, Integer>>() {
                }),
                jsonConverter.fromJson(stats.getDistribucionExamenesPorMateria(), new TypeReference<Map<String, Integer>>() {
                }),
                jsonConverter.fromJson(stats.getMateriaMasRendida(), MateriaRankingResponse.class),
                stats.getCantidadMateriaMasRendida(),
                jsonConverter.fromJson(stats.getTop5Aprobadas(), new TypeReference<List<MateriaRankingResponse>>() {
                }),
                jsonConverter.fromJson(stats.getTop5Reprobadas(), new TypeReference<List<MateriaRankingResponse>>() {
                }),
                jsonConverter.fromJson(stats.getPromedioNotasPorMateria(), new TypeReference<Map<String, Double>>() {
                })
        );
    }
}
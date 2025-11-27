package com.asistenteVirtual.modules.estadisticas.service;

import com.asistenteVirtual.common.utils.JsonConverter;
import com.asistenteVirtual.modules.estadisticas.dto.EstadisticasGeneralesResponse;
import com.asistenteVirtual.modules.estadisticas.dto.EstadisticasMateriaResponse;
import com.asistenteVirtual.modules.estadisticas.dto.MateriaRankingResponse;
import com.asistenteVirtual.modules.estadisticas.model.EstadisticasGenerales;
import com.asistenteVirtual.modules.estadisticas.model.EstadisticasMateria;
import com.asistenteVirtual.modules.estadisticas.repository.EstadisticasGeneralesRepository;
import com.asistenteVirtual.modules.estadisticas.repository.EstadisticasMateriaRepository;
import com.asistenteVirtual.modules.experiencia.model.Experiencia;
import com.asistenteVirtual.modules.experiencia.repository.ExperienciaRepository;
import com.asistenteVirtual.modules.historiaAcademica.model.Examen;
import com.asistenteVirtual.modules.historiaAcademica.model.HistoriaAcademica;
import com.asistenteVirtual.modules.historiaAcademica.repository.ExamenRepository;
import com.asistenteVirtual.modules.historiaAcademica.repository.HistoriaAcademicaRepository;
import com.asistenteVirtual.modules.planEstudio.model.Materia;
import com.asistenteVirtual.modules.planEstudio.repository.MateriaRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EstadisticasService {

    private final ExamenRepository examenRepo;
    private final MateriaRepository materiaRepo;
    private final HistoriaAcademicaRepository historiaRepo;
    private final ExperienciaRepository experienciaRepo;

    private final EstadisticasMateriaRepository statsMateriaRepo;
    private final EstadisticasGeneralesRepository statsGeneralesRepo;

    private final EstadisticasCalculatorHelper helper;
    private final JsonConverter jsonConverter;

    @Transactional
    public void actualizarTodas() {
        log.info("🔄 Iniciando actualización masiva de estadísticas...");
        List<String> codigosMaterias = examenRepo.findDistinctMateriasPorCodigo();

        for (String codigo : codigosMaterias) {
            calcularYGuardarMateria(codigo);
        }
        calcularYGuardarGenerales();
        log.info("✅ Actualización de estadísticas finalizada.");
    }

    @Transactional
    public EstadisticasMateriaResponse calcularYGuardarMateria(String codigoMateria) {
        List<Materia> materias = materiaRepo.findByCodigo(codigoMateria);
        if (materias.isEmpty()) return null;

        List<Examen> examenes = new ArrayList<>();
        for (Materia materia : materias) {
            examenes.addAll(examenRepo.findByMateriaWithJoins(materia));
        }

        List<Experiencia> experiencias = experienciaRepo.findAllByCodigoMateria(codigoMateria);
        String nombreMateria = materias.getFirst().getNombre();

        var stats = EstadisticasMateria.builder()
                .codigoMateria(codigoMateria)
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

        stats = statsMateriaRepo.save(stats);

        // ✅ Retornamos el DTO mapeado directamente para uso inmediato
        return mapMateriaToResponse(stats);
    }

    @Transactional
    public EstadisticasGeneralesResponse calcularYGuardarGenerales() {
        List<Examen> todosExamenes = examenRepo.findAll();
        List<HistoriaAcademica> historias = historiaRepo.findAll();

        var topAprobadas = helper.mapToMateriaRankingResponse(examenRepo.findTop5MateriasAprobadas());
        var topReprobadas = helper.mapToMateriaRankingResponse(examenRepo.findTop5MateriasReprobadas());

        String materiaMasRendida = examenRepo.findCodigoMateriaMasRendida();
        String materiaMasRendidaNombre = materiaRepo.findFirstNombreByCodigo(materiaMasRendida);
        long cantMateriaMasRendida = examenRepo.countExamenesByCodigoMateria(materiaMasRendida);
        long cantAprobadosMateriaMasRendida = examenRepo.countExamenesAprobadosByCodigoMateria(materiaMasRendida);

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
                .promedioNotasPorMateria(jsonConverter.toJson(helper.obtenerPromediosPorMateria(statsMateriaRepo.findAll())))
                .materiaMasRendida(jsonConverter.toJson(
                        helper.crearRankingDTO(materiaMasRendida, materiaMasRendidaNombre, cantMateriaMasRendida, cantAprobadosMateriaMasRendida)
                ))
                .cantidadMateriaMasRendida(cantMateriaMasRendida)
                .fechaUltimaActualizacion(LocalDateTime.now())
                .build();

        stats = statsGeneralesRepo.save(stats);

        // ✅ Retornamos el DTO mapeado
        return mapGeneralToResponse(stats);
    }

    // --- Mappers Internos (Replica la lógica de FastStatisticsService para mantener independencia) ---

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
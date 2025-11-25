package com.asistenteVirtual.modules.estadisticas.service;

import com.asistenteVirtual.common.utils.JsonConverter;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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

    /**
     * Recalcula TODAS las estadísticas del sistema.
     * Ideal para ejecutar en un Cron Job nocturno.
     */
    @Transactional
    public void actualizarTodas() {
        log.info("🔄 Iniciando actualización masiva de estadísticas...");

        // 1. Actualizar por Materia
        List<String> codigosMaterias = examenRepo.findDistinctMateriasPorCodigo();
        log.info("Se procesarán {} materias.", codigosMaterias.size());

        for (String codigo : codigosMaterias) {
            calcularYGuardarMateria(codigo);
        }

        // 2. Actualizar Generales
        calcularYGuardarGenerales();

        log.info("✅ Actualización de estadísticas finalizada.");
    }

    @Transactional
    public void calcularYGuardarMateria(String codigoMateria) {
        List<Materia> materias = materiaRepo.findByCodigo(codigoMateria);
        if (materias.isEmpty()) return;

        // Buscamos todos los exámenes y experiencias de esta materia (en cualquier plan)
        // Nota: Esto asume que tu repositorio soporta búsqueda polimórfica o que iteramos.
        // Para optimizar, usamos la primera materia para buscar relaciones si el código es único conceptualmente.
        Materia materiaPrincipal = materias.get(0);

        List<Examen> examenes = examenRepo.findByMateriaWithJoins(materiaPrincipal);
        List<Experiencia> experiencias = experienciaRepo.findByMateriaWithJoins(materiaPrincipal);

        EstadisticasMateria stats = EstadisticasMateria.builder()
                .codigoMateria(codigoMateria)
                .nombreMateria(materiaPrincipal.getNombre())
                .totalRendidos(examenes.size())
                .aprobados(helper.calcularAprobados(examenes))
                .reprobados(examenes.size() - helper.calcularAprobados(examenes))
                .promedioNotas(helper.calcularPromedioNotas(examenes))
                .promedioDiasEstudio(helper.calcularPromedioDiasEstudio(experiencias))
                .promedioHorasDiarias(helper.calcularPromedioHorasDiarias(experiencias))
                .promedioDificultad(helper.calcularPromedioDificultad(experiencias))
                // Serialización automática con JsonConverter
                .distribucionDificultad(jsonConverter.toJson(helper.calcularDistribucionDificultadMap(experiencias)))
                .distribucionModalidad(jsonConverter.toJson(helper.calcularDistribucionModalidadMap(experiencias)))
                .distribucionRecursos(jsonConverter.toJson(helper.calcularDistribucionRecursosMap(experiencias)))
                .fechaUltimaActualizacion(LocalDateTime.now())
                .build();

        statsMateriaRepo.save(stats);
    }

    @Transactional
    public void calcularYGuardarGenerales() {
        List<Examen> todosExamenes = examenRepo.findAll();
        List<HistoriaAcademica> historias = historiaRepo.findAll();

        // Consultas optimizadas para rankings
        var topAprobadas = helper.mapToMateriaRankingResponse(examenRepo.findTop5MateriasAprobadas());
        var topReprobadas = helper.mapToMateriaRankingResponse(examenRepo.findTop5MateriasReprobadas());

        String materiaMasRendida = examenRepo.findCodigoMateriaMasRendida();
        String materiaMasRendidaNombre = materiaRepo.findFirstNombreByCodigo(materiaMasRendida);
        long cantMateriaMasRendida = examenRepo.countExamenesByCodigoMateria(materiaMasRendida);
        long cantAprobadosMateriaMasRendida = examenRepo.countExamenesAprobadosByCodigoMateria(materiaMasRendida);

        EstadisticasGenerales stats = EstadisticasGenerales.builder()
                .totalMaterias((int) materiaRepo.count())
                .totalExamenesRendidos(todosExamenes.size())
                .estudiantesActivos((long) historias.size())
                .promedioGeneral(helper.calcularPromedioNotas(todosExamenes))
                .porcentajeAprobadosGeneral(helper.calcularPorcentajeGlobal(todosExamenes))
                // JSONs
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

        statsGeneralesRepo.save(stats);
    }
}
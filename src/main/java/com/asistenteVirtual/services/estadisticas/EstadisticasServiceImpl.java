package com.asistenteVirtual.services.estadisticas;

import com.asistenteVirtual.DTOs.EstadisticasGeneralesDTO;
import com.asistenteVirtual.DTOs.EstadisticasMateriaDTO;
import com.asistenteVirtual.exceptions.ResourceNotFoundException;
import com.asistenteVirtual.model.*;
import com.asistenteVirtual.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EstadisticasServiceImpl implements EstadisticasCalculator, EstadisticasGeneralCalculator, EstadisticasMapper {
    private final ExperienciaRepository experienciaRepo;
    private final ExamenRepository examenRepo;
    private final EstadisticasMateriaRepository estadisticasRepo;
    private final MateriaRepository materiaRepo;
    private final EstadisticasHelper estadisticasHelper;
    private final PlanDeEstudioRepository planDeEstudioRepository;
    private final HistoriaAcademicaRepository historiaRepo;
    private final ObjectMapper objectMapper;
    private final EstadisticasGeneralesRepository estadisticasGeneralesRepo;


    public void actualizarEstadisticas() {
        List<String> codigoMaterias = examenRepo.findDistinctMateriasPorCodigo();
        List<Materia> materias = new ArrayList<>();
        for (String cod : codigoMaterias) {
            materias.add(materiaRepo.findFirstByCodigo(cod).orElseThrow());
        }
        materias.forEach(this::calcularYGuardarEstadisticas);
    }


    @Override
    public EstadisticasMateria obtenerEstadisticasMateriaUnica(String codigoMateria, String codigoPlan) {
        PlanDeEstudio plan = planDeEstudioRepository.findById(codigoPlan).orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado"));
        Materia materia = materiaRepo.findByCodigoAndPlanDeEstudio(codigoMateria, plan)
                .orElseThrow(() -> new ResourceNotFoundException("La materia de la que se quiere obtener estadisticas no existe."));
        return calcularEstadisticas(materia);
    }

    @Override
    public EstadisticasMateriaDTO obtenerEstadisticasSuperMateria(String codigoMateria) {
        actualizarEstadisticas();
        return convertToDTO(obtenerEstadisticasMateria(codigoMateria));
    }

    public EstadisticasMateria obtenerEstadisticasMateria(String codigoMateria) {
        // Modo agrupado por código
        List<Materia> materiasConMismoCodigo = materiaRepo.findByCodigo(codigoMateria);

        if (materiasConMismoCodigo == null || materiasConMismoCodigo.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron materias con el código: " + codigoMateria);
        }

        if (materiasConMismoCodigo.size() == 1) {
            return obtenerEstadisticasMateriaUnica(codigoMateria, materiasConMismoCodigo.getFirst().getPlanDeEstudio().getCodigo());
        }

        List<Examen> todosLosExamenes = materiasConMismoCodigo.stream()
                .flatMap(m -> m.getRenglones().stream())
                .map(Renglon::getExamen)
                .filter(e -> e != null)
                .toList();

        List<Experiencia> todasLasExperiencias = todosLosExamenes.stream()
                .map(Examen::getExperiencia)
                .filter(e -> e != null)
                .toList();

        int totalRendidos = todosLosExamenes.size();
        int aprobados = estadisticasHelper.calcularAprobados(todosLosExamenes);
        double promedio = estadisticasHelper.calcularPromedioNotas(todosLosExamenes);
        double promedioDias = estadisticasHelper.calcularPromedioDiasEstudio(todasLasExperiencias);
        double promedioHoras = estadisticasHelper.calcularPromedioHorasDiarias(todasLasExperiencias);
        double promedioDificultad = estadisticasHelper.calcularPromedioDificultad(todasLasExperiencias);

        EstadisticasMateria stats = new EstadisticasMateria();
        stats.setCodigoMateria(codigoMateria);
        stats.setNombreMateria(materiasConMismoCodigo.getFirst().getNombre());
        stats.setTotalRendidos(totalRendidos);
        stats.setAprobados(aprobados);
        stats.setReprobados(totalRendidos - aprobados);
        stats.setPromedioNotas(promedio);
        stats.setPromedioDiasEstudio(promedioDias);
        stats.setPromedioHorasDiarias(promedioHoras);
        stats.setPromedioDificultad(promedioDificultad);
        stats.setDistribucionDificultad(estadisticasHelper.calcularDistribucionDificultad(todasLasExperiencias));
        stats.setDistribucionModalidad(estadisticasHelper.calcularDistribucionModalidad(todasLasExperiencias));
        stats.setDistribucionRecursos(estadisticasHelper.calcularDistribucionRecursos(todasLasExperiencias));
        stats.setFechaUltimaActualizacion(LocalDateTime.now());

        return stats;
    }


    public void guardarEstadisticasGenerales(EstadisticasGeneralesDTO dto) {
        try {
            EstadisticasGenerales stats = EstadisticasGenerales.builder()
                    .estudiantesActivos(dto.getEstudiantesActivos())
                    .totalMaterias(dto.getTotalMaterias())
                    .totalExamenesRendidos(dto.getTotalExamenesRendidos())
                    .porcentajeAprobadosGeneral(dto.getPorcentajeAprobadosGeneral())
                    .promedioGeneral(dto.getPromedioGeneral())
                    .distribucionEstudiantesPorCarrera(objectMapper.writeValueAsString(dto.getDistribucionEstudiantesPorCarrera()))
                    .distribucionExamenesPorMateria(objectMapper.writeValueAsString(dto.getDistribucionExamenesPorMateria()))
                    .top5Aprobadas(objectMapper.writeValueAsString(dto.getTop5Aprobadas()))
                    .top5Reprobadas(objectMapper.writeValueAsString(dto.getTop5Reprobadas()))
                    .promedioNotasPorMateria(objectMapper.writeValueAsString(dto.getPromedioNotasPorMateria()))
                    .materiaMasRendida(objectMapper.writeValueAsString(dto.getMateriaMasRendida()))
                    .cantidadMateriaMasRendida(dto.getCantidadMateriaMasRendida())
                    .fechaUltimaActualizacion(LocalDateTime.now())
                    .build();

            estadisticasGeneralesRepo.save(stats);
        } catch (JsonProcessingException e) {
            log.error("Error al serializar estadísticas generales", e);
        }
    }


    public EstadisticasMateria calcularEstadisticas(Materia materia) {
        List<Examen> examenes = examenRepo.findByMateriaWithJoins(materia);
        List<Experiencia> experiencias = experienciaRepo.findByMateriaWithJoins(materia);

        return EstadisticasMateria.builder()
                .codigoMateria(materia.getCodigo())
                .nombreMateria(materia.getNombre())
                .totalRendidos(examenes.size())
                .aprobados(estadisticasHelper.calcularAprobados(examenes))
                .reprobados(examenes.size() - estadisticasHelper.calcularAprobados(examenes))
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

    @Override
    public EstadisticasGeneralesDTO obtenerEstadisticasGenerales() {
        EstadisticasGeneralesDTO dto = calcularEstadisticasGenerales();
        guardarEstadisticasGenerales(dto);
        return dto;
    }

    @Override
    public EstadisticasGeneralesDTO calcularEstadisticasGenerales() {
        actualizarEstadisticas();
        List<Object[]> topAprobadas = examenRepo.findTop5MateriasAprobadas();
        List<Object[]> topReprobadas = examenRepo.findTop5MateriasReprobadas();

        long totalExamenes = examenRepo.count();
        long totalAprobados = examenRepo.countByNotaGreaterThanEqual(4.0);
        List<Examen> todosLosExamenes = examenRepo.findAll();

        String materiaMasRendida = examenRepo.findCodigoMateriaMasRendida();
        String materiaMasRendidaNombre = materiaRepo.findFirstNombreByCodigo(materiaMasRendida);
        long cantMateriaMasRendida = examenRepo.countExamenesByCodigoMateria(materiaMasRendida);
        long cantAprobadosMateriaMasRendida = examenRepo.countExamenesAprobadosByCodigoMateria(materiaMasRendida);

        List<HistoriaAcademica> historias = historiaRepo.findAll(); // Asegurate de tener este repo
        Map<String, Integer> distEstudiantes = estadisticasHelper.calcularDistribucionEstudiantesPorCarrera(historias);
        Map<String, Integer> distExamenes = estadisticasHelper.calcularDistribucionExamenesPorMateria(todosLosExamenes);


        return EstadisticasGeneralesDTO.builder()
                .totalMaterias((int) materiaRepo.count())
                .totalExamenesRendidos((int) totalExamenes)
                .materiaMasRendida(estadisticasHelper.calcularMateriaMasRendida(materiaMasRendida, materiaMasRendidaNombre, cantMateriaMasRendida, cantAprobadosMateriaMasRendida))
                .cantidadMateriaMasRendida(cantMateriaMasRendida)
                .porcentajeAprobadosGeneral(estadisticasHelper.calcularPorcentaje(totalAprobados, totalExamenes))
                .top5Aprobadas(estadisticasHelper.mapToMateriaRankingDTO(topAprobadas))
                .top5Reprobadas(estadisticasHelper.mapToMateriaRankingDTO(topReprobadas))
                .promedioNotasPorMateria(estadisticasHelper.obtenerPromediosPorMateria())
                .estudiantesActivos(estadisticasHelper.calcularEstudiantes())
                .promedioGeneral(estadisticasHelper.calcularPromedioNotas(todosLosExamenes))
                .distribucionEstudiantesPorCarrera(distEstudiantes)
                .distribucionExamenesPorMateria(distExamenes)
                .build();
    }

    @Override
    public EstadisticasMateriaDTO convertToDTO(EstadisticasMateria stats) {
        if (stats == null) {
            throw new IllegalArgumentException("Las estadísticas no pueden ser nulas");
        }

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
        dto.setPorcentajeAprobados(estadisticasHelper.calcularPorcentajeAprobados(stats));

        try {
            estadisticasHelper.cargarDistribuciones(dto, stats);
        } catch (JsonProcessingException ignored) {
        }

        if (stats.getFechaUltimaActualizacion() != null) {
            dto.setFechaUltimaActualizacion(stats.getFechaUltimaActualizacion().toLocalDate());
        }

        return dto;
    }

    private void calcularYGuardarEstadisticas(Materia materia) {
        EstadisticasMateria stats = obtenerEstadisticasMateria(materia.getCodigo());
        estadisticasRepo.save(stats);
    }

}
package com.asistenteVirtual.modules.estadisticas.service;

import com.asistenteVirtual.common.exceptions.ResourceNotFoundException;
import com.asistenteVirtual.modules.estadisticas.dto.EstadisticasMateriaResponse;
import com.asistenteVirtual.modules.estadisticas.model.PeriodoEstadisticas;
import com.asistenteVirtual.modules.experiencia.model.Experiencia;
import com.asistenteVirtual.modules.experiencia.repository.ExperienciaRepository;
import com.asistenteVirtual.modules.historiaAcademica.model.Examen;
import com.asistenteVirtual.modules.historiaAcademica.repository.ExamenRepository;
import com.asistenteVirtual.modules.planEstudio.model.Materia;
import com.asistenteVirtual.modules.planEstudio.repository.MateriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EstadisticasMateriaPeriodoService {

    private final ExamenRepository examenRepository;
    private final MateriaRepository materiaRepository;
    private final ExperienciaRepository experienciaRepository;
    private final EstadisticasCalculatorHelper helper;

    @Transactional(readOnly = true)
    public EstadisticasMateriaResponse obtenerEstadisticasPorPeriodo(String codigoMateria, PeriodoEstadisticas periodo) {
        LocalDate fechaLimite = calcularFechaLimite(periodo);
        List<Materia> materias = materiaRepository.findByCodigo(codigoMateria);

        if (materias.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron materias con el código: " + codigoMateria);
        }

        // Obtenemos datos filtrados
        List<Examen> examenes = filtrarExamenes(materias, fechaLimite);
        List<Experiencia> experiencias = filtrarExperiencias(materias, fechaLimite);

        // Calculamos métricas en memoria usando el Helper
        return construirResponse(codigoMateria, materias.get(0).getNombre(), examenes, experiencias);
    }

    private List<Examen> filtrarExamenes(List<Materia> materias, LocalDate fechaLimite) {
        List<Examen> resultados = new ArrayList<>();
        for (Materia m : materias) {
            if (fechaLimite != null) {
                resultados.addAll(examenRepository.findByMateriaAndFechaAfter(m, fechaLimite));
            } else {
                resultados.addAll(examenRepository.findByMateriaWithJoins(m));
            }
        }
        return resultados;
    }

    private List<Experiencia> filtrarExperiencias(List<Materia> materias, LocalDate fechaLimite) {
        List<Experiencia> resultados = new ArrayList<>();
        for (Materia m : materias) {
            if (fechaLimite != null) {
                resultados.addAll(experienciaRepository.findByMateriaAndFechaAfter(m, fechaLimite));
            } else {
                resultados.addAll(experienciaRepository.findAllByCodigoMateria(m.getCodigo()));
            }
        }
        return resultados;
    }

    private EstadisticasMateriaResponse construirResponse(String codigo, String nombre, List<Examen> examenes, List<Experiencia> experiencias) {
        int total = examenes.size();
        int aprobados = helper.calcularAprobados(examenes);
        int reprobados = total - aprobados;

        // Conversión de Map<Tipo, Long> a Map<Tipo, Integer> para el DTO
        Map<Integer, Integer> distDificultad = mapValuesToInteger(helper.calcularDistribucionDificultadMap(experiencias));
        Map<String, Integer> distModalidad = mapValuesToInteger(helper.calcularDistribucionModalidadMap(experiencias));
        Map<String, Integer> distRecursos = mapValuesToInteger(helper.calcularDistribucionRecursosMap(experiencias));

        return new EstadisticasMateriaResponse(
                codigo,
                nombre,
                total,
                aprobados,
                reprobados,
                helper.calcularPorcentaje(aprobados, total),
                helper.calcularPromedioNotas(examenes),
                helper.calcularPromedioDiasEstudio(experiencias),
                helper.calcularPromedioHorasDiarias(experiencias),
                helper.calcularPromedioDificultad(experiencias),
                distDificultad,
                distModalidad,
                distRecursos,
                LocalDate.now()
        );
    }

    private <K> Map<K, Integer> mapValuesToInteger(Map<K, Long> map) {
        return map.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().intValue()));
    }

    private LocalDate calcularFechaLimite(PeriodoEstadisticas periodo) {
        if (periodo == null) return null;
        return switch (periodo) {
            case ULTIMO_ANIO -> LocalDate.now().minusYears(1);
            case ULTIMOS_2_ANIOS -> LocalDate.now().minusYears(2);
            case ULTIMOS_5_ANIOS -> LocalDate.now().minusYears(5);
            case TODOS_LOS_TIEMPOS -> null;
        };
    }
}
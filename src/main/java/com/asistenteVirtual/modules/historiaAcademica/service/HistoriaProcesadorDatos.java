package com.asistenteVirtual.modules.historiaAcademica.service;

import com.asistenteVirtual.common.exceptions.PlanIncompatibleException;
import com.asistenteVirtual.modules.historiaAcademica.dto.DatosFila;
import com.asistenteVirtual.modules.historiaAcademica.model.Examen;
import com.asistenteVirtual.modules.historiaAcademica.model.HistoriaAcademica;
import com.asistenteVirtual.modules.historiaAcademica.model.Renglon;
import com.asistenteVirtual.modules.planEstudio.model.Materia;
import com.asistenteVirtual.modules.planEstudio.model.PlanDeEstudio;
import com.asistenteVirtual.modules.planEstudio.repository.MateriaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistoriaProcesadorDatos {

    private final MateriaRepository materiaRepository;
    private static final double UMBRAL_COINCIDENCIA_PLAN = 80.0;

    @Transactional
    public void procesarDatos(HistoriaAcademica historia, List<DatosFila> filasCrudas, PlanDeEstudio plan) {
        // 1. Obtener mapa de materias (O(1))
        Map<String, Materia> mapaMaterias = materiaRepository.findByPlanDeEstudio_Codigo(plan.getCodigo())
                .stream()
                .collect(Collectors.toMap(Materia::getNombre, Function.identity()));

        // 2. Validación Heurística
        validarCoincidenciaDelPlan(filasCrudas, mapaMaterias);

        // 3. Transformación y Lógica de Negocio
        List<Renglon> nuevosRenglones = transformarYFiltrar(filasCrudas, mapaMaterias, historia);

        // 4. Actualización Inteligente (Merge)
        mergeRenglones(historia, nuevosRenglones);

        // 5. LIMPIEZA FINAL (Regla de Negocio Crítica):
        // Las promociones son instrumentales, no se persisten. Se borran todas (nuevas y viejas).
        eliminarPromociones(historia);
    }

    private void validarCoincidenciaDelPlan(List<DatosFila> filas, Map<String, Materia> materiasDelPlan) {
        if (filas.isEmpty()) return;

        long coincidencias = filas.stream()
                .filter(fila -> materiasDelPlan.containsKey(fila.nombreMateria()))
                .count();

        double porcentaje = (double) coincidencias / filas.size() * 100.0;

        if (porcentaje < UMBRAL_COINCIDENCIA_PLAN) {
            throw new PlanIncompatibleException(String.format(
                    "El archivo no parece corresponder al plan seleccionado. Solo el %.1f%% de las materias coinciden (mínimo requerido: %.0f%%).",
                    porcentaje, UMBRAL_COINCIDENCIA_PLAN
            ));
        }
    }

    private List<Renglon> transformarYFiltrar(List<DatosFila> filas, Map<String, Materia> mapaMaterias, HistoriaAcademica historia) {
        List<Renglon> listaProcesada = new ArrayList<>();

        for (DatosFila fila : filas) {
            if (debeOmitirFila(fila)) continue;

            Materia materia = mapaMaterias.get(fila.nombreMateria());
            if (materia == null) continue;

            procesarFilaSegunTipo(fila, materia, historia, listaProcesada);
        }

        return eliminarDuplicadosLogicos(listaProcesada);
    }

    private boolean debeOmitirFila(DatosFila fila) {
        if ("En curso".equalsIgnoreCase(fila.tipo())) return true;
        if ("Ausente".equalsIgnoreCase(fila.resultado())) return true;
        // Ignorar regularidades reprobadas
        return "Regularidad".equalsIgnoreCase(fila.tipo()) && "Reprobado".equalsIgnoreCase(fila.resultado());
    }

    private void procesarFilaSegunTipo(DatosFila fila, Materia materia, HistoriaAcademica historia, List<Renglon> acumulador) {
        Renglon renglon = Renglon.builder()
                .fecha(fila.fecha())
                .tipo(fila.tipo())
                .nota(fila.nota())
                .resultado(fila.resultado())
                .materia(materia)
                .historiaAcademica(historia)
                .build();

        String tipoNormalizado = fila.tipo().toLowerCase();

        switch (tipoNormalizado) {
            case "examen":
            case "equivalencia": // Equivalencia con nota = Examen
                if (fila.nota() != null) {
                    Examen examen = Examen.builder()
                            .fecha(fila.fecha())
                            .nota(fila.nota())
                            .renglon(renglon)
                            .build();
                    renglon.setExamen(examen);

                    // Si aprobó examen, borramos regularidades (del acumulador y de la DB histórica)
                    if (fila.nota() >= 4.0) {
                        eliminarRegularidadesDeLaMateria(acumulador, materia);
                        eliminarRegularidadesDeLaMateria(historia.getRenglones(), materia);
                    }
                }
                acumulador.add(renglon);
                break;

            case "promocion":
            case "aprobres":
                renglon.setTipo("Promocion");
                // Regla: Promoción mata regularidad (del acumulador y de la DB histórica)
                eliminarRegularidadesDeLaMateria(acumulador, materia);
                eliminarRegularidadesDeLaMateria(historia.getRenglones(), materia);

                acumulador.add(renglon); // Se agrega temporalmente para procesar, se borrará en el paso 5.
                break;

            case "regularidad":
                // Solo agregamos si NO existe ya una aprobación (examen o promo)
                // Verificamos tanto en lo nuevo (acumulador) como en lo viejo (historia)
                if (!tieneAprobacionRegistrada(acumulador, materia) &&
                        !tieneAprobacionRegistrada(historia.getRenglones(), materia)) {
                    acumulador.add(renglon);
                }
                break;
        }
    }

    private void mergeRenglones(HistoriaAcademica historia, List<Renglon> nuevos) {
        List<Renglon> actuales = historia.getRenglones();

        for (Renglon nuevo : nuevos) {
            boolean existe = actuales.stream().anyMatch(actual ->
                    actual.getMateria().getCodigo().equals(nuevo.getMateria().getCodigo()) &&
                            actual.getFecha().equals(nuevo.getFecha()) &&
                            actual.getTipo().equalsIgnoreCase(nuevo.getTipo())
            );

            if (!existe) {
                historia.agregarRenglon(nuevo);
            }
        }
    }

    // --- Helpers de Lógica de Negocio ---

    /**
     * Elimina las regularidades de una lista (ya sea la nueva o la persistente).
     * Al actuar sobre historia.getRenglones(), Hibernate agendará el DELETE gracias a orphanRemoval.
     */
    private void eliminarRegularidadesDeLaMateria(List<Renglon> lista, Materia materia) {
        lista.removeIf(r ->
                r.getMateria().equals(materia) && "Regularidad".equalsIgnoreCase(r.getTipo())
        );
    }

    /**
     * Elimina todas las promociones de la historia antes de guardar.
     * Cumple con la regla de que las promociones no se persisten.
     */
    private void eliminarPromociones(HistoriaAcademica historia) {
        historia.getRenglones().removeIf(r -> "Promocion".equalsIgnoreCase(r.getTipo()));
    }

    private boolean tieneAprobacionRegistrada(List<Renglon> lista, Materia materia) {
        return lista.stream().anyMatch(r ->
                r.getMateria().equals(materia) &&
                        (
                                "Promocion".equalsIgnoreCase(r.getTipo()) ||
                                        (r.getNota() != null && r.getNota() >= 4.0)
                        )
        );
    }

    private List<Renglon> eliminarDuplicadosLogicos(List<Renglon> renglones) {
        Set<String> vistos = new HashSet<>();
        List<Renglon> unicos = new ArrayList<>();

        for (Renglon r : renglones) {
            String key = r.getMateria().getCodigo() + "|" + r.getFecha() + "|" + r.getTipo();
            if (vistos.add(key)) {
                unicos.add(r);
            }
        }
        return unicos;
    }
}
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
        // 1. Obtener todas las materias del plan para búsqueda rápida (O(1))
        Map<String, Materia> mapaMaterias = materiaRepository.findByPlanDeEstudio_Codigo(plan.getCodigo())
                .stream()
                .collect(Collectors.toMap(Materia::getNombre, Function.identity())); // Clave: Nombre, Valor: Materia

        // 2. Validación Heurística: ¿Es este el plan correcto?
        validarCoincidenciaDelPlan(filasCrudas, mapaMaterias);

        // 3. Filtrado y Transformación: De DatosFila a Renglon
        List<Renglon> nuevosRenglones = transformarYFiltrar(filasCrudas, mapaMaterias, historia);

        // 4. Actualización Inteligente: Merge con lo existente
        mergeRenglones(historia, nuevosRenglones);
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
            if (materia == null) continue; // Ignoramos materias ajenas al plan

            procesarFilaSegunTipo(fila, materia, historia, listaProcesada);
        }
        
        // Limpieza final de duplicados en la lista procesada
        return eliminarDuplicadosLogicos(listaProcesada);
    }

    private boolean debeOmitirFila(DatosFila fila) {
        // Regla: Ignorar "En curso", "Reprobados" en regularidades, o "Ausentes"
        if ("En curso".equalsIgnoreCase(fila.tipo())) return true;
        if ("Ausente".equalsIgnoreCase(fila.resultado())) return true;
        
        boolean esRegularidad = "Regularidad".equalsIgnoreCase(fila.tipo());
        boolean esReprobado = "Reprobado".equalsIgnoreCase(fila.resultado());

        return esRegularidad && esReprobado;
    }

    private void procesarFilaSegunTipo(DatosFila fila, Materia materia, HistoriaAcademica historia, List<Renglon> acumulador) {
        Renglon renglon = Renglon.builder()
                .fecha(fila.fecha())
                .tipo(fila.tipo()) // Normalizamos luego si es necesario
                .nota(fila.nota())
                .resultado(fila.resultado())
                .materia(materia)
                .historiaAcademica(historia)
                .build();

        String tipoNormalizado = fila.tipo().toLowerCase();

        switch (tipoNormalizado) {
            case "examen":
            case "equivalencia": // Equivalencia con nota se trata como examen
                if (fila.nota() != null) {
                    Examen examen = Examen.builder()
                            .fecha(fila.fecha())
                            .nota(fila.nota())
                            .renglon(renglon)
                            .build();
                    renglon.setExamen(examen);
                    // Regla: Si aprobó examen, borramos regularidades previas de la lista
                    if (fila.nota() >= 4.0) {
                        eliminarRegularidadesDeLaMateria(acumulador, materia);
                    }
                }
                acumulador.add(renglon);
                break;

            case "promocion":
            case "aprobres": // Aprobación por resolución
                renglon.setTipo("Promocion"); // Unificamos nombre
                // Regla: Promoción mata regularidad
                eliminarRegularidadesDeLaMateria(acumulador, materia);
                acumulador.add(renglon);
                break;

            case "regularidad":
                // Solo agregamos si NO existe ya una aprobación (examen o promo) para esta materia
                if (!tieneAprobacionRegistrada(acumulador, materia)) {
                    acumulador.add(renglon);
                }
                break;
        }
    }

    private void mergeRenglones(HistoriaAcademica historia, List<Renglon> nuevos) {
        // Estrategia simple: Reemplazo inteligente o Append
        // Para este Refactor, asumimos una estrategia de "Actualización Incremental":
        // Si el renglón ya existe (misma fecha, materia y tipo), no lo duplicamos.
        
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

    private void eliminarRegularidadesDeLaMateria(List<Renglon> lista, Materia materia) {
        lista.removeIf(r -> 
            r.getMateria().equals(materia) && "Regularidad".equalsIgnoreCase(r.getTipo())
        );
    }

    private boolean tieneAprobacionRegistrada(List<Renglon> lista, Materia materia) {
        return lista.stream().anyMatch(r -> 
            r.getMateria().equals(materia) && 
            (
                ("Promocion".equalsIgnoreCase(r.getTipo())) ||
                (r.getNota() != null && r.getNota() >= 4.0)
            )
        );
    }

    private List<Renglon> eliminarDuplicadosLogicos(List<Renglon> renglones) {
        // Usamos un Set con una clave única temporal para filtrar duplicados exactos en el mismo archivo
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
package com.recommendationSys.Sistema_Recomendador_Finales.services.planEstudio;

import com.recommendationSys.Sistema_Recomendador_Finales.model.Correlativa;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Materia;
import com.recommendationSys.Sistema_Recomendador_Finales.model.PlanDeEstudio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CorrelativaProcessor {

    public List<Correlativa> generarCorrelativasConCache(
            String correlativasStr,
            Materia materia,
            Map<String, Materia> cacheMaterias,
            PlanDeEstudio plan) {

        if (correlativasStr == null || "No tiene".equalsIgnoreCase(correlativasStr)) {
            return Collections.emptyList();
        }

        return Arrays.stream(correlativasStr.split("-"))
                .map(String::trim)
                .filter(codigo -> !codigo.isEmpty())
                .map(cacheMaterias::get)
                .filter(Objects::nonNull)
                .map(correlativa -> Correlativa.builder()
                        .materia(materia)
                        .correlativaCodigo(correlativa)
                        .build())
                .toList();
    }
}
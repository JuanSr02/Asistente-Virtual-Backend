package com.asistenteVirtual.services.planEstudio;

import com.asistenteVirtual.model.Correlativa;
import com.asistenteVirtual.model.Materia;
import com.asistenteVirtual.model.PlanDeEstudio;
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
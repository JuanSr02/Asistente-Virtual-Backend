package com.recommendationSys.Sistema_Recomendador_Finales.services.planEstudio;

import com.recommendationSys.Sistema_Recomendador_Finales.model.Correlativa;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Materia;
import com.recommendationSys.Sistema_Recomendador_Finales.model.PlanDeEstudio;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.CorrelativaRepository;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.MateriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class CorrelativaProcessor {

    private final MateriaRepository materiaRepo;
    private final CorrelativaRepository correlativaRepo;

    public void procesarCorrelativas(String correlativasStr, Materia materia, PlanDeEstudio plan) {
        if (correlativasStr == null || "No tiene".equalsIgnoreCase(correlativasStr)) {
            return;
        }

        Arrays.stream(correlativasStr.split("-"))
                .map(String::trim)
                .filter(codigo -> !codigo.isEmpty())
                .forEach(codigo -> procesarCorrelativa(codigo, materia, plan));
    }

    private void procesarCorrelativa(String codigoCorrelativa, Materia materia, PlanDeEstudio plan) {
        materiaRepo.findById(codigoCorrelativa)
                .ifPresent(correlativa -> {
                    Correlativa nuevaCorrelativa = Correlativa.builder()
                            .materia(materia)
                            .correlativaCodigo(correlativa)
                            .planDeEstudio(plan)
                            .build();
                    correlativaRepo.save(nuevaCorrelativa);
                });
    }
}
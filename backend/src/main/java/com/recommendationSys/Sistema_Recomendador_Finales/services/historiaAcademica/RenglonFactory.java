package com.recommendationSys.Sistema_Recomendador_Finales.services.historiaAcademica;

import com.recommendationSys.Sistema_Recomendador_Finales.model.HistoriaAcademica;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Materia;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Renglon;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class RenglonFactory {
    public Renglon crearRenglon(LocalDate fecha, String tipo, Double nota,
                                String resultado, HistoriaAcademica historia, Materia materia) {
        return Renglon.builder()
                .fecha(fecha)
                .tipo(tipo)
                .nota(nota)
                .resultado(resultado)
                .historiaAcademica(historia)
                .materia(materia)
                .build();
    }
}

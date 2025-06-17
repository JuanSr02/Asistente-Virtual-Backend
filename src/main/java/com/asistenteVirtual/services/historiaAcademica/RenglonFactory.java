package com.asistenteVirtual.services.historiaAcademica;

import com.asistenteVirtual.model.HistoriaAcademica;
import com.asistenteVirtual.model.Materia;
import com.asistenteVirtual.model.Renglon;
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

package com.asistenteVirtual.services.rankingFinales;

import com.asistenteVirtual.DTOs.FinalDTO;
import com.asistenteVirtual.DTOs.OrdenFinales;
import com.asistenteVirtual.model.HistoriaAcademica;

import java.util.List;

public interface FinalesCalculator {
    List<FinalDTO> calcularFinalesParaRendir(HistoriaAcademica historia, OrdenFinales orden);
}
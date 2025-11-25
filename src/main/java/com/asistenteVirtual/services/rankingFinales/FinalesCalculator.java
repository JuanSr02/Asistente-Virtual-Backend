package com.asistenteVirtual.services.rankingFinales;

import com.asistenteVirtual.DTOs.FinalDTO;
import com.asistenteVirtual.DTOs.OrdenFinales;
import com.asistenteVirtual.modules.historiaAcademica.model.HistoriaAcademica;
import com.asistenteVirtual.modules.historiaAcademica.model.Renglon;

import java.util.List;

public interface FinalesCalculator {
    List<FinalDTO> calcularFinalesParaRendir(HistoriaAcademica historia, OrdenFinales orden);

    List<Renglon> obtenerRegularesAprobadasHabilitadas(HistoriaAcademica historia);

    List<FinalDTO> mapearARenglonDTO(List<Renglon> regularesAprobadas);
}
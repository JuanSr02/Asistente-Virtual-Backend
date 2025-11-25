package com.asistenteVirtual.services.rankingFinales;

import com.asistenteVirtual.DTOs.FinalDTO;
import com.asistenteVirtual.DTOs.OrdenFinales;
import com.asistenteVirtual.modules.planEstudio.dto.MateriaResponse;

import java.util.List;

public interface RankingFinalesService {
    List<FinalDTO> obtenerFinalesParaRendir(Long estudianteId, OrdenFinales orden);

    List<MateriaResponse> obtenerFinalesParaInscribirse(Long estudianteId);
}


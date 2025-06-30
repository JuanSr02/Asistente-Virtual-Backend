package com.asistenteVirtual.services.rankingFinales;

import com.asistenteVirtual.DTOs.FinalDTO;
import com.asistenteVirtual.DTOs.MateriaDTO;
import com.asistenteVirtual.DTOs.OrdenFinales;

import java.util.List;

public interface RankingFinalesService {
    List<FinalDTO> obtenerFinalesParaRendir(Long estudianteId, OrdenFinales orden);

    List<MateriaDTO> obtenerFinalesParaInscribirse(Long estudianteId);
}


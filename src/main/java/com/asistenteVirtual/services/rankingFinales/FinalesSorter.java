package com.asistenteVirtual.services.rankingFinales;

import com.asistenteVirtual.DTOs.FinalDTO;
import com.asistenteVirtual.DTOs.OrdenFinales;

import java.util.List;

public interface FinalesSorter {
    void ordenarFinales(List<FinalDTO> finales, OrdenFinales orden);
}
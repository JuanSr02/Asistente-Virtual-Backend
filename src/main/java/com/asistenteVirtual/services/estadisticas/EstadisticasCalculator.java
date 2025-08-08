package com.asistenteVirtual.services.estadisticas;

import com.asistenteVirtual.DTOs.EstadisticasMateriaDTO;
import com.asistenteVirtual.model.EstadisticasMateria;

public interface EstadisticasCalculator {
    EstadisticasMateria obtenerEstadisticasMateriaUnica(String materia, String plan);

    EstadisticasMateriaDTO obtenerEstadisticasSuperMateria(String materia);
}
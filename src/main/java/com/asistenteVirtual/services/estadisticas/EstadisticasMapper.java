package com.asistenteVirtual.services.estadisticas;

import com.asistenteVirtual.DTOs.EstadisticasMateriaDTO;
import com.asistenteVirtual.model.EstadisticasMateria;

public interface EstadisticasMapper {
    EstadisticasMateriaDTO convertToDTO(EstadisticasMateria stats);
}
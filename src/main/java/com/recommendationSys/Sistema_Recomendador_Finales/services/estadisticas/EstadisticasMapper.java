package com.recommendationSys.Sistema_Recomendador_Finales.services.estadisticas;

import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.EstadisticasMateriaDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.model.EstadisticasMateria;

public interface EstadisticasMapper {
    EstadisticasMateriaDTO convertToDTO(EstadisticasMateria stats);
}
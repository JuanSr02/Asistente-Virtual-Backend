package com.recommendationSys.Sistema_Recomendador_Finales.services.experiencia;

import com.recommendationSys.Sistema_Recomendador_Finales.model.Experiencia;

import java.util.List;

public interface ExperienciaQueryService {
    List<Experiencia> obtenerExperienciasPorMateria(String codigoMateria,String plan);
}

package com.recommendationSys.Sistema_Recomendador_Finales.services.inscripciones;

import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.RegistroInscripcionDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.model.RegistroInscripcion;

public interface InscripcionNotificationService {
    void notificarCompaneros(RegistroInscripcion nuevaInscripcion);
    void notificarCompanerosDTO(RegistroInscripcionDTO nuevaInscripcion);
}


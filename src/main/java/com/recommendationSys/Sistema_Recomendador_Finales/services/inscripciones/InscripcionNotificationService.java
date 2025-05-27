package com.recommendationSys.Sistema_Recomendador_Finales.services.inscripciones;

import com.recommendationSys.Sistema_Recomendador_Finales.model.RegistroInscripcion;

public interface InscripcionNotificationService {
    void notificarCompaneros(RegistroInscripcion nuevaInscripcion);
}


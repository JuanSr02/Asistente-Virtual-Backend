package com.asistenteVirtual.services.inscripciones;

import com.asistenteVirtual.DTOs.RegistroInscripcionDTO;
import com.asistenteVirtual.model.RegistroInscripcion;

public interface InscripcionNotificationService {
    void notificarCompaneros(RegistroInscripcion nuevaInscripcion);
    void notificarCompanerosDTO(RegistroInscripcionDTO nuevaInscripcion);
}


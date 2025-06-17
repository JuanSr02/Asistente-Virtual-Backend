package com.asistenteVirtual.services.email;

import com.asistenteVirtual.model.Estudiante;

/**
 * Datos necesarios para enviar una notificación de nuevo inscripto.
 */
public record NotificationData(
        String emailDestinatario,
        String materiaNombre,
        String turno,
        String anio,
        Estudiante companero
) {

    /**
     * Constructor que valida los datos requeridos.
     */
    public NotificationData {
        validateNotificationData(emailDestinatario, materiaNombre, turno, anio, companero);
    }

    private void validateNotificationData(String email, String materia, String turno,
                                          String anio, Estudiante estudiante) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El email destinatario no puede estar vacío");
        }

        if (materia == null || materia.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la materia no puede estar vacío");
        }

        if (turno == null || turno.trim().isEmpty()) {
            throw new IllegalArgumentException("El turno no puede estar vacío");
        }

        if (anio == null || anio.trim().isEmpty()) {
            throw new IllegalArgumentException("El año no puede estar vacío");
        }

        if (estudiante == null) {
            throw new IllegalArgumentException("Los datos del estudiante son requeridos");
        }

        if (estudiante.getNombreApellido() == null || estudiante.getNombreApellido().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del estudiante no puede estar vacío");
        }

        if (estudiante.getMail() == null || estudiante.getMail().trim().isEmpty()) {
            throw new IllegalArgumentException("El email del estudiante no puede estar vacío");
        }
    }
}

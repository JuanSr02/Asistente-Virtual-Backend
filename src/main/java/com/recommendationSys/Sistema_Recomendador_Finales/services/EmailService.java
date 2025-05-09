package com.recommendationSys.Sistema_Recomendador_Finales.services;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Async  // Ejecución asíncrona
    public void enviarEmailNotificacion(String destinatario, String materiaNombre, String turno) {
        String asunto = "Nuevo inscripto en " + materiaNombre;
        String cuerpo = String.format(
                "Se ha registrado un nuevo compañero en %s (turno %s).",
                materiaNombre, turno);

        // Implementación real con JavaMailSender o servicio externo
        System.out.println("Enviando email a: " + destinatario);
        System.out.println("Asunto: " + asunto);
        System.out.println("Cuerpo: " + cuerpo);

        // En producción usarías:
        // javaMailSender.send(mimeMessagePreparator);
    }
}
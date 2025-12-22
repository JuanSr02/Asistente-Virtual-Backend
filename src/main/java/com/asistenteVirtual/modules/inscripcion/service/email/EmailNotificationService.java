package com.asistenteVirtual.modules.inscripcion.service.email;

import com.asistenteVirtual.modules.estudiante.model.Estudiante;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class EmailNotificationService {

    private final ResendEmailService resendService;
    private final EmailTemplateGenerator templateGenerator;

    public EmailNotificationService(ResendEmailService resendService,
                                    EmailTemplateGenerator templateGenerator) {
        this.resendService = resendService;
        this.templateGenerator = templateGenerator;
    }

    @Async
    public CompletableFuture<Void> enviarNotificacionNuevoInscripto(
            String emailDestinatario,
            String materiaNombre,
            String turno,
            String anio,
            Estudiante companero) {

        try {
            NotificationData data = new NotificationData(
                    emailDestinatario, materiaNombre, turno, anio, companero
            );

            String subject = templateGenerator.generateSubject(data);
            String htmlContent = templateGenerator.generateTemplate(data);

            // Usamos el servicio de Resend (HTTP)
            resendService.enviarCorreo(emailDestinatario, subject, htmlContent);

            return CompletableFuture.completedFuture(null);

        } catch (Exception e) {
            // Logueamos pero no rompemos la ejecución async
            System.err.println("Error enviando notificación: " + e.getMessage());
            return CompletableFuture.completedFuture(null);
        }
    }
}
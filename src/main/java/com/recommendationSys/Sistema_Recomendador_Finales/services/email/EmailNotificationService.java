package com.recommendationSys.Sistema_Recomendador_Finales.services.email;

import com.recommendationSys.Sistema_Recomendador_Finales.exceptions.EmailException;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Estudiante;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Servicio para el envío de emails relacionados con el sistema de recomendaciones.
 * Implementa el principio de responsabilidad única (SRP) y utiliza inyección de dependencias.
 */
@Service
public class EmailNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationService.class);
    private static final String DEFAULT_FROM_ADDRESS = "noreply@sistema-recomendador.com";
    private static final String UTF_8_ENCODING = "UTF-8";

    private final JavaMailSender mailSender;
    private final Environment environment;
    private final EmailTemplateGenerator templateGenerator;

    public EmailNotificationService(JavaMailSender mailSender,
                                    Environment environment,
                                    EmailTemplateGenerator templateGenerator) {
        this.mailSender = mailSender;
        this.environment = environment;
        this.templateGenerator = templateGenerator;
    }

    /**
     * Envía una notificación asíncrona sobre un nuevo estudiante inscrito.
     *
     * @param emailDestinatario email del destinatario
     * @param materiaNombre nombre de la materia
     * @param turno turno del examen
     * @param anio año del examen
     * @param companero datos del nuevo estudiante
     * @return CompletableFuture que se completa cuando el email es enviado
     * @throws EmailException si ocurre un error durante el envío
     */
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

            MimeMessage message = createMimeMessage(data);
            mailSender.send(message);

            logger.info("Email de notificación enviado exitosamente a: {} para materia: {}",
                    emailDestinatario, materiaNombre);

            return CompletableFuture.completedFuture(null);

        } catch (Exception e) {
            logger.error("Error al enviar email de notificación a: {} para materia: {}. Error: {}",
                    emailDestinatario, materiaNombre, e.getMessage(), e);
            throw new EmailException("Error al enviar email de notificación", e.getMessage());
        }
    }

    /**
     * Crea y configura un mensaje MIME para el email.
     *
     * @param data datos de la notificación
     * @return mensaje MIME configurado
     * @throws Exception si ocurre un error durante la creación del mensaje
     */
    private MimeMessage createMimeMessage(NotificationData data) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING);

        String fromAddress = environment.getProperty("mail.from", DEFAULT_FROM_ADDRESS);
        String subject = templateGenerator.generateSubject(data);
        String htmlContent = templateGenerator.generateTemplate(data);

        helper.setFrom(fromAddress);
        helper.setTo(data.emailDestinatario());
        helper.setSubject(subject);
        helper.setText(htmlContent, true); // true = HTML content

        return message;
    }
}
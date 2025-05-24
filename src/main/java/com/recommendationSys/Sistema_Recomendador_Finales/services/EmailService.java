package com.recommendationSys.Sistema_Recomendador_Finales.services;

import com.recommendationSys.Sistema_Recomendador_Finales.exceptions.EmailException;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Estudiante;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final Environment env;

    public EmailService(JavaMailSender mailSender, Environment env) {
        this.mailSender = mailSender;
        this.env = env;
    }

    @Async
    public void enviarNotificacionNuevoInscripto(String emailDestinatario, String materiaNombre, String turno,String anio, Estudiante companero) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String asunto = String.format("[%s] Nuevo compañero en %s - Turno %s",
                    env.getProperty("app.name"), materiaNombre, turno);

            String cuerpo = plantillaEmail(materiaNombre, turno,anio,companero.getNombreApellido(),companero.getMail());

            helper.setFrom("Sistema_Recomendador");
            helper.setTo(emailDestinatario);
            helper.setSubject(asunto);
            helper.setText(cuerpo, true); // true = HTML

            mailSender.send(message);
        } catch (Exception e) {
            throw new EmailException("Error al enviar email de notificación",e.toString());
        }
    }

    private String plantillaEmail(String materia, String turno,String anio,String nombre,String mail) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; }
                    .container { max-width: 600px; margin: 0 auto; }
                    .header { background-color: #f8f9fa; padding: 20px; text-align: center; }
                    .content { padding: 20px; }
                    .footer { margin-top: 20px; font-size: 12px; color: #6c757d; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h2>Nuevo estudiante que va a rendir con vos</h2>
                    </div>
                    <div class="content">
                        <p>Se ha inscripto un nuevo estudiante en:</p>
                        <ul>
                            <li><strong>Materia:</strong> %s</li>
                            <li><strong>Turno:</strong> %s</li>
                            <li><strong>Año:</strong> %s</li>
                            <li><strong>Nombre:</strong> %s</li>
                            <li><strong>Mail:</strong> %s</li>
                        </ul>
                    </div>
                    <div class="footer">
                        <p>Este es un mensaje automatico, por favor no respondas.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(materia,turno,anio,nombre,mail);
    }

}
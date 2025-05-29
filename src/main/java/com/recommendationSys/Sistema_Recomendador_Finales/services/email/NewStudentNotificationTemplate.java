package com.recommendationSys.Sistema_Recomendador_Finales.services.email;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Implementación del generador de plantillas para notificaciones de nuevos inscriptos.
 */
@Component
public class NewStudentNotificationTemplate implements EmailTemplateGenerator {

    private final Environment environment;

    public NewStudentNotificationTemplate(Environment environment) {
        this.environment = environment;
    }

    @Override
    public String generateSubject(NotificationData data) {
        String appName = environment.getProperty("app.name", "Sistema Recomendador");
        return String.format("[%s] Nuevo compañero en %s - Turno %s",
                appName, data.materiaNombre(), data.turno());
    }

    @Override
    public String generateTemplate(NotificationData data) {
        return """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Notificación - Nuevo Estudiante</title>
                <style>
                    body { 
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; 
                        line-height: 1.6;
                        color: #333;
                        margin: 0;
                        padding: 0;
                        background-color: #f4f4f4;
                    }
                    .container { 
                        max-width: 600px; 
                        margin: 20px auto; 
                        background-color: white;
                        border-radius: 8px;
                        box-shadow: 0 0 10px rgba(0,0,0,0.1);
                        overflow: hidden;
                    }
                    .header { 
                        background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                        color: white;
                        padding: 30px 20px; 
                        text-align: center; 
                    }
                    .header h2 {
                        margin: 0;
                        font-size: 24px;
                        font-weight: 300;
                    }
                    .content { 
                        padding: 30px; 
                    }
                    .info-card {
                        background-color: #f8f9fa;
                        border-left: 4px solid #667eea;
                        padding: 20px;
                        margin: 20px 0;
                        border-radius: 0 4px 4px 0;
                    }
                    .info-item {
                        margin: 10px 0;
                        display: grid;
                        grid-template-columns: 130px 1fr;
                        align-items: center;
                    }
                    .info-label {
                        font-weight: 600;
                        color: #495057;
                        min-width: 80px;
                    }
                    .info-value {
                        color: #212529;
                    }
                    .footer { 
                        background-color: #f8f9fa;
                        padding: 20px;
                        text-align: center;
                        font-size: 14px; 
                        color: #6c757d; 
                        border-top: 1px solid #dee2e6;
                    }
                    .highlight {
                        color: #667eea;
                        font-weight: 600;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h2>📚 Nuevo Compañero de Estudio</h2>
                    </div>
                    <div class="content">
                        <p>¡Hola! Te informamos que se ha inscrito un nuevo estudiante que rendirá en la misma fecha que tú.</p>
                        
                        <div class="info-card">
                            <div class="info-item">
                                <span class="info-label">📖 Materia:</span>
                                <span class="info-value highlight">%s</span>
                            </div>
                            <div class="info-item">
                                <span class="info-label">🕐 Turno:</span>
                                <span class="info-value">%s</span>
                            </div>
                            <div class="info-item">
                                <span class="info-label">📅 Año:</span>
                                <span class="info-value">%s</span>
                            </div>
                            <div class="info-item">
                                <span class="info-label">👤 Nombre:</span>
                                <span class="info-value highlight">%s</span>
                            </div>
                            <div class="info-item">
                                <span class="info-label">✉️ Email:</span>
                                <span class="info-value">%s</span>
                            </div>
                        </div>
                        
                        <p>¡Podrían contactarse para coordinar el estudio juntos!</p>
                    </div>
                    <div class="footer">
                        <p>🤖 Este es un mensaje automático del sistema. Por favor, no respondas a este email.</p>
                        <p>Si tienes alguna consulta, contacta al administrador del sistema.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                data.materiaNombre(),
                data.turno(),
                data.anio(),
                data.companero().getNombreApellido(),
                data.companero().getMail()
        );
    }
}

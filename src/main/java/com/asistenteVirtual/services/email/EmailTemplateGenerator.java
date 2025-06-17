package com.asistenteVirtual.services.email;

/**
 * Generador de plantillas HTML para emails.
 * Implementa el patrón Strategy para diferentes tipos de templates.
 */
public interface EmailTemplateGenerator {

    /**
     * Genera el contenido HTML del email basado en los datos proporcionados.
     *
     * @param data datos necesarios para generar el template
     * @return contenido HTML del email
     */
    String generateTemplate(NotificationData data);

    /**
     * Genera el asunto del email.
     *
     * @param data datos necesarios para generar el asunto
     * @return asunto del email
     */
    String generateSubject(NotificationData data);
}
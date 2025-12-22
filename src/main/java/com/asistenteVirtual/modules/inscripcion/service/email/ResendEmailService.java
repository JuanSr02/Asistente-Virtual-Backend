package com.asistenteVirtual.modules.inscripcion.service.email;

import com.asistenteVirtual.common.exceptions.EmailException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class ResendEmailService {

    @Value("${resend.api.key}")
    private String resendApiKey;

    @Value("${mail.from}")
    private String fromEmail;

    private final RestClient restClient;

    public ResendEmailService(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("https://api.resend.com")
                .defaultHeader("Authorization", "Bearer " + resendApiKey) // Se inyectará al ejecutar, no en el constructor si usas @Value así, ver nota abajo*
                .build();
    }

    public void enviarCorreo(String destinatario, String asunto, String htmlContent) {
        try {
            Map<String, Object> body = Map.of(
                    "from", "Asistente Virtual <" + fromEmail + ">",
                    "to", List.of(destinatario),
                    "subject", asunto,
                    "html", htmlContent
            );

            restClient.post()
                    .uri("/emails")
                    .header("Authorization", "Bearer " + resendApiKey) // Lo ponemos aquí para asegurar que lea el valor actualizado
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();

        } catch (Exception e) {
            throw new EmailException("Error enviando correo con Resend a " + destinatario, e.getMessage());
        }
    }
}
package com.asistenteVirtual.services.supabase;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SupabaseAuthService {

    @Value("${supabase.serviceRole.key}")
    private String serviceRoleKey;

    @Value("${supabase.url}")
    private String supabaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public void eliminarUsuarioSupabase(String userId) {
        String url = supabaseUrl + "/auth/v1/admin/users/" + userId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + serviceRoleKey);
        headers.set("apikey", serviceRoleKey); // también recomendable agregarlo
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                entity,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Error al eliminar el usuario de Supabase Auth: " + response.getBody());
        }
    }

    public void actualizarEmailSupabase(String userId, String nuevoEmail) {
        String url = supabaseUrl + "/auth/v1/admin/users/" + userId;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + serviceRoleKey);
        headers.set("apikey", serviceRoleKey);

        // JSON body con nuevo email
        String body = String.format("{\"email\": \"%s\"}", nuevoEmail);

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                entity,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Error al actualizar el email en Supabase Auth: " + response.getBody());
        }
    }

}


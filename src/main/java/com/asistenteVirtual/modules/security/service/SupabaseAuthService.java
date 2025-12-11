package com.asistenteVirtual.modules.security.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupabaseAuthService {

    @Value("${supabase.serviceRole.key}")
    private String serviceRoleKey;

    @Value("${supabase.url}")
    private String supabaseUrl;

    // Inyectamos el builder para que Spring lo autoconfigure (timeout, etc.)
    private final RestClient.Builder restClientBuilder;

    /**
     * Elimina un usuario de Supabase Auth.
     */
    public void eliminarUsuarioSupabase(String userId) {
        RestClient client = buildClient();

        client.delete()
                .uri("/auth/v1/admin/users/{id}", userId)
                .retrieve()
                .onStatus(status -> status.isError(), (request, response) -> {
                    throw new RuntimeException("Error eliminando usuario Supabase: " + response.getStatusCode());
                })
                .toBodilessEntity();

    }

    /**
     * Actualiza email o password de un usuario.
     */
    public void actualizarUsuarioSupabase(String userId, String nuevoEmail, String nuevaPassword) {
        var updateRequest = new SupabaseUserUpdate(nuevoEmail, nuevaPassword);
        RestClient client = buildClient();

        client.put()
                .uri("/auth/v1/admin/users/{id}", userId)
                .body(updateRequest)
                .retrieve()
                .onStatus(status -> status.isError(), (request, response) -> {
                    throw new RuntimeException("Error actualizando usuario Supabase: " + response.getStatusCode());
                })
                .toBodilessEntity();

    }

    // Helper para construir el cliente con headers comunes
    private RestClient buildClient() {
        return restClientBuilder
                .baseUrl(supabaseUrl)
                .defaultHeader("Authorization", "Bearer " + serviceRoleKey)
                .defaultHeader("apikey", serviceRoleKey)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    // Record interno para el DTO (Java 21) - Elimina la concatenación de Strings manual
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private record SupabaseUserUpdate(String email, String password) {
    }
}
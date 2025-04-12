package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import services.SupabaseAuthService;
import model.Persona;
import model.Estudiante;
import model.Administrador;
import repository.PersonaRepository;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private SupabaseAuthService supabaseAuthService;

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private WebClient supabaseWebClient;

    /**
     * Endpoint para registrar un nuevo usuario
     */
    @PostMapping("/signup")
    public Mono<ResponseEntity<JsonNode>> signUp(@RequestBody Map<String, Object> request) {
        String email = (String) request.get("email");
        String password = (String) request.get("password");
        String tipo = (String) request.get("tipo");

        // Datos adicionales para Supabase (metadatos de usuario)
        Map<String, Object> userData = Map.of(
                "tipo", tipo,
                "nombre_apellido", request.get("nombreApellido"),
                "dni", request.get("dni"),
                "nroRegistro", tipo.equals("ESTUDIANTE") ? request.get("nroRegistro") : null,
                "token", tipo.equals("ADMINISTRADOR") ? request.get("token") : null
        );

        return supabaseAuthService.signUp(email, password, userData)
                .map(response -> {
                    // Resto del código para crear el usuario en tu base de datos
                    // ...

                    return ResponseEntity.ok(response);
                });
    }
    /**
     * Endpoint para iniciar sesión
     */
    @PostMapping("/signin")
    public Mono<ResponseEntity<JsonNode>> signIn(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        return supabaseAuthService.signIn(email, password)
                .map(ResponseEntity::ok);
    }

    /**
     * Endpoint para cerrar sesión
     */
    @PostMapping("/signout")
    public Mono<ResponseEntity<Void>> signOut(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7); // Quitar "Bearer "

        return supabaseAuthService.signOut(token)
                .then(Mono.just(ResponseEntity.ok().<Void>build()));
    }

    /**
     * Endpoint para obtener los datos del usuario actual
     */
    @GetMapping("/me")
    public Mono<ResponseEntity<JsonNode>> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7); // Quitar "Bearer "

        return supabaseAuthService.getUserData(token)
                .map(ResponseEntity::ok);
    }

    /**
     * Actualiza los metadatos de un usuario
     * @param token Token de acceso
     * @param metadata Nuevos metadatos
     * @return Respuesta de la actualización
     */
    public Mono<JsonNode> updateUserMetadata(String token, Map<String, Object> metadata) {
        return supabaseWebClient.put()
                .uri("/auth/v1/user")
                .header("Authorization", "Bearer " + token)
                .bodyValue(Map.of("data", metadata))
                .retrieve()
                .bodyToMono(JsonNode.class);
    }

}


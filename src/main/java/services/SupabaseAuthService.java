package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Map;

@Service
public class SupabaseAuthService {

    @Autowired
    private WebClient supabaseWebClient;

    @Value("K5exmS8udlct4uOwA0i5aQebd92aWsqOOOKKqEhRTLuZRwuQ6yDFR0cvCjuK2a74TXVendg5We4H3U/CM95ySQ==")
    private String jwtSecret;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Verifica un token JWT de Supabase
     * @param token El token JWT a verificar
     * @return Las claims del token si es válido
     */
    public Claims verifyToken(String token) {
        try {
            // Decodificar la clave secreta de JWT de base64
            byte[] decodedKey = Base64.getDecoder().decode(jwtSecret);
            Key key = Keys.hmacShaKeyFor(decodedKey);

            // Verificar y parsear el token
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido o expirado", e);
        }
    }

    /**
     * Obtiene los datos del usuario desde Supabase
     * @param token El token de acceso
     * @return Los datos del usuario
     */
    public Mono<JsonNode> getUserData(String token) {
        return supabaseWebClient.get()
                .uri("/auth/v1/user")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(JsonNode.class);
    }

    /**
     * Registra un nuevo usuario en Supabase
     * @param email Email del usuario
     * @param password Contraseña del usuario
     * @param userData Datos adicionales del usuario
     * @return Respuesta del registro
     */
    public Mono<JsonNode> signUp(String email, String password, Map<String, Object> userData) {
        Map<String, Object> requestBody = Map.of(
                "email", email,
                "password", password,
                "data", userData
        );

        return supabaseWebClient.post()
                .uri("/auth/v1/signup")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(JsonNode.class);
    }

    /**
     * Inicia sesión con email y contraseña
     * @param email Email del usuario
     * @param password Contraseña del usuario
     * @return Respuesta del inicio de sesión
     */
    public Mono<JsonNode> signIn(String email, String password) {
        Map<String, Object> requestBody = Map.of(
                "email", email,
                "password", password
        );

        return supabaseWebClient.post()
                .uri("/auth/v1/token?grant_type=password")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(JsonNode.class);
    }

    /**
     * Cierra la sesión del usuario
     * @param token Token de acceso
     * @return Respuesta del cierre de sesión
     */
    public Mono<Void> signOut(String token) {
        return supabaseWebClient.post()
                .uri("/auth/v1/logout")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(Void.class);
    }
}

